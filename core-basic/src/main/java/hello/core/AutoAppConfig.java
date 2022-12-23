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
