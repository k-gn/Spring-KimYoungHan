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

/*
	## 자동, 수동의 올바른 실무 운영 기준
	- 편리한 자동 기능을 기본으로 사용하자!
	- 스프링은 점점 자동을 선호하는 추세이다!
	- 자동 빈 등록을 사용해도 OCP, DIP 를 지킬 수 있다!
	- 수동으로 만든 빈들의 설정정보가 커질 수록 관리하기가 힘들어진다!

	# 그럼 수동 빈 등록은 언제 사용할까?
	- 애플리케이션은 크게 업무로직과 기술지원로직으로 나뉜다.
	- 업무로직 빈 : 컨트롤러, 서비스, 레포지토리 등 - 보통 비즈니스 요구사항을 개발할 때 추가 / 변경된다.
		- 숫자도 많고, 유사한 패턴이 있어 자동 기능을 적극 사용하는 것이 좋다.
		- 어떤 곳에서 문제가 발생했는지 명확히 파악하기 쉽다.
	- 기술지원 빈 : 기술적 문제나 공통 관심사 (AOP) 처리 시 주로 사용된다. 데이터베이스 연결, 공통 로그처리 처럼 업무 로직을 지원하기 위한 기술이다.
		- 수가 매우 적고, 애플리케이션 전반에 걸쳐 광범위하게 영향을 미친다.
		- 잘 적용되어 있는지 파악하기 어려운 경우가 많다.
		- 따라서 수동 빈 등록으로 명확하게 들어내는 것이 좋다.

	- 스프링과 스프링부트가 자동으로 등록하는 많은 빈들은 예외다. (메뉴얼을 참조하여 의도한 대로 편하게 사용하면 됨)
	- 내가 직접 기술 지원 객체를 빈으로 등록한다면 수동으로 등록하는 것이 좋다.

	- 비즈니스 로직 중에서 다형성을 적극 활용할 때 (조회한 빈이 모두 필요할 때 List, Map 사용한 부분)
		- 어떤 빈들이 주입될 지, 각 빈들의 이름이 뭔지 한눈에 코드만 보고 파악하기 힘들다. (하나하나 찾아 들어가서 확인해야함)
		- 이런 경우 수동 빈으로 등록하거나 자동으로 할 경우 구현 빈들을 특정 패키지에 같이 묶어 두는 것이 좋다!
 */