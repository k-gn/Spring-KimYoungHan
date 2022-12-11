package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // jpa 는 트랜잭션 안에서 기본적으로 동작한다. (readOnly = true : 읽기 전용 - 조회 성능 최적화, 더티체킹 방지)
@RequiredArgsConstructor
public class MemberService {

    /*
        생성자 주입은 생성자의 호출 시점에 1회 호출 되는 것이 보장
        Spring 프레임워크에서는 생성자 주입을 적극 지원 (권장)
            1. 객체의 불변성 확보
            2. 테스트 코드의 작성
            3. final 키워드 작성 및 Lombok과의 결합
            4. 스프링에 비침투적인 코드 작성
            5. 순환 참조 에러 방지 (예방)

        이외의 주입방법들은 테스트용으론 좋다.
     */
    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional // 따로 줄 경우 우선권을 가짐
    public Long join(Member member) {

        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member); //항상 영속성 컨텍스트에서 id 값을 넣어준다.
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // 동시성 이슈가 있다. -> 데이터베이스에 name 을 유니크 조건으로 걸어주면 안전하다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}
