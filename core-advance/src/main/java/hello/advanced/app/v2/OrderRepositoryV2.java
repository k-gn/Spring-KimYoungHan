package hello.advanced.app.v2;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hello.HelloTraceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryV2 {

	private final HelloTraceV2 traceV2;

	public void save(String itemId, TraceId traceId) {

		TraceStatus status = null;
		try {
			status = traceV2.beginSync(traceId, "OrderRepository.save()");
			if (itemId.equals("ex")) {
				throw new IllegalArgumentException("예외 발생!");
			}
			sleep(1000);
			traceV2.end(status);
		} catch (Exception exception) {
			traceV2.exception(status, exception);
			throw exception;
		}
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
