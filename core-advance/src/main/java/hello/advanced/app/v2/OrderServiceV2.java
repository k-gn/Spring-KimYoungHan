package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hello.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {

	private final OrderRepositoryV2 orderRepository;

	private final HelloTraceV2 traceV2;

	public void orderItem(String itemId, TraceId traceId) {
		TraceStatus status = null;
		try {
			status = traceV2.beginSync(traceId, "OrderService.orderItem()");
			orderRepository.save(itemId, status.getTraceId());
			traceV2.end(status);
		} catch (Exception exception) {
			traceV2.exception(status, exception);
			throw exception;
		}
	}
}
