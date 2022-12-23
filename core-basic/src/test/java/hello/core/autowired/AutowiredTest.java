package hello.core.autowired;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.Nullable;

import hello.core.member.Member;

public class AutowiredTest {

	@Test
	void autowiredOption() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(TestBean.class);
	}

	/*
		주입할 빈이 없어도 동작해야 하는 경우가 있다.
		1. @Autowired(required = false) : 자동 주입할 대상이 없으면 수정자 메서드 자체가 호출이 안된다.
		2. @Nullable : 자동 주입할 대상이 없으면 null 이 입력된다.
		3. Optional<> : 자동 주입할 대상이 없으면 Optional.empty 가 입략된다.

		2번과 3번은 스프링 전반에 걸쳐서 지원된다.
		따라서 생성자 자동주입에서 특정 필드에 사용해도 된다.
	 */
	static class TestBean {

		@Autowired(required = false)
		public void setNoBean1(Member member) {
			System.out.println("member1 = " + member);
		}

		@Autowired
		public void setNoBean2(@Nullable Member member) {
			System.out.println("member2 = " + member);
		}

		@Autowired
		public void setNoBean3(Optional<Member> member) {
			System.out.println("member3 = " + member);
		}
	}
}
