package study.querydsl.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import javax.persistence.EntityManager;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        Member findMember = memberRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);
        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).containsExactly(member);
        List<Member> result2 = memberRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    /*
        # QuerydslPredicateExecutor 구현 시 querydsl 조건을 바로 넣어줄 수 있다.

        QuerydslPredicateExecutor 는 Pagable, Sort를 모두 지원하고 정상 동작한다.

        - 한계점
            조인X (묵시적 조인은 가능하지만 left join이 불가능하다.)
            클라이언트가 Querydsl에 의존해야 한다. 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다.
            복잡한 실무환경에서 사용하기에는 한계가 명확하다
     */
    @Test
    void querydslPredicateTest() {
        Iterable result = memberRepository.findAll(
                member.age.between(10, 40).and(member.username.eq("member1"))
        );
    }

    /*
        count(*) 을 사용하고 싶으면 예제의 주석처럼 Wildcard.count 를 사용.
        member.count() 를 사용하면 count(member.id) 로 처리.
        응답 결과는 숫자 하나이므로 fetchOne() 을 사용
     */
    @Test
    public void count() {
        Long totalCount = queryFactory
                //.select(Wildcard.count) //select count(*)
                .select(member.count()) //select count(member.id)
                .from(member)
                .fetchOne();
        System.out.println("totalCount = " + totalCount);
    }

    /*
        # Querydsl Web 지원
        - 공식 URL: https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/#core.web.type-safe
            - 한계점
                단순한 조건만 가능
                조건을 커스텀하는 기능이 복잡하고 명시적이지 않음
                컨트롤러가 Querydsl에 의존
                복잡한 실무환경에서 사용하기에는 한계가 명확
     */
}