package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.*;

/*
     # 인터페이스가 있는 경우에는 JDK 동적 프록시를 적용하고, 그렇지 않은 경우에는 CGLIB를 적용하려면 어떻게 해야할까?
        - 스프링은 유사한 구체적인 기술들이 있을 때, 그것들을 통합해서 일관성 있게 접근할 수 있고, 더욱 편리하게 사용할 수 있는 추상화된 기술을 제공한다.
        - 스프링은 동적 프록시를 통합해서 편리하게 만들어주는 프록시 팩토리( ProxyFactory )라는 기능을 제공한다.
        - 이전에는 상황에 따라서 JDK 동적 프록시를 사용하거나 CGLIB를 사용해야 했다면, 이제는 이 프록시팩토리 하나로 편리하게 동적 프록시를 생성할 수 있다.
        - 프록시 팩토리는 인터페이스가 있으면 JDK 동적 프록시를 사용하고, 구체 클래스만 있다면 CGLIB를 사용한다. 그리고 이 설정을 변경할 수도 있다.

     # 두 기술을 함께 사용할 때 부가 기능을 적용하기 위해 JDK 동적 프록시가 제공하는
       InvocationHandler와 CGLIB가 제공하는 MethodInterceptor를 각각 중복으로 따로 만들어야 할까?
        - 스프링은 이 문제를 해결하기 위해 부가 기능을 적용할 때 Advice 라는 새로운 개념을 도입했다.
        - 개발자는 InvocationHandler 나 MethodInterceptor 를 신경쓰지 않고, Advice 만 만들면 된다.
        - 결과적으로 InvocationHandler 나 MethodInterceptor 는 Advice 를 호출하게 된다.
        - 프록시 팩토리를 사용하면 Advice 를 호출하는 전용 InvocationHandler , MethodInterceptor 를 내부에서 사용한다

    # 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되었으면?
        - 앞서 특정 메서드 이름의 조건에 맞을 때만 프록시 부가 기능이 적용되는 코드를 직접 만들었다.
        - 스프링은 Pointcut 이라는 개념을 도입해서 이 문제를 일관성 있게 해결한다.
 */
@Slf4j
public class ProxyFactoryTest {

    /*
        new ProxyFactory(target) : 프록시 팩토리를 생성할 때, 생성자에 프록시의 호출 대상을 함께 넘겨준다. 프록시 팩토리는 이 인스턴스 정보를 기반으로 프록시를 만들어낸다.
         만약 이 인스턴스에 인터페이스가 있다면 JDK 동적 프록시를 기본으로 사용하고 인터페이스가 없고 구체 클래스만 있다면 CGLIB를 통해서 동적 프록시를 생성한다.
         여기서는 target 이 new ServiceImpl() 의 인스턴스이기 때문에 ServiceInterface 인터페이스가 있다. 따라서 이 인터페이스를 기반으로 JDK 동적 프록시를 생성한다.
     */
    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        // 프록시 팩토리를 통해서 만든 프록시가 사용할 부가 기능 로직을 설정한다.
        proxyFactory.addAdvice(new TimeAdvice());
        // 프록시 객체를 생성하고 그 결과를 받는다.
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        // 프록시 팩토리를 통한 프록시 적용 확인
        assertThat(AopUtils.isAopProxy(proxy)).isTrue(); // 프록시 팩토리를 통해서 프록시가 생성되면 JDK 동적 프록시나, CGLIB 모두 참이다.
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue(); // 프록시 팩토리를 통해서 프록시가 생성되고, JDK 동적 프록시인 경우 참
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse(); // 프록시 팩토리를 통해서 프록시가 생성되고, CGLIB 동적 프록시인 경우 참
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        ConcreteService proxy = (ConcreteService) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    /*
        # 프록시 팩토리의 기술 선택 방법
        대상에 인터페이스가 있으면: JDK 동적 프록시, 인터페이스 기반 프록시
        대상에 인터페이스가 없으면: CGLIB, 구체 클래스 기반 프록시
        proxyTargetClass=true : CGLIB, 구체 클래스 기반 프록시, 인터페이스 여부와 상관없음

        # 정리
        프록시 팩토리의 서비스 추상화 덕분에 구체적인 CGLIB, JDK 동적 프록시 기술에 의존하지 않고, 매우 편리하게 동적 프록시를 생성할 수 있다.
        프록시의 부가 기능 로직도 특정 기술에 종속적이지 않게 Advice 하나로 편리하게 사용할 수 있었다.
        이것은 프록시 팩토리가 내부에서 JDK 동적 프록시인 경우 InvocationHandler 가 Advice 를 호출하도록 개발해두고,
        CGLIB인 경우 MethodInterceptor 가 Advice 를 호출하도록 기능을 개발해두었기 때문이다.

         스프링 부트는 AOP를 적용할 때 기본적으로 proxyTargetClass=true 로 설정해서 사용한다.
         따라서 인터페이스가 있어도 항상 CGLIB를 사용해서 구체 클래스를 기반으로 프록시를 생성한다.
     */

    @Test
    @DisplayName("ProxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB를 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TimeAdvice());
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

}
