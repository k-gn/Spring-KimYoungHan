package hello.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;

@Configuration
// @Component 류의 어노테이션이 붙은 클래스를 자동으로 스프링 빈으로 등록해준다.
@ComponentScan(
	// 탐색할 위치를 지정할 수 있다. (default : 어노테이션을 붙인 클래스의 패키지와 하위패키지 전부)
	// 권장하는 방법은 설정 정보 클래스의 위치를 프로젝트 최상단에 두는 것 (관례)
	// basePackages = {"hello.core.member", "hello.core.discount"},
	// 지정한 클래스의 패키지를 탐색 시작 위치로 지정할 수 있다.
	// basePackageClasses = AutoAppConfig.class,
	// 스캔을 제외할 클래스를 지정할 수 있다.
	excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

	// 수동 빈 등록 vs 자동 빈 등록 => 수동 빈 등록이 우선권을 가진다. (수동빈이 자동빈을 오버라이딩 한다.)
	// 스프링 부트에선 기본적으로 오버라이딩을 시키지 않고 예외를 발생시킨다.
	// 어설프게 하면 매우 잡기 어려운 버그가 발생해서 명확하게 하거나 빨리 에러를 내서 튕겨내거나 하는 것을 권장한다.
	// @Bean(name = "memoryMemberRepository")
	// MemberRepository memberRepository() {
	// 	return new MemoryMemberRepository();
	// }
}

/*
	- 인식하는 어노테이션과 부가기능
		1. @Controller : 스프링 MVC 컨트롤러에 사용
			- 스프링 MVC 컨트롤러로 인식
		2. @Service : 스프링 비즈니스 로직에서 사용
			- 개발자가 핵심 비즈니스 로직이 여기 있구나~ 하고 인지하게 해준다.
		3. @Repository : 스프링 데이터 접근 계층에서 사용
			- 스프링 데이터 접근 계층으로 인식하고 데이터 계층의 예외를 스프링 예외로 변환(추상화) 헤준다.
		4. @Configuration : 스프링 설정 정보에서 사용
			- 스프링 설정 정보로 인식하고 빈이 싱글톤을 유지하도록 해준다.
	- 애노테이션을 들어가보면 내부에 @Component 가 있는 것을 볼 수 있다.
		- 애노테이션은 상속관계가 존재하지 않지만 스프링이 지원해준다.
 */