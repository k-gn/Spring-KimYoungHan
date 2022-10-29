package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/*
	필드에 전략을 보관하는 방식

	ContextV1 은 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드
	문맥 속에서 strategy 를 통해 일부 전략이 변경된다

	스프링에서 의존관계 주입에서 사용하는 방식이 바로 전략 패턴이다. (선 조립 후 실행)
 */
@Slf4j
public class ContextV1 {

	private Strategy strategy;

	public ContextV1(Strategy strategy) {
		this.strategy = strategy;
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		strategy.call(); //위임
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}
}
