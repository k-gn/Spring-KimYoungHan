package hello.core.singleton;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberServiceImpl;
import hello.core.order.OrderServiceImpl;

public class ConfigurationSingletonTest {

	@Test
	void configurationTest() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		MemberServiceImpl memberService = ac.getBean("memberService", MemberServiceImpl.class);
		OrderServiceImpl orderService = ac.getBean("orderService", OrderServiceImpl.class);
		MemberRepository memberRepository = ac.getBean("memberRepository", MemberRepository.class);

		MemberRepository memberRepository1 = memberService.getMemberRepository();
		MemberRepository memberRepository2 = orderService.getMemberRepository();

		// 모두 같은 객체를 사용하고 있다! => 스프링 빈을 생성 시 싱글톤이 적용됨
		assertThat(memberRepository1).isSameAs(memberRepository2);
		assertThat(memberRepository1).isSameAs(memberRepository);
	}

	@Test
	void configurationDeep() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
		AppConfig appConfig = ac.getBean(AppConfig.class);
		// 스프링은 CGLIB 라는 바이트 코드 조작 라이브러리를 사용해 클래스를 상속받은 임의의 다른 클래스를 만들고, 그 다른 클래스를 스프링 빈으로 등록한다.
		// 그 다른 클래스가 바로 싱글톤을 보장해준다. (CGLIB 내부 기술을 사용하는데 매우 복잡하다.)
		System.out.println("appConfig.getClass() = " + appConfig.getClass());
	}
}
