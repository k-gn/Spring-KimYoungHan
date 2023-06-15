package hello;

import hello.order.gauge.StockConfigV1;
import hello.order.gauge.StockConfigV2;
import hello.order.v0.OrderConfigV0;
import hello.order.v1.OrderConfigV1;
import hello.order.v2.OrderConfigV2;
import hello.order.v3.OrderConfigV3;
import hello.order.v4.OrderConfigV4;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/*
    - 운영환경에선 모니터링이 굉장히 중요하다.
    - 프로덕션 준비 기능
        - 지표, 추적, 감시
        - 모니터링
    - 애플리케이션이 살아 있는지, 로그 정보는 정상 설정이 되었는지, 커넥션 풀은 얼마나 사용되고 있는지 등을 확인

    # 액츄에이터
    - 수많은 기능을 엔드포인트로 제공한다.
    - 엔드포인트를 사용하려면?
        1. 엔드포인트 활성화
        2. 엔드포인트 노출

    - 자주 사용하는 기능 목록
        - beans : 스프링 컨테이너에 등록된 스프링 빈을 보여준다.
        - conditions : condition 을 통해서 빈을 등록할 때 평가 조건과 일치하거나 일치하지 않는 이유를 표시한다.
        - configprops : @ConfigurationProperties 를 보여준다.
        - env : Environment 정보를 보여준다.
        - health : 애플리케이션 헬스 정보를 보여준다.
            - 문제를 수시로 확인할 수 있다.
        - httpexchanges : HTTP 호출 응답 정보를 보여준다. HttpExchangeRepository 를 구현한 빈을 별도로 등록해야 한다.
            - 최대 100개 정보 제공, 넘어가면 과거 요청 삭제
            - 매우 간단한 기능이라 개발에서나 조금 사용하고, 실제 운영 서비스에서는 모니터링 툴이나 핀포인트, Zipkin 같은 다른 기술을 사용한다.
        - info : 애플리케이션 정보를 보여준다.
        - loggers : 애플리케이션 로거 설정을 보여주고(Get) 변경도(Post) 할 수 있다.
            - 이 때 변경은 메모리 상에서만 변경한다. (잠시 확인하기 위해 사용)
        - metrics : 애플리케이션의 메트릭 정보를 보여준다.
        - mappings : @RequestMapping 정보를 보여준다.
        - threaddump : 쓰레드 덤프를 실행해서 보여준다.
        - shutdown : 애플리케이션을 종료한다. 이 기능은 기본으로 비활성화 되어 있다.

    - 엑추에이터는 애플리케이션 내부 정보를 많이 노출하고 있어서 엔드포인트를 공개하면 안된다.
        - 내부에서만 접근 가능하게 내부망을 사용하는 것이 안전하다.
        - 포트도 변경 가능
        - url 경로에 인증 기능 설정 (/actuator 경로로 접근 시 필터나 인터셉터, 스프링 시큐리티로 인증 처리 추가 개발)

    - 마이크로미터
        - 표준 측정 방식
        - 모니터링 도구 추상화
            - 다른 모니터링 도구로 쉽게 교체할 수 있게 되었다.
        - 메트릭 파사드라고 불린다.

    - 매트릭
        - 다양한 지표
            JVM 메트릭
            시스템 메트릭
            애플리케이션 시작 메트릭
            스프링 MVC 메트릭
            톰캣 메트릭
            데이터 소스 메트릭
            로그 메트릭
            ...
            사용자가 메트릭을 직접 정의하는 것도 가능

    - 프로메테우스
        - 메트릭을 수집하고 보관하는 DB
        - 과거의 지표도 확인할 수 있다.
        - 다양한 정규식과 함수를 제공한다.
        - 애플리케이션 설정 + 프로메테우스 설정 필요
            - 프로메테우스 포멧에 맞추어 메트릭 만들기
                - 마이그로미터가 해결해준다.
                - /actuator/prometheus
            - 메트릭을 주기적으로 수집하도록 설정
                - 프로메테우스 yml
                - 기본적으로 10s ~ 1m 정도 권장
        - 게이지와 카운터
            - 게이지 : 임의로 오르내릴 수 있는 값 (CPU, Memory, Connection)
            - 카운터 : 단순하게 증가하는 단일 누적 값 (http, log)
                - 값이 단조롭게 증가하는 카운터는 increase() , rate() 등을 사용해서 표현하자.

    - 그라파나
        - 한눈에 파악 가능한 대시보드
        - 이미 만들어진 공유 대시보드를 활용하면 편하게 모니터링 환경을 구성할 수 있다.

    - ## 공식문서를 잘 보면서 사용하자. ##
 */
//터Import(OrderConfigV0.class)
//@Import(OrderConfigV1.class)
//@Import(OrderConfigV2.class)
//@Import(OrderConfigV3.class)
//@Import(OrderConfigV4.class)
//@Import({OrderConfigV4.class, StockConfigV1.class})
@Import({OrderConfigV4.class, StockConfigV2.class})
@SpringBootApplication(scanBasePackages = "hello.controller")
public class ActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorApplication.class, args);
    }

    // httpexchanges
    @Bean
    public InMemoryHttpExchangeRepository httpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

}

/*
    # 실무 모니터링 환경 팁

    1. 대시보드
        - 전체를 한눈에 볼 수 있는 가장 높은 뷰
        - 마이크로미터, 프로메테우스, 그라파나 등
        - 대상
            - 시스템 메트릭 (CPU, 메모리 등)
            - 애플리케이션 메트릭 (톰캣 쓰레드풀, DB 커넥션풀, 애플리케이션 호출 수 등)
            - 비즈니스 메트릭 (주문수, 쥐소수, 재고수 등)

    2. 애플리케이션 추적
        - 주로 각각의 HTTP 요청을 추적, 일부는 마이크로서비스 환경에서 분산 추적
        - 하나하나를 디테일하게 들어가는 부분
        - 핀포인트, 스카우트, 와탭, 제니퍼 등
            - 영한쌤 강추 : 핀포인트
                - 마이크로서비스 분산 모니터링 및 대용량 트래픽 대응 가능

    3. 로그
        - 가장 자세한 추적, 원하는데로 커스텀 가능
        - 같은 HTTP 요청을 묶어서 확인할 수 있는 방법이 중요
        - 파일로 직접 로그를 남기는 경우
            - 일반 로그와 에러 로그는 파일을 구분해서 남길 것
            - 에러 로그만 확인해서 문제를 바로 정리할 수 있다.
        - 클라우드에 로그를 저장하는 경우
            - 검색이 잘 되도록 구분

   4. 알람
    - 모니터링 툴에서 일정 수치가 넘어가면 슬랙, 문자 등을 연동
    - 알람은 2가지 종류로 구분할 것
        - 경고
            - 경고는 하루 1번 정도 사람이 직접 확인해도 되는 수준
        - 심각
            - 심각은 즉시 확인해야 한다. (슬랙, 문자, 전화)
        - ex.
            - 디스크 사용량 70% -> 경고
            - 디스크 사용량 80% -> 심각
            - CPU 사용량 40% -> 경고
            - CPU 사용량 50% -> 심각
    - 애매하거나 거짓된 알람은 발견 시 바로바로 처리하자
 */
