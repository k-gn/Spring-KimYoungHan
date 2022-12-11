package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // junit 실행 시 스프링과 함께 실행
@SpringBootTest // 스프링 부트를 띄우고 테스트 실행
// 테스트에선 트랜잭션 시 자동으로 롤백된다. @Rollback(false) 를 사용하여 롤백이 안되게 할 수 있다.
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    //@Rollback(false)
    public void 회원가입() throws Exception {
        /*
            given when then test pattern (준비 실행 검증)
                - BDD(Behaviour-Driven Development)중 하나
                - BDD는 TDD를 수행하려는 어떤한 행동과 기능을 개발자가 더 이해하기 쉽게하는 것이 목적이다.
                - 테스트 코드를 익히고 유지보수하는데 좋다.

            Given
                - 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
                - 테스트를 위해 주어진 상태 / 테스트 대상에게 주어진 조건 / 테스트가 동작하기 위해 주어진 환경
            When
                - 구체화하고자 하는 그 행동
            Then
                - 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명

            => 즉, 어떤 상태에서 출발(given)하여 어떤 상태이 변화를 가했을 때(when) 기대하는 어떠한 상태가 되어야 한다.(then)
         */

        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush(); // insert문 확인
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class) // 예외가 터지면 잡힌다.
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야 한다!!!

        //then
        fail("예외가 발생해야 한다."); // 코드가 여기까지 오면 fail, 예외가 발생해 안오면 success
    }
}
/*
    테스트를 위해 외부 데이터베이스를 설치하고, 테스트 종료 시 데이터가 남아있는 것은 불편하다.
    -> 격리된 테스트 환경 = 메모리 db (was 뜰 때 같이 뜨고 꺼지면 같이 사라진다. - 메모리라서 다 초기화됨!)
    
    테스트 영역에 resource + yml 생성 (테스트 영역의 yml이 우선적)
    운영과 테스트의 yml을 분리하는게 당연히 좋다! (서로 당연히 설정이 다르다)

    => 근데 스프링 부트에선 별도의 설정이 없다면 그냥 메모리 모드로 돌린다!! (yml 에 h2 설정이 없어도 된다)

    테스트에선 기본적으로 create-drop 으로 돌아간다.
 */