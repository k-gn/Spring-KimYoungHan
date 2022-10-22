package hello.core.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import hello.core.discount.DiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import lombok.RequiredArgsConstructor;

// 사용영역
@Component
// 막상 개발을 해보면 대부분이 다 불변이다.
// 롬복을 사용하면 편하게 생성자 주입을 사용할 수 있다.
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /*
        구체도 의존하고 있어서 기획 변경 시 클라이언트 부분도 같이 수정해야 한다 (DIP-구현을 모르는, OCP-확장에 열리고 변경에 닫힌 => 위배)
     */
//    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
//    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    // 구체가 아닌 추상에만 의존하도록 변경 (누군가 대신 주입을 해줘야 한다.)
    private final MemberRepository memberRepository;


    /*
		# @Autowired 조회 빈이 2개 이상일 경우
		- 기본적으로 2개 이상일 때 문제가 발생한다.

		# 해결 방법
		1. 하위 타입으로 지정 : DIP 를 위배하고 유연성이 떨어진다. + 이름만 다르고 완전히 똑같은 타입의 스프링 빈이 2개 있을 때 해결이 안된다.

		2. @Autowired 필드명 매칭 : 타입 매칭을 시도하고, 그 결과로 여러 빈이 있으면 필드 이름으로 빈 이름을 추가 매칭한다.

		3. @Qualifier : 추가 구분자를 붙여주는 방법 ( 빈 이름을 적어준다 - @Qualifier("rateDiscountPolicy") ) / 애도 못찾으면 필드명으로 또 찾는다.
		               @Qualifier 를 찾는 용도로만 명확하게 사용하자.
		               명시적으로 획득하는 방식

		4. @Primary : 우선순위를 정하는 방법 (@Primary 붙은 클래스가 우선권을 가진다)
		              @Qualifier 없이 편리하게 조회할 수 있다.
   	                  main db 와 sub db 가 있을 때 main 에 @Primary 를 붙여주고, sub 는 @Qualifier 를 지정해서 가져오자~ 이런식으로 룰을 지정해서 사용할 수 있다.

        셋다 빈을 못찾으면 기본적으로 예외를 발생시킨다.

        @Qualifier > @Primary 순으로 우선순위를 가진다. -> 스프링은 자동보다 수동, 넓은 범위보다 좁은 범위의 선택권이 우선순위가 높다.
	 */
    // Command + option + b : 구현체 확인
    private final DiscountPolicy discountPolicy;
    // private final DiscountPolicy rateDiscountPolicy; // 1

    /*
        - 생성자 의존성 주입
            1. 생성자 호출 시점에 딱 1번만 호출
            2. 불변, 필수
            3. 생성자가 딱 1개만 있으면 @Autowired 생략 가능
            4. 함부로 불변객체에 대한 setter / getter 만들지 말 것!

        생성자 -> 수정자 주입 순으로 호출된다.

        - 수정자 주입
            1. setter 라 불리는 필드의 값을 변경하는 수정자 메서드를 통해 의존관계 주입
            2. 선택, 변경 가능성이 있는 의존관계에 사용
            3. 주입할 의존성이 없어도 @Autowired(required = false) 로 선택적 사용 가능
                (기본적으로 @Autowired 는 주입할 대상이 없으면 오류 발생)

        - 필드 주입
            1. 필드에 바로 주입하는 방식
            2. 코드가 간결하지만 외부에서 변경이 불가능해서 테스트하기 힘들다.
            3. DI 프레임워크가 없으면 아무것도 할 수 없다.
            4. 실제 코드와 관계없는 테스트 코드나 스프링에서 설정을 목적으로 하는 클래스에서나 사용하고 왠만하면 쓰지말자.

        - 일반 메서드 주입
            1. 일반 메서드를 통해 주입받을 수 있다.
            2. 한번에 여러 필드를 주입받을 수 있다.
            3. 잘 안쓴다.

        new 로 생성하는 객체는 당연히 의존성 주입 대상이 아니다.
        스프링 컨테이너가 관리하는 스프링 빈이어야 의존성 주입이 동작한다.
     */
    // @Autowired
    /*public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("rateDiscountPolicy") DiscountPolicy discountPolicy) {
    /*public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) {
        System.out.println("memberRepository : " + memberRepository);
        System.out.println("discountPolicy : " + discountPolicy);
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }*/

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        // 할인 정책에게 할인에 대해선 모르겠고 너가 그냥 할인해줘~ => 단일책임 원칙을 잘 지킨 것!
        int discountPrice = discountPolicy.discount(member, itemPrice);
        // int discountPrice = rateDiscountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 그냥 get 만 처도 나오면서 메소드 완성됨
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}