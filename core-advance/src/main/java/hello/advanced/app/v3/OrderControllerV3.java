package hello.advanced.app.v3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;
import lombok.RequiredArgsConstructor;

/*
	- 요구사항
		1. 모든 Public 메서드의 호출과 응답 정보를 로그로 추적
		2. 애플리케이션의 흐름을 변경하면 안됨
			- 즉, 로그를 남긴다고 해서 비즈니스 로직의 동작에 영향을 주면 안됨
		3. 메서드 호출에 걸린 시간
		4. 정상 흐름과 예외 흐름 구분
			- 예외 발생 시 예외 정보가 남아야 함
		5. 메서드 호출의 깊이 표현
		6. HTTP 요청을 구분
			- HTTP 요청 단위로 특정 ID 를 남겨서 어떤 HTTP 요청에서 시작된 것인지 명확하게 구분이 가능해야 함
			- 트랜잭션 ID (DB X), 여기서는 하나의 HTTP 요청이 시작해서 끝날 때 까지를 하나의 트랜잭션이라 한다.
 */
@RestController
@RequiredArgsConstructor
public class OrderControllerV3 {

	private final OrderServiceV3 orderService;

	private final LogTrace trace;

	@GetMapping("/v3/request")
	public String request(String itemId) {

		/*
		 	V0 에 비해 핵심기능 외에 부가적인 코드들이 많아져서 복잡하다. -> 배보다 배꼽이 큰 상황

		 	동일한 패턴들이 보이지만 try 문은 물론이고, 핵심 기능이 중간에 존재해서 단순하게 메서드로 추출하는 것은 어렵다.

			## 변하는 것과 변하지 않는 것을 분리하는 것이 좋은 설계이다.
				- 로그 추적기 같은 경우 변하지 않는 부분에 속한다.
				- 핵심 기능과 로그 추적기 분리 -> 모듈화
				- 템플릿 메서드 패턴은 이런 문제를 해결하는 디자인 패턴이다.
					- 추상 템플릿에 변하지 않는 코드를 모아놓는 패턴
					- 코드 중복이 사라진다!
		*/
		TraceStatus status = null;
		try {
			status = trace.begin("OrderController.request()");
			orderService.orderItem(itemId);
			trace.end(status);
			return "ok";
		} catch (Exception exception) {
			trace.exception(status, exception);
			throw exception;
		}
	}
}
