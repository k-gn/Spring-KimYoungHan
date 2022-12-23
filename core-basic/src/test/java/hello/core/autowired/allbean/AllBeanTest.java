package hello.core.autowired.allbean;

import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AutoAppConfig;
import hello.core.discount.DiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;

public class AllBeanTest {

	@Test
	void findAllBean() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AutoAppConfig.class, DiscountService.class);

		DiscountService discountService = ac.getBean(DiscountService.class);
		Member userA = new Member(1L, "userA", Grade.VIP);
		int fixDiscountPrice = discountService.discount(userA, 10000, "fixDiscountPolicy");
		Assertions.assertThat(fixDiscountPrice).isEqualTo(1000);

		int rateDiscountPrice = discountService.discount(userA, 10000, "rateDiscountPolicy");
		Assertions.assertThat(rateDiscountPrice).isEqualTo(1000);
	}

	/*
		# 조회한 빈이 모두 필요할 때 List, Map
		- 의도적으로 해당 타입의 빈이 다 필요한 경우가 있을 수 있다.
	*/
	static class DiscountService {
		private final Map<String, DiscountPolicy> policyMap;
		private final List<DiscountPolicy> policies;

		@Autowired
		DiscountService(
			Map<String, DiscountPolicy> policyMap,
			List<DiscountPolicy> policies
		) {
			this.policyMap = policyMap;
			this.policies = policies;
			System.out.println("policyMap = " + policyMap);
			System.out.println("policies = " + policies);
		}

		// 아래처럼 동적으로 다형성을 적용하여 활용이 가능하다.
		public int discount(
			Member member,
			int price,
			String discountCode // 할인 코드를 빈이름과 매칭
		) {
			DiscountPolicy discountPolicy = policyMap.get(discountCode);
			return discountPolicy.discount(member, price);
		}
	}
}
