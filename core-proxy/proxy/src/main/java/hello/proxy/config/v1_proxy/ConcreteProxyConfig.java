package hello.proxy.config.v1_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderControllerConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderRepositoryConcreteProxy;
import hello.proxy.config.v1_proxy.concrete_proxy.OrderServiceConcreteProxy;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcreteProxyConfig {

    @Bean
    public OrderControllerV2 orderControllerV2(LogTrace logTrace) {
        OrderControllerV2 controllerImpl = new OrderControllerV2(orderServiceV2(logTrace));
        return new OrderControllerConcreteProxy(controllerImpl, logTrace);
    }

    @Bean
    public OrderServiceV2 orderServiceV2(LogTrace logTrace) {
        OrderServiceV2 serviceImpl = new OrderServiceV2(orderRepositoryV2(logTrace));
        return new OrderServiceConcreteProxy(serviceImpl, logTrace);
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(LogTrace logTrace) {
        OrderRepositoryV2 repositoryImpl = new OrderRepositoryV2();
        return new OrderRepositoryConcreteProxy(repositoryImpl, logTrace);
    }
}

/*
    인터페이스가 없어도 프록시 클래스를 만들 수 있다.
    클래스 기반 프록시는 해당 클래스에만 적용할 수 있고, 인터페이스 기반 프록시는 해당 인터페이스와 같으면 적용할 수 있다.
    클래스 기반 프록시는 부모클래스 생성자 호출, final 키워드가 붙으면 상속 및 오버라이딩이 불가능하다는 단점이 있다.

    인터페이스 기반 프록시는 상속이란 제약으로 부터 자유롭다.
    프로그래밍 관점에선 인터페이스를 사용하는 것이 역할과 구현을 명확하게 나눌 수 있어 좋다.
    단점은 인터페이스가 필요하며, 없으면 구현이 불가능하다는 점이다.
    인터페이스 도입은 구현을 변경할 가능성이 있을 때 효과적이다.

    핵심 : 인터페이스를 사용하는 것이 좋지만 무조건적으로 사용할 필요는 없다.

 */
