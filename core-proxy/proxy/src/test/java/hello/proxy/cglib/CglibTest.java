package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

/*
    # CGLIB: Code Generator Library
    CGLIB는 바이트코드를 조작해서 동적으로 클래스를 생성하는 기술을 제공하는 라이브러리이다.
    CGLIB를 사용하면 인터페이스가 없어도 구체 클래스만 가지고 동적 프록시를 만들어낼 수 있다.
    CGLIB는 원래는 외부 라이브러리인데, 스프링 프레임워크가 스프링 내부 소스 코드에 포함했다.
    따라서 스프링을 사용한다면 별도의 외부 라이브러리를 추가하지 않아도 사용할 수 있다.

    참고로 우리가 CGLIB를 직접 사용하는 경우는 거의 없다.
    스프링의 ProxyFactory 라는 것이 이 기술을 편리하게 사용하게 도와주기 때문에, 너무 깊이있게 파기 보다는 CGLIB가 무엇인지 대략 개념만 잡으면 된다.

    # CGLIB 제약
        클래스 기반 프록시는 상속을 사용하기 때문에 몇가지 제약이 있다.
        부모 클래스의 생성자를 체크해야 한다. -> CGLIB는 자식 클래스를 동적으로 생성하기 때문에 기본 생성자가 필요하다.
        클래스에 final 키워드가 붙으면 상속이 불가능하다. -> CGLIB에서는 예외가 발생한다.
        메서드에 final 키워드가 붙으면 해당 메서드를 오버라이딩 할 수 없다. -> 따라서 CGLIB에서는 프록시 로직이 동작하지 않는다.
 */
@Slf4j
public class CglibTest {

    /*
        Enhancer : CGLIB는 Enhancer 를 사용해서 프록시를 생성한다.
        enhancer.setSuperclass(ConcreteService.class) : CGLIB는 구체 클래스를 상속 받아서 프록시를 생성할 수 있다. 어떤 구체 클래스를 상속 받을지 지정한다.
        enhancer.setCallback(new TimeMethodInterceptor(target)) : 프록시에 적용할 실행 로직을 할당한다.
        enhancer.create() : 프록시를 생성한다. 앞서 설정한 enhancer.setSuperclass(ConcreteService.class) 에서 지정한 클래스를 상속 받아서 프록시가 만들어진다.

        JDK 동적 프록시는 인터페이스를 구현(implement)해서 프록시를 만든다. CGLIB는 구체 클래스를 상속 (extends)해서 프록시를 만든다.
     */
    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService) enhancer.create();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();
    }
}
/*
    ## 남은 문제
        1. 인터페이스가 있는 경우에는 JDK 동적 프록시를 적용하고, 그렇지 않은 경우에는 CGLIB를 적용하려면 어떻게 해야할까?
        2. 두 기술을 함께 사용할 때 부가 기능을 제공하기 위해서 JDK 동적 프록시가 제공하는 InvocationHandler 와 CGLIB가 제공하는
           MethodInterceptor 를 각각 중복으로 만들어서 관리해야 할까?
        3. 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되었으면?
 */