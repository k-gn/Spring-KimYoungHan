package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"}) //JDK 동적 프록시
//@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"}) //CGLIB 프록시
@SpringBootTest
@Import(ProxyDIAspect.class)
public class ProxyDITest {

    @Autowired
    MemberService memberService;

    // JDK 동적 프록시는 대상 객체인 MemberServiceImpl 타입에 의존관계를 주입할 수 없다.
    // DI 도 프록시 기반
    @Autowired
    MemberServiceImpl memberServiceImpl;

    @Test
    void go() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());
        memberServiceImpl.hello("hello");
    }

    // 스프링에서 CGLIB는 구체 클래스를 상속 받아서 AOP 프록시를 생성할 때 사용한다.
    // CGLIB 구체 클래스 기반 프록시 문제점
    // 1. 대상 클래스에 기본 생성자 필수
    // 2. 생성자 2번 호출 문제
    // 3. final 키워드 클래스, 메서드 사용 불가

    /*
        스프링 3.2, CGLIB를 스프링 내부에 함께 패키징
        CGLIB를 사용하려면 CGLIB 라이브러리가 별도로 필요했다.
        스프링은 CGLIB 라이브러리를 스프링 내부에 함께 패키징해서
        별도의 라이브러리 추가 없이 CGLIB를 사용할 수 있게 되었다.

        CGLIB 기본 생성자 필수 문제 해결
        스프링 4.0부터 CGLIB의 기본 생성자가 필수인 문제가 해결되었다.
        objenesis 라는 특별한 라이브러리를 사용해서 기본 생성자 없이 객체 생성이 가능하다.
        참고로 이 라이브러리는 생성자 호출 없이 객체를 생성할 수 있게 해준다.

        생성자 2번 호출 문제
        스프링 4.0부터 CGLIB의 생성자 2번 호출 문제가 해결되었다.
        이것도 역시 objenesis 라는 특별한 라이브러리 덕분에 가능해졌다. 이제 생성자가 1번만 호출된다.

        스프링 부트 2.0 - CGLIB 기본 사용
        스프링 부트 2.0 버전부터 CGLIB를 기본으로 사용하도록 했다.
        이렇게 해서 구체 클래스 타입으로 의존관계를 주입하는 문제를 해결했다.
        스프링 부트는 별도의 설정이 없다면 AOP를 적용할 때 기본적으로 proxyTargetClass=true 로 설정해서 사용한다.
        따라서 인터페이스가 있어도 JDK 동적 프록시를 사용하는 것이 아니라 항상 CGLIB를 사용해서 구체클래스를 기반으로 프록시를 생성한다.
        물론 스프링은 우리에게 선택권을 열어주기 때문에 다음과 깉이 설정하면 JDK 동적 프록시도 사용할 수 있다.

        final 키워드는 AOP 적용대상 안에서 거의 사용되지 않기 때문에 크게 신경쓰지 않아도 된다.

        **

        스프링은 최종적으로 CGLIB 를 기본으로 사용한다.
     */
}
