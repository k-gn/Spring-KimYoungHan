package hello.advanced.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hello.advanced.trace.logtrace.FieldLogTrace;
import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.logtrace.ThreadLocalLogTrace;

@Configuration
public class LogTraceConfig {

	@Bean
	public LogTrace logTrace() {
		// return new FieldLogTrace();
		return new ThreadLocalLogTrace();
	}
}
