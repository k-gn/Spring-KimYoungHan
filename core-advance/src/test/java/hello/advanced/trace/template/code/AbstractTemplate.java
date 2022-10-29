package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

/*
	템플릿은 기준이 되는 거대한 틀 - 변하지 않는 부분을 몰아둔다
 */
@Slf4j
public abstract class AbstractTemplate {

	public void execute() {
		long startTime = System.currentTimeMillis();
		//비즈니스 로직 실행
		call(); // 상속 (템플릿 안에서 변하는 부분은 call() 메서드를 호출해서 처리)
		//비즈니스 로직 종료
		long endTime = System.currentTimeMillis();
		long resultTime = endTime - startTime;
		log.info("resultTime={}", resultTime);
	}

	// 다형성을 사용하여 변하는 부분은 자식 클래스에 두고 상속과 오버라이딩을 사용해서 처리
	protected abstract void call();
}
