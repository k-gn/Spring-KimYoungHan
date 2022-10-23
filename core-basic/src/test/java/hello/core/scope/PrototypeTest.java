package hello.core.scope;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
	스프링 빈이 스프링 컨테이너의 시작과 함께 생성되어 스프링 컨테이너 종료시 까지 유지되는 것은
	기본적으로 스프링 빈이 싱글톤 스코프로 생성되기 때문이다.

	# 빈 스코프
		- 스프링은 다양한 스코프를 지원한다.
		1. 싱글톤 : 기본 스코프, 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프
		2. 프로토타입 : 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 짧은 범위의 스코프
		3. 웹 관련 스코프
			- request : 웹 요청이 들어오고 나갈때까지 유지되는 스코프
			- session : 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프
			- application : 웹의 서블릿 컨텍스트와 같은 범위로 유지되는 스코프

	# 프로토타입 스코프
		- 싱글톤 스코프의 빈은 조회 시 항상 같은 인스턴스의 스프링 빈을 반환한다.
		- 프로토타입은 항상 새로운 인스턴스를 생성해서 반환한다.
		- 핵심은 스프링 컨테이너는 프로토타입 빈을 생성하고, 의존관계 주입, 초기화까지만 처리한다.
		- 이 후 스프링 컨테이너는 생성된 프로토타입 빈을 관리하지 않는다. (관리 책임이 클라이언트로 넘어감) -> 종료 메서드는 실행 불가 (클라이언트가 직접 해야함)
        - 대부분 싱글톤을 쓰고 어쩌다가 프로토타입을 쓴다.
 */
public class PrototypeTest {

    @Test
    void prototypeBeanFind() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrototypeBean.class);

        PrototypeBean bean1 = context.getBean(PrototypeBean.class);
        PrototypeBean bean2 = context.getBean(PrototypeBean.class);
        System.out.println("bean1 = " + bean1);
        System.out.println("bean2 = " + bean2);

        context.close();
        bean1.destroy();
        bean2.destroy();
    }

    @Scope("prototype")
    static class PrototypeBean {

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init");
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
