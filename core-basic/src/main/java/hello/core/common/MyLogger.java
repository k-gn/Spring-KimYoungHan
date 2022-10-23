package hello.core.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

/*
    # 웹 스코프
    - 웹 환경에서만 동작
    - 스프링이 해당 스코프의 종료시점까지 관리
        - request : HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각 HTTP 요청마다 별도의 빈 인스턴스가 생성되고 관리된다.
        - session : HTTP session 과 동일한 생명주기를 가지는 스코프
        - application : 서블릿 컨텍스트와 동일한 생명주기를 가지는 스코프
        - websocket : 웹 소켓과 동일한 생명주기를 가지는 스코프

    # 스코프와 프록시
    - ObjectProvider 를 쓰지 않고 request scope 를 주입
    - proxyMode = ScopedProxyMode.TARGET_CLASS 부분이 핵심
        - 적용 대상이 인터페이스가 아닌 클래스면 TARGET_CLASS
        - 적용 대상이 인터페이스면 INTERFACES
    - 위 설정을 통해 MyLogger 의 가짜 프록시 클래스를 만들어두고 request 요청과 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입할 수 있다.
        - CGLIB 라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어 주입한다.
        - 가짜 프록시 클래스는 상속받아 만들어졌기 때문에 클라이언트 입장에서는 원본을 몰라도 동일하게 사용할 수 있다. (다형성)
    - 이 후 실제로 호출될 때 진짜 클래스를 찾아 요청을 위임하도록 동작한다.

    - Provider를 사용하든 프록시를 사용하든 핵심은 진짜 객체 조회를 꼭 필요한 시점까지 지연처리 한다는 것!
    - 웹 스코프가 아니여도 프록시는 그냥 사용할 수 있다!

    # 주의점
    - 싱글톤 같지만 다르게 동작하기 때문에 주의해서 사용해야한다.
    - 최소화해서 사용하지 않으면, 유지보수하기 어려워진다.
*/
@Component
// 단지 어노태이션 설정만으로 객체를 프록시로 대체할 수 있다. - 다형성과 DI 컨테이너가 가진 강점
// AOP 나 프록시 기술을 활용하면 원본 코드를 고칠 필요 없이 편하게 다앙향 것들을 해볼 수 있다.
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {

    private String uuid;

    private String requestURL;

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("[" + uuid + "]" + " [" + requestURL + "] " + "- message = " + message);
    }

    @PostConstruct
    public void init() {
        this.uuid = UUID.randomUUID().toString();
        System.out.println("MyLogger.init");
    }

    @PreDestroy
    public void close() {
        System.out.println("MyLogger.close");
    }
}
