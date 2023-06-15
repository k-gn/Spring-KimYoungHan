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
 */
//@Import(OrderConfigV0.class)
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
