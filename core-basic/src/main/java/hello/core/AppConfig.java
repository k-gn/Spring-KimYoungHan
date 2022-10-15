package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* 구성영역
    구현 객체를 생성하고 연결하는 책임을 가지는 클래스
    공연을 구성하고, 배우를 섭외하고, 역할에 맞는 배우를 지정하는 공연 기획자 같은 클래스
    관심사 분리 - 구체 클래스를 AppConfig 가 선택한다.
    애플리케이션이 어떻게 동작해야 할지 전체 구성을 책임진다.
    이제 각 MemberServiceImpl 나 OrderServiceImpl 는 각 기능을 "실행" 만 시키면 된다.

    AppConfig 가 스프링에서 IOC / DI Container 역할

    @Configuration - 설정 클래스 싱글톤으로 빈 등록 / 해당 어노테이션류를 붙여야 CGLIB 가 동작을 한다.
                    @Bean 만 사용해도 스프링 빈으로 등록은 되지만 싱글톤을 보장하진 않는다.
                    크게 고민할 것 없이 스프링 설정정보는 해당 어노테이션을 붙여주자!
    AppConfig 는 팩토리 빈 / 메소드가 적용된 방식이라 할 수 있다.
    팩토리 메소드 : 생성 패턴 중 하나로 객체를 생성할 때 어떤 클래스의 인스턴스를 어떻게 만들 지 서브 클래스에서 결정하는 것
 */
@Configuration
public class AppConfig {

    // @Configuration 이 없어도 @Autowired 를 통해 주입 후 끌어다 쓰는 방식으로 대체할 순 있다.
    // @Autowired MemberRepository memberRepository;

    // @Bean 이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고 아니면 새로 빈으로 등록하고 반환하도록 동적으로 만들어진다. => 싱글톤 보장
    @Bean // 빈으로 등록하기, 메소드명을 빈 이름으로 사용한다.
    public MemberService memberService() {
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }

    @Bean
    public OrderService orderService() {
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    @Bean
    public DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
