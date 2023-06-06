package me.developery.actuatordemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
	# spring boot auto configuration
	- @ConditionalOne~ 애노테이션을 활용하여 해당 기능에 대한 의존성이 있냐 없냐에 따라 빈을 생성해주거나 생성하지 않는다.
 */
@SpringBootApplication
public class ActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActuatorApplication.class, args);
	}

}
