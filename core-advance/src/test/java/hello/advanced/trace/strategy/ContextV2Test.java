package hello.advanced.trace.strategy;

import org.junit.jupiter.api.Test;

import hello.advanced.trace.strategy.code.strategy.ContextV2;
import hello.advanced.trace.strategy.code.strategy.Strategy;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV2Test {

	/**
	 * 전략 패턴 적용 (파라미터)
	 * Context 를 실행할 때 마다 전략을 인수로 전달한다.
	 * -> 원하는 전략을 유연하게 변경할 수 있다.
	 *
	 * 스프링에서는 ContextV2 같은 방식을 템플릿 콜백 패턴이라고 한다.
	 * -> 전략 패턴에서 Context 가 템플릿 역할을 하고, Strategy 부분이 콜백으로 넘어온다 생각하면 된다.
	 * (JdbcTemplate, RestTemplate, TransactionTemplate , RedisTemplate 등)
	 */
	@Test
	void strategyV1() {
		ContextV2 context = new ContextV2();
		context.execute(new StrategyLogic1());
		context.execute(new StrategyLogic2());
	}

	/**
	 * 전략 패턴 익명 내부 클래스
	 */
	@Test
	void strategyV2() {
		ContextV2 context = new ContextV2();
		context.execute(new Strategy() {
			@Override
			public void call() {
				log.info("비즈니스 로직1 실행");
			}
		});
		context.execute(new Strategy() {
			@Override
			public void call() {
				log.info("비즈니스 로직2 실행");
			}
		});
	}

	/**
	 * 전략 패턴 익명 내부 클래스 (람다)
	 */
	@Test
	void strategyV3() {
		ContextV2 context = new ContextV2();
		context.execute(() -> log.info("비즈니스 로직1 실행"));
		// 이렇게 다른 코드의 인수로서 넘겨주는 함수를 콜백이라고 한다. (코드를 넘겨준 곳의 뒤에서 실행된다는 뜻)
		context.execute(() -> log.info("비즈니스 로직2 실행"));
	}
}
