package hello.core.beanfind;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberServiceImpl;

/*
	# 스프링 빈 상속관계
		- 부모 타입으로 조회 하면 자식 타입도 함께 조회한다.
 */
public class ApplicationContextExtendsFindTest {

	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

	@Configuration
	static class TestConfig {

		@Bean
		DiscountPolicy rateDiscountPolicy() { // 역할 타입의 구현 객체 반환
			return new RateDiscountPolicy();
		}

		@Bean
		DiscountPolicy fixDiscountPolicy() {
			return new FixDiscountPolicy();
		}
	}

	@Test
	@DisplayName("1. 부모타입으로 조회 시 자식이 둘 이상 있으면 중복 오류가 발생한다.")
	void findBeanByParentTypeDuplicate() {
		assertThrows(NoUniqueBeanDefinitionException.class, () -> ac.getBean(DiscountPolicy.class));
	}

	@Test
	@DisplayName("2. 부모타입으로 조회 시 자식이 둘 이상 있으면 빈 이름을 지정하면 된다.")
	void findBeanByBeanName() {
		DiscountPolicy discountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);
		assertThat(discountPolicy).isInstanceOf(RateDiscountPolicy.class);
	}

	@Test
	@DisplayName("3. 부모타입으로 조회 시 자식이 둘 이상 있으면 특정 하위 타입으로 조회하면 된다.")
	void findBeanBySubType() {
		DiscountPolicy discountPolicy = ac.getBean(RateDiscountPolicy.class);
		assertThat(discountPolicy).isInstanceOf(RateDiscountPolicy.class);
	}

	@Test
	@DisplayName("4. 부모타입으로 모두 조회")
	void findAllBeanByParentType() {
		Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);
		assertThat(beansOfType.size()).isEqualTo(2);
		for (String key : beansOfType.keySet()) {
			System.out.println("key = " + key);
			System.out.println("value = " + beansOfType.get(key));
		}
	}

	@Test
	@DisplayName("5. 부모타입으로 모두 조회 - Object")
	void findAllBeanByObjectType() {
		Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);
		// assertThat(beansOfType.size()).isEqualTo(2);
		for (String key : beansOfType.keySet()) {
			System.out.println("key = " + key);
			System.out.println("value = " + beansOfType.get(key));
		}
	}
}