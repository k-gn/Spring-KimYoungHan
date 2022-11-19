package hello.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AopApplicationTests {

	@Test
	void contextLoads() {
		System.out.println(test());
	}

	public int test() {
		for (int i = 0; i < 10; i++) {
			return i;
		}
		throw new IllegalArgumentException();
	}
}
