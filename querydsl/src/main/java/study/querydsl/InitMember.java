package study.querydsl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitMember {
    private final InitMemberService initMemberService;

    /*
        @PostConstruct 와 @Transactional 은 분리되어야 한다. (한번에 같이 못있음)
        - 스프링의 라이프사이클 상에서 함께 선언될 수 없다. ( @PostConstruct는 해당빈의 AOP 적용을 보장하지 않음 )
            - @PostConstruct는 해당 빈 자체만 생성되었다고 가정하고 호출된다. 해당 빈에 관련된 AOP등을 포함한, 전체 스프링 애플리케이션 컨텍스트가 초기화 된 것을 의미하지는 않음.
            - 트랜잭션을 처리하는 AOP등은 스프링의 후 처리기(post processer)가 완전히 동작을 끝내서, 스프링 애플리케이션 컨텍스트의 초기화가 완료되어야 적용
     */
    @PostConstruct
    public void init() {
        initMemberService.init();
    }

    @Component
    static class InitMemberService {
        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");
            em.persist(teamA);
            em.persist(teamB);
            for (int i = 0; i < 100; i++) {
                Team selectedTeam = i % 2 == 0 ? teamA : teamB;
                em.persist(new Member("member" + i, i, selectedTeam));
            }
        }
    }
}
