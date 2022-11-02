package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {

    /*
        지금까지 프록시를 적용하기 위해 적용 대상의 숫자 만큼 많은 프록시 클래스를 만들었다.
        적용 대상이 100 개면 프록시 클래스도 100개 만들었다.

        이러한 문제를 해결하는 것이 동적 프록시다.
        동적 프록시 기술을 사용하면 개발자가 직접 프록시 클래스를 만들지 않아도 된다.
        이름 그대로 프록시 객체를 동적으로 런타임에 개발자 대신 만들어준다.
        그리고 동적 프록시에 원하는 실행 로직을 지정할 수 있다.

        # 실행 순서
        1. 클라이언트는 JDK 동적 프록시의 call() 을 실행한다.
        2. JDK 동적 프록시는 InvocationHandler.invoke() 를 호출한다. TimeInvocationHandler 가 구현체로 있으로 TimeInvocationHandler.invoke() 가 호출된다.
        3. TimeInvocationHandler 가 내부 로직을 수행하고, method.invoke(target, args) 를 호출해서 target 인 실제 객체( AImpl )를 호출한다.
        4. AImpl 인스턴스의 call() 이 실행된다.
        5. AImpl 인스턴스의 call() 의 실행이 끝나면 TimeInvocationHandler 로 응답이 돌아온다. 시간 로그를 출력하고 결과를 반환한다.
     */
    @Test
    void dynamicA() {
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 동적 프록시는 java.lang.reflect.Proxy 를 통해서 생성할 수 있다.
        // 클래스 로더 정보, 인터페이스, 그리고 핸들러 로직을 넣어주면 된다. 그러면 해당 인터페이스를 기반으로 동적 프록시를 생성하고 그 결과를 반환한다.
        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

    /*
        AImpl , BImpl 각각 프록시를 만들지 않았다. 프록시는 JDK 동적 프록시를 사용해서 동적으로 만들고 TimeInvocationHandler 는 공통으로 사용했다.
        JDK 동적 프록시 기술 덕분에 적용 대상 만큼 프록시 객체를 만들지 않아도 된다. 그리고 같은 부가 기능 로직을 한번만 개발해서 공통으로 적용할 수 있다.
        프록시 클래스를 수 없이 만들어야 하는 문제도 해결하고, 부가 기능 로직도 하나의 클래스에 모아서 단일 책임 원칙(SRP)도 지킬 수 있게 되었다.
     */
    @Test
    void dynamicB() {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }
}
