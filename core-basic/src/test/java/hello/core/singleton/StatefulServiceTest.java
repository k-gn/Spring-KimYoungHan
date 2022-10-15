package hello.core.singleton;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class StatefulServiceTest {

	@Test
	void statefulServiceSingleton() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

		StatefulService statefulService1 = ac.getBean(StatefulService.class);
		StatefulService statefulService2 = ac.getBean(StatefulService.class);

		// ThreadA
		int userAPrice = statefulService1.order("userA", 10000);
		// ThreadB
		int userBPrice = statefulService2.order("userB", 20000);

		// ThreadA 주문 금액 조회
		// int price = statefulService1.getPrice();
		// 10000 원을 기대했지만 중간에 공유값을 ThreadB 가 바꿔버려서 20000 원이 나온다.
		// System.out.println("price = " + price);

		System.out.println("userAPrice = " + userAPrice);
		System.out.println("userBPrice = " + userBPrice);

		// assertThat(statefulService1.getPrice()).isEqualTo(20000);
	}

	static class TestConfig {

		@Bean
		public StatefulService statefulService() {
			return new StatefulService();
		}
	}
}
