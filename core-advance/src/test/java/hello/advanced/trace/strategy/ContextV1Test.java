package hello.advanced.trace.strategy;

import org.junit.jupiter.api.Test;

import hello.advanced.trace.strategy.code.strategy.ContextV1;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;

/*
	템플릿 메서드 패턴은 상속을 사용한다.

	상속을 받는다는 것은 특정 부모클래스를 의존하고 있다는 것이다.
	자식 클래스 입장에서는 부모클래스의 기능을 전혀 사용하지 않아도 부모클래스를 알아야 한다.
	또한 잘못된 의존관계 때문에 부모클래스를 수정하면 자식 클래스에도 영향을 줄 수 있다.

	템플릿 메서드 패턴은 별도의 클래스나 익명 내부 클래스를 만들어야 하는 부분도 복잡하다.

	# 템플릿 메서드 패턴과 비슷한 역할을 하면서 상속의 단점을 제거할 수 있는 디자인 패턴이 바로 전략 패턴이다.
	- 전략 패턴은 변하지 않는 부분을 Context 라는 곳에 두고, 변하는 부분을 Strategy 라는 인터페이스를 만들고 해당 인터페이스를 구현하도록 해서 문제를 해결한다.
		- 상속이 아니라 위임으로 문제를 해결하는 것이다.
		- 전략 패턴에서 Context 는 변하지 않는 템플릿 역할을 하고, Strategy 는 변하는 알고리즘 역할을 한다.
		-  전략을 사용하면 알고리즘을 사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다.
 */
@Slf4j
public class ContextV1Test {

	@Test
	void strategyV0() {

		logic1();
		logic2();
	}

	private void logic1() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		log.info("비즈니스 로직1 실행");
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	private void logic2() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		log.info("비즈니스 로직2 실행");
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	/**
	 * 전략 패턴 적용
	 */
	@Test
	void strategyV1() {
		Strategy strategyLogic1 = new StrategyLogic1();
		ContextV1 context1 = new ContextV1(strategyLogic1); // 전략 주입
		context1.execute();

		Strategy strategyLogic2 = new StrategyLogic2();
		ContextV1 context2 = new ContextV1(strategyLogic2); // 전략 주입
		context2.execute();
	}

	/**
	 * 전략 패턴 익명 내부 클래스
	 */
	@Test
	void strategyV2() {
		ContextV1 context1 = new ContextV1(new Strategy() {
			@Override
			public void call() {
				log.info("비즈니스 로직1 실행");
			}
		});
		context1.execute();

		ContextV1 context2 = new ContextV1(new Strategy() {
			@Override
			public void call() {
				log.info("비즈니스 로직2 실행");
			}
		});
		context2.execute();
	}

	/**
	 * 전략 패턴 익명 내부 클래스 (람다)
	 * 람다로 변경하려면 인터페이스에 메서드가 1개만 있으면 되는데,
	 * 여기에서 제공하는 Strategy 인터페이스는 메서드가 1개만 있으므로 람다로 사용할 수 있다.
	 */
	@Test
	void strategyV3() {
		ContextV1 context1 = new ContextV1(() -> log.info("비즈니스 로직1 실행"));
		context1.execute();
		ContextV1 context2 = new ContextV1(() -> log.info("비즈니스 로직2 실행"));
		context2.execute();
	}

}
