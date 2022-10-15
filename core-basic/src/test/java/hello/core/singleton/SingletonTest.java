package hello.core.singleton;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;
import hello.core.member.MemberService;

// 테스트 추가 시 전체 테스트를 돌려보면 좋다.
// 스프링 컨테이너는 기본적으로 객체를 싱글톤으로 생성해준다.
public class SingletonTest {

	// 스프링이 없는 순수한 DI 컨테이너는 요청 시 객체를 새로 생성한다. -> 메모리 낭비가 심하다.
	// 고객 트래픽이 초당 100 이 나오면 초당 100 개 객체가 생성되고 소멸된다/
	// 해당 객체가 딱 1개만 생성되고 공유하도록 설계해야한다 -> 싱글톤 패턴
	@Test
	@DisplayName("1. 스프링 없는 순수한 DI 컨테이너")
	void pureContainer() {
		AppConfig appConfig = new AppConfig();

		// 조회 : 호출할 때 마다 객체를 생성
		MemberService memberService1 = appConfig.memberService();
		MemberService memberService2 = appConfig.memberService();

		// 참조값이 다른 것을 확인
		System.out.println("memberService1 = " + memberService1);
		System.out.println("memberService2 = " + memberService2);

		assertThat(memberService1).isNotSameAs(memberService2);
	}

	@Test
	@DisplayName("2. 싱글톤 패턴을 적용한 객체 사용")
	void singletonServiceTest() {
		SingletonService singletonService1 = SingletonService.getInstance();
		SingletonService singletonService2 = SingletonService.getInstance();
		System.out.println("singletonService1 = " + singletonService1);
		System.out.println("singletonService2 = " + singletonService2);
		assertThat(singletonService1).isEqualTo(singletonService2); // 내용자체를 비교 (equals())
		assertThat(singletonService1).isSameAs(singletonService2); // 주소값을 비교 (==)
	}

	// 스프링 컨테이너 덕분에 요청이 올 때 마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유해서 효율적으로 재사용 가능
	@Test
	@DisplayName("3. 스프링 컨테이너와 싱글톤")
	void springContainer() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		// 조회 : 호출할 때 마다 객체를 생성
		MemberService memberService1 = ac.getBean("memberService", MemberService.class);
		MemberService memberService2 = ac.getBean("memberService", MemberService.class);

		// 참조값이 다른 것을 확인
		System.out.println("memberService1 = " + memberService1);
		System.out.println("memberService2 = " + memberService2);

		assertThat(memberService1).isSameAs(memberService2);
	}
}
