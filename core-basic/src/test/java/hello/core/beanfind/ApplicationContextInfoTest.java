package hello.core.beanfind;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.core.AppConfig;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;

public class ApplicationContextInfoTest {

	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
	AnnotationConfigApplicationContext ac2 = new AnnotationConfigApplicationContext(SameBeanConfig.class);

	@Test
	@DisplayName("1. 모든 빈 출력하기")
	void findAllBean() {
		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
		// iter + tab
		for (String beanDefinitionName : beanDefinitionNames) {
			Object bean = ac.getBean(beanDefinitionName);
			System.out.println("name = " + beanDefinitionName + " object = " + bean); // soutv , soutm
		}
	}

	@Test
	@DisplayName("2. 애플리케이션 빈 출력하기")
	void findApplicationBean() {
		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
		// iter + tab
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

			if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
				Object bean = ac.getBean(beanDefinitionName);
				System.out.println("name = " + beanDefinitionName + " object = " + bean); // soutv , soutm
			}
		}
	}

	// method 안에서 ctrl + r = 실행
	@Test
	@DisplayName("3. 빈 이름으로 조회")
	void findBeanByName() {
		MemberService memberService = ac.getBean("memberService", MemberService.class);
		System.out.println("memberService = " + memberService);
		System.out.println("memberService = " + memberService.getClass());

		assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
	}

	@Test
	@DisplayName("4. 이름 없이 타입으로만 조회")
	void findBeanByType() {
		// 인터페이스 선택 시 구현체가 대상
		MemberService memberService = ac.getBean(MemberService.class);
		System.out.println("memberService = " + memberService);
		System.out.println("memberService = " + memberService.getClass());

		assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
	}

	@Test
	@DisplayName("5. 구체 타입으로 조회")
	void findBeanByName2() {
		// 구체타입 조회 -> 구현에 의존 = 그렇게 좋은 코드는 아니다.
		MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
		System.out.println("memberService = " + memberService);
		System.out.println("memberService = " + memberService.getClass());

		assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
	}

	@Test
	@DisplayName("6. 빈 이름으로 조회 실패")
	void findBeanByNameX() {
		// 구체타입 조회 -> 구현에 의존 = 그렇게 좋은 코드는 아니다.
		// MemberService memberService = ac.getBean("memberService", MemberService.class);
		assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("xxxx", MemberServiceImpl.class));
	}

	@Test
	@DisplayName("7. 타입으로 조회 시 같은 타입이 둘 이상 있으면 중복 에러가 발생한다.")
	void findBeanByTypeDuplicate() {
		assertThrows(NoUniqueBeanDefinitionException.class, () -> ac2.getBean(MemberRepository.class));
	}

	@Test
	@DisplayName("8. 타입으로 조회 시 같은 타입이 둘 이상 있으면 빈 이름을 지정하면 된다.")
	void findBeanByNameDuplicate() {
		MemberRepository memberRepository = ac2.getBean("memberRepository1", MemberRepository.class);
		assertThat(memberRepository).isInstanceOf(MemberRepository.class);
	}

	@Test
	@DisplayName("9. 특정 타입을 모두 조회")
	void findAllBeanByType() {
		Map<String, MemberRepository> beansOfType = ac2.getBeansOfType(MemberRepository.class);
		for (String key : beansOfType.keySet()) {
			System.out.println("key = " + key);
			System.out.println("value = " + beansOfType.get(key));
		}
		System.out.println("beansOfType = " + beansOfType);
		assertThat(beansOfType.size()).isEqualTo(2);
	}

	// 내부 클래스를 선언 할 때는 static 키워드를 붙여준다.
	// 내부클래스 - scope 를 부모 클래스 내로 하겠다는 의미
	@Configuration
	static class SameBeanConfig {

		@Bean
		MemberRepository memberRepository1() {
			return new MemoryMemberRepository();
		}

		@Bean
		MemberRepository memberRepository2() {
			return new MemoryMemberRepository();
		}
	}
}