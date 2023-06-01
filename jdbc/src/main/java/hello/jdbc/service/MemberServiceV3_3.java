package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * 트랜잭션 - @Transactional AOP
 *
 * 스프링 aop 를 적용하려면 스프링 컨테이너가 필요하다.
 * 즉, 스프링에 의해 관리/호출되어야 동작한다. (빈 등록, 주입)
 * cglib 라이브러리를 활용해 해당 클래스를 상속받은 프록시를 동적으로 만들어 동작한다.
 *
 * 프록시 동작 -> 트랜잭션 시작 -> 트랜잭션 매니저 -> 트랜잭션 동기화 매니저 -> 비즈니스 로직 -> 트랜잭션 종료 순으로 흐름
 *
 * 선언적 트랜잭션 관리 (@Transactional) 를 대부분 사용한다.
 */
@Slf4j
public class MemberServiceV3_3 {

    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_3(MemberRepositoryV3 memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

}
