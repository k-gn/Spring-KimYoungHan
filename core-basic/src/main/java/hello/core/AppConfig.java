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

/* 구성영역
    구현 객체를 생성하고 연결하는 책임을 가지는 클래스
    공연을 구성하고, 배우를 섭외하고, 역할에 맞는 배우를 지정하는 공연 기획자 같은 클래스
    관심사 분리 - 구체 클래스를 AppConfig 가 선택한다.
    애플리케이션이 어떻게 동작해야 할지 전체 구성을 책임진다.
    이제 각 MemberServiceImpl 나 OrderServiceImpl 는 각 기능을 "실행" 만 시키면 된다.
 */
public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    private DiscountPolicy discountPolicy() {
//        return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
