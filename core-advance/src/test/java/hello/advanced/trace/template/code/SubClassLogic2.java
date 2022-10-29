package hello.advanced.trace.template.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubClassLogic2 extends AbstractTemplate {

	@Override
	protected void call() {
		log.info("비즈니스 로직2 실행");
	}
}

/*
	템플릿 메서드 패턴은 SubClassLogic1 , SubClassLogic2 처럼 클래스를 계속 만들어야 하는 단점이 있다.
	익명 내부 클래스를 사용하면 이런 단점을 보완할 수 있다.
 */