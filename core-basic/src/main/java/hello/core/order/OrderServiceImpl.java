package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;

// 사용영역
public class OrderServiceImpl implements OrderService {

    /*
        구체도 의존하고 있어서 기획 변경 시 클라이언트 부분도 같이 수정해야 한다 (DIP-구현을 모르는, OCP-확장에 열리고 변경에 닫힌 => 위배)
     */
//    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    // 구체가 아닌 추상에만 의존하도록 변경 (누군가 대신 주입을 해줘야 한다.)
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        // 할인 정책에게 할인에 대해선 모르겠고 너가 그냥 할인해줘~ => 단일책임 원칙을 잘 지킨 것!
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
