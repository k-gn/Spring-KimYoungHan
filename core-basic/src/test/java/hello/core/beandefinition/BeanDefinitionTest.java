package hello.core.beandefinition;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import hello.core.AppConfig;

/*
	# BeanFactory
		- 스프링 컨테이너의 최상위 인터페이스
		- 스프링 빈을 관리하고 조회하는 역할을 담당
		- getBean() 제공

	# ApplicationContext
		- BeanFactory 기능을 모두 상속 받아서 제공
		- 그 외에 수많은 부가기능을 제공해준다.
			1. 메시지 소스를 활용한 국제화 기능
			2. 환경변수
			3. 애플리케이션 이벤트
			4. 편리한 리소스 조회

	# BeanDefinition
		- 스프링 빈 설정 메타정보
		- 스프링 컨테이너는 자바 코드인지, xml 인지 몰라도 된다. (역할과 구현을 개념적으로 나눈 것)
		- 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈 / 인스턴스를 생성한다.
		- ApplicationContext 에서 BeanDefinitionReader 를 사용해 설정 정보를 읽고 BeanDefinition 를 생성한다.
 */
public class BeanDefinitionTest {

	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

	@Test
	@DisplayName("1. 빈 설정 메타정보 확인")
	void findApplicationBean() {
		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

			if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
				System.out.println("beanDefinitionName = " + beanDefinitionName);
				System.out.println("beanDefinition = " + beanDefinition);
			}
		}
	}
}
