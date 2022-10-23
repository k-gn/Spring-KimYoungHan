package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycle {

	@Test
	void lifeCycleTest() {
		ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
		NetworkClients networkClients = ac.getBean(NetworkClients.class);
		ac.close();
	}

	@Configuration
	static class LifeCycleConfig {

//		@Bean(initMethod = "init")//, destroyMethod = "close")
		@Bean
		public NetworkClients networkClient() {
			NetworkClients networkClient = new NetworkClients();
			networkClient.setUrl("http://hello-spring.dev");
			return networkClient;
		}
	}
}
