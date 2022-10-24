package hello.advanced.trace.hello;

import hello.advanced.trace.TraceStatus;
import org.junit.jupiter.api.Test;

class HelloTraceV2Test {

	@Test
	void begin_end() {
		HelloTraceV2 traceV2 = new HelloTraceV2();

		TraceStatus status1 = traceV2.begin("hello");
		TraceStatus status2 = traceV2.beginSync(status1.getTraceId(), "hi~");

		traceV2.end(status2);
		traceV2.end(status1);
	}

	@Test
	void begin_exception() {
		HelloTraceV2 traceV2 = new HelloTraceV2();

		TraceStatus status1 = traceV2.begin("hello");
		TraceStatus status2 = traceV2.beginSync(status1.getTraceId(), "hi~");

		traceV2.exception(status2, new IllegalArgumentException());
		traceV2.exception(status1, new IllegalArgumentException());
	}
}