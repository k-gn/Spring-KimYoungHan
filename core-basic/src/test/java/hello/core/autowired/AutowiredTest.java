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

/*
	## 생성자 주입을 선택해라!
	- "불변"
		- 대부분의 의존관계 주입은 한번 일어나면 애플리케이션 종료시점까지 의존관계를 변경할 일이 없다.
		- 대부분의 의존관계는 애플리케이션 종료 전까지 변하면 안된다.
		- 수정자 주입을 사용하면 setter 메서드를 public 으로 열어두어여 한다.
		- 누군가 실수로 변경할 수 있고, 변경하면 안되는 메서드를 열어두는 것은 좋은 설계방법이 아니다.
		- 생성자 주입은 객체를 생성할 때 딱 1번만 호출되므로 이 후에 호출되는 일이 없어 불변하게 설계할 수 있다.
		- 순환참조를 방지할 수 있다.

	- "누락"
		- 프레임워크 없이 순수 자바 코드를 단위 테스트하는 경우 수정자 의존관계라면 @Autowired 시 의존관계가 없을 때 오류가 발생한다.
		- 하지만 오류가 나는지 안나는지 실행해야 확인이 가능하다.
		- 프레임워크에 의존하지 않고, 순수한 자바 언어의 특징을 잘 살리는 방법이다.
		- 필드에 final 키워드를 사용할 수 있다. -> 생성자에 값이 설정되지 않는 오류를 컴파일 시점에 확인이 가능하다.

	생성자 주입과 수정자 주입은 동시에 사용할 수 있다.
	기본적으로 생성자 주입을 사용하고, 필수값이 아닌 경우 수정자 주입방식을 옵션으로 부여하면 된다.
 */