package hello.core.scan.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.context.annotation.ComponentScan.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

public class ComponentFilterAppConfigTest {

	@Test
	void filterScan() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
		BeanA beanA = ac.getBean("beanA", BeanA.class);
		Assertions.assertThat(beanA).isNotNull();

		assertThrows(NoSuchBeanDefinitionException.class, () -> ac.getBean("beanB", BeanB.class));
	}

	@Configuration
	@ComponentScan(
		// 컴포넌트 스캔 대상을 추가로 지정
		includeFilters = @Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
		// 컴포넌트 스캔에서 제외할 대상을 지정
		excludeFilters = {
			@Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class),
			@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = BeanB.class)
		}
		/*
			# FilterType
				1. ANNOTATION : 기본값, 애노테이션을 인식하여 동작 (org.example.SomeAnnotation)
				2. ASSIGNABLE_TYPE : 지정한 타입과 자식 타입을 인식해서 동작 (org.example.SomeClass)
				3. ASPECTJ : AspectJ 패턴 사용 (org.example.*Service+)
				4. REGEX : 정규 표현식  (org\.example\.Default.*)
				5. CUSTOM : TypeFilter 라는 인터페이스를 구현해서 처리 (org.example.MyTypeFilter)

			스프링 부트는 컴포넌트 스캔을 기본으로 제공 / 옵션을 변경하기보단 스프링 기본설정에 맞추어 사용하는 것을 권장
		 */
	)
	static class ComponentFilterAppConfig {

	}
}
