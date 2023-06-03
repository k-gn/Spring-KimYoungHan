package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService service;

    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedException() {
        Assertions.assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(() -> service.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    /*
        - 예외 발생 시 스프링 트랜잭션의 기본정책
            - UnChecked 예외인 RuntimeException, Error 와 그 하위 예외가 발생하면 롤백 (복구 불가능한 예외)
            - Checked 예외인 Exception 과 그 하위 예외들은 커밋 

        - rollbackFor
            - 어떤 예외가 발생할 때 롤백할 지 지정할 수 있다.
            - 따라서 Exception 발생 시 롤백시킬 수도 있다.
            - 반대 특성인 noRollbackFor 도 있다.
            
        - 체크 예외 : 비즈니스 의미가 있을 때 사용
        - 언체크 예외 : 복구 불가능한 예외
        - ex.
            # 비즈니스 요구사항
                - 주문을 하는데 상황에 따라 다음과 같이 조치한다.
                1. 정상: 주문시 결제를 성공하면 주문 데이터를 저장하고 결제 상태를 완료 로 처리한다.
                2. 시스템 예외: 주문시 내부에 복구 불가능한 예외가 발생하면 전체 데이터를 롤백한다.
                3. 비즈니스 예외: 주문시 결제 잔고가 부족하면 주문 데이터를 저장하고, 결제 상태를 대기로 처리한다.
                    추가로 고객에게 잔고 부족을 알리고 별도의 계죄로 입금하도록 안내한다.
            - 시스템은 정상동작 했지만 비즈니스 문제 발생 -> 이런 경우가 비즈니스 상황에서 발생한 예외이다.
     */
    @Slf4j
    static class RollbackService {

        //런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        //체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        //체크 예외 rollbackFor 지정: 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }
    }

    static class MyException extends Exception {
    }

}

/*
    # isolation
        - 트랜잭션 격리 수준을 지정할 수 있다.
        - 기본 값은 데이터베이스에서 설정한 트랜잭션 격리 수준을 사용하는 DEFAULT 이다.
        - 대부분 데이터베이스에서 설정한 기준을 따른며, 애플리케이션 개발자가 트랜잭션 격리 수준을 직접 지정하는 경우는 드물다.

        DEFAULT : 데이터베이스에서 설정한 격리 수준을 따른다.
        READ_UNCOMMITTED : 커밋되지 않은 읽기
        READ_COMMITTED : 커밋된 읽기
        REPEATABLE_READ : 반복 가능한 읽기
        SERIALIZABLE : 직렬화 가능

    # timeout
        - 트랜잭션 수행시간에 대한 타임아웃을 초단위로 지정

    ## readOnly
        - 트랜잭션은 기본적으로 읽기 쓰기가 모두 가능한 트랜잭션이 생성된다.
        - readOnly=true 옵션을 사용하면 읽기 전용 트랜잭션이 생성된다. 이 경우 등록, 수정, 삭제가 안되고 읽기
          기능만 작동한다. (드라이버나 데이터베이스에 따라 정상 동작하지 않는 경우도 있다.) 그리고 readOnly
          옵션을 사용하면 읽기에서 다양한 성능 최적화가 발생할 수 있다.

        - 주로 3곳에서 활용한다.
        1. 프레임워크
            - JdbcTemplate은 읽기 전용 트랜잭션 안에서 변경 기능을 실행하면 예외를 던진다.
            - JPA(하이버네이트)는 읽기 전용 트랜잭션의 경우 커밋 시점에 플러시를 호출하지 않는다.
            - 읽기 전용이니 변경에 사용되는 플러시를 호출할 필요가 없다.
            - 추가로 변경이 필요 없으니 변경 감지를 위한 스냅샷 객체도 생성하지 않는다. 이렇게 JPA에서는 다양한 최적화가 발생한다.
        2. JDBC 드라이버
            - 참고로 여기서 설명하는 내용들은 DB와 드라이버 버전에 따라서 다르게 동작하기 때문에 사전에 확인이 필요하다.
            - 읽기 전용 트랜잭션에서 변경 쿼리가 발생하면 예외를 던진다.
            - 읽기, 쓰기(마스터, 슬레이브) 데이터베이스를 구분해서 요청한다.
            - 읽기 전용 트랜잭션의 경우 읽기 (슬레이브) 데이터베이스의 커넥션을 획득해서 사용한다.
        3. 데이터베이스
            - 데이터베이스에 따라 읽기 전용 트랜잭션의 경우 읽기만 하면 되므로, 내부에서 성능 최적화가 발생한다
 */