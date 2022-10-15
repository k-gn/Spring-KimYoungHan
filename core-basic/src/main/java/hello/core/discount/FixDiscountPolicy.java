package hello.core.discount;

import org.springframework.stereotype.Component;

import hello.core.member.Grade;
import hello.core.member.Member;

// @Component
public class FixDiscountPolicy implements DiscountPolicy {

    private int dixcountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        if (member.getGrade() == Grade.VIP) {
            return dixcountFixAmount;
        } else {
            return 0;
        }
    }
}
