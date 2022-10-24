package hello.advanced.app.v2;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hello.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class OrderControllerV2 {

	private final OrderServiceV2 orderService;

	private final HelloTraceV2 traceV2;

	@GetMapping("/v2/request")
	public String request(String itemId) {

		TraceStatus status = null;
		try {
			status = traceV2.begin("OrderController.request()");
			orderService.orderItem(itemId, status.getTraceId());
			traceV2.end(status);
			return "ok";
		} catch (Exception exception) {
			traceV2.exception(status, exception);
			throw exception;
		}
	}
}
