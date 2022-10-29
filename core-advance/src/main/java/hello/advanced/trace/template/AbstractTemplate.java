package hello.advanced.trace.template;

import java.util.List;
import java.util.stream.IntStream;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;

/*
	AbstractTemplate 은 템플릿 메서드 패턴에서 부모 클래스이고, 템플릿 역할을 한다.
	템플릿 메서드 패턴 덕분에 변하는 코드와 변하지 않는 코드를 명확하게 분리했다.
	로그를 출력하는 템플릿 역할을 하는 변하지 않는 코드는 모두 AbstractTemplate 에 담아두고,
	변하는 코드는 자식 클래스를 만들어서 분리했다.

	변경에 쉽게 대처할 수 있는 구조
 */
public abstract class AbstractTemplate<T> {

	private final LogTrace trace;

	public AbstractTemplate(LogTrace trace) {this.trace = trace;}

	public T execute(String message) {
		TraceStatus status = null;
		try {
			status = trace.begin(message);

			//로직 호출
			T result = call();

			trace.end(status);
			return result;
		} catch (Exception e) {
			trace.exception(status, e);
			throw e;
		}
	}

	protected abstract T call();
}
