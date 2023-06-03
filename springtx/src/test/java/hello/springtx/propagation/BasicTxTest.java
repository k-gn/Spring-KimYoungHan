package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    /*
        - 같은 커넥션을 쓴다고 놀라면 안된다.
        - tx1 이 커넥션을 사용 후 풀에 반납하고 나서, tx2 가 다시 커넥션을 획득한 것이다.
        - 따라서 서로 완전히 다른 커넥션이라 생각하면 된다.
        - 히카리 커넥션 객체 주소를 통해 다름을 확인할 순 있다.

        - 각각 독립된 커넥션의 트랜잭션은 서로 영향이 없다.
            - 트랜잭션은 하나의 Connection을 가져와 사용하다가 닫는 사이에 일어난다. (즉, 한 트랜잭션의 시작과 종료는 한 커넥션 객체에 의해 이루어진다)
     */
    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        txManager.commit(tx2);
    }

    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManager.rollback(tx2);
    }

    /*
        # 트랜잭션 전파 (Propagation)
        - 각각 사용하는 게 아닌 중첩된 트랜잭션 수행
        - 스프링은 다양한 전파 옵션을 제공한다.
            - 기본적으로 @Transactional 이 적용되어 있다면 REQUIRED 전파 옵션을 사용한다.
            - 기본적으로 스프링은 외부와 내부 트랜잭션을 묶어 하나의 트랜잭션을 만들어준다.
            - 내부 트랜잭션이 진행중인 외부 트랜잭션에 참여하는 것이며, 이게 기본 동작이다.

        - 스프링은 이해를 돕기 위해 논리 트랜잭션과 물리 트랜잭션 이라는 개념을 나눈다.
            - 논리 트랜잭션은 하나의 물리 트랜잭션으로 묶인다.
            - 물리 트랜잭션은 우리가 이해하는 실제 데이터베이스에 적용되는 트랜잭션이다.
            - 논리 트랜잭션은 트랜잭션 매니저를 통해 사용하는 단위이다.
            - 논리 트랜잭션 개념은 트랜잭션이 진행되는 중에 내부에 추가로 트랜잭션을 사용하는 경우에 나타난다.

        - 모든 논리 트랜잭션이 커밋되어야 물리 트랜잭션이 커밋된다.
        - 하나의 논리 트랜잭션이라도 롤백되면 물리 트랜잭션은 롤백된다.
        - 외부든 내부든 전부 논리 트랜잭션이고 하나로 묶은 게 물리 트랜잭션이다.
     */
    @Test
    void inner_commit() {
        // 신규 트랜잭션을 시작한 외부 트랜잭션이 실제 물리 트랜잭션을 관리한다.
        // 신규 트랜잭션 시작 (커넥션 생성)
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        // 내부 트랜잭션은 그저 외부 트랜잭션을 자연스럽게 사용한다. (기존 커넥션 사용)
        log.info("내부 트랜잭션 시작"); // 기존 트랜잭션에 참여
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner); // 실제로 여기선 아무것도 안한다.

        log.info("외부 트랜잭션 커밋");
        // 신규 트랜잭션인 경우에만 커밋과 롤백을 수행한다.
        txManager.commit(outer); // 이 때 실제 커밋이 일어난다.
    }

    @Test
    void outer_rollback() {
        // 논리 트랜잭션이 하나라도 롤백되면 전체 물리 트랜잭션이 롤백된다.
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer); // 실제 롤백 된다.
    }

    @Test
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner); // marking rollback-only (롤백 표시)

        log.info("외부 트랜잭션 커밋"); // rollback-only 확인 후 롤백 처리, 추가로 이땐 예외를 던진다.
        // 시스템 입장에선 커밋을 호출했는데 롤백이 된 상황 -> 기대하지 않은 롤백이기 때문에 예외로 명확히 알려준다.
        // 스프링이 이렇게 명확하게 알려주는 것 처럼 개발에 있어서 모호함을 줄이는 것이 정말 중요하다.
        assertThatThrownBy(() -> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    /*
        # REQUIRES_NEW
        - 외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 사용할 수 있다.
        - 각각 별도의 물리 트랜잭션으로 사용하는 방법이다. (각각의 db 커넥션을 사용한다는 뜻)
        - 내부 트랜잭션의 문제가 발생해 롤백해도, 외부 트랜잭션에 영향을 주지 않는다. (반대도 동일)

        - 잘못쓰면 위험하다.
            - 커넥션을 동시에 2개 사용하기 때문에
            - 커넥션이 빨리 고갈될 수 있다.
            - 하나의 http 요청에 2개의 커넥션을 가져가기 때문에 성능 이슈가 있다.
            - REQUIRES_NEW를 사용하지 않고 문제를 해결할 수 있는 방법이 있다면 그 방법을 선택하는 것이 좋다.
                - ex. facade pattern 을 활용하여 순차적으로 트랜잭션을 사용하기

        - 실무에선 REQUIRES / REQUIRES_NEW 옵션 두개를 주로 사용한다.
     */
    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); //true

        log.info("내부 트랜잭션 시작");
        // REQUIRES_NEW 설정 (기존 트랜잭션을 무시하고 자신만의 신규 트랜잭션을 만든다)
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 외부의 트랜잭션을 잠시 미뤄두고 자신의 트랜잭션을 실행한다. (완전히 독립된 영역)
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); //true

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner); //롤백

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer); //커밋
    }





}
