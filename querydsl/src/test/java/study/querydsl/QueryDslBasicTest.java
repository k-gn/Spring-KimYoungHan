package study.querydsl;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before() {
		// 엔티티 매니저 자체가 멀티쓰레드에 문제없게 설계되어 있다! ( 트랜잭션 마다 별도의 영속성 컨텍스트를 제공 )
		// 따라서 동시성 문제없이 동작한다.
		queryFactory = new JPAQueryFactory(em);

		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}

	@Test
	void startJPQL() {
		// find member1
		Member member1 = em.createQuery("select m from Member m where m.username = :username", Member.class)
			.setParameter("username", "member1")
			.getSingleResult();

		assertThat(member1.getUsername()).isEqualTo("member1");
	}

	/*
		- EntityManager 로 JPAQueryFactory 생성
		- Querydsl은 JPQL 빌더
		- JPQL: 문자(실행 시점 오류), Querydsl: 코드(컴파일 시점 오류)
		- JPQL: 파라미터 바인딩 직접, Querydsl: 파라미터 바인딩 자동 처리

	 */

	@Test
	void startQueryDsl() {
		// QMember m = new QMember("m");
		// 컴파일 시점에 오류를 잡을 수 있다!
		Member member1 = queryFactory
			.select(member) // static import! (같은 테이블을 조인해야 하는 경우에 선언해서 사용하고 아니면 이렇게 사용하자)
			.from(member)
			.where(member.username.eq("member1")) // prepareStatements (auto parameter binding)
			.fetchOne();

		assertThat(member1.getUsername()).isEqualTo("member1");
	}

	/*


	 */
	@Test
	public void search() {

		Member findMember = queryFactory
			.selectFrom(member) // select , from 을 selectFrom 으로 합칠 수 있음
			// 다양한 검색 조건들을 제공한다 (eq, ne, in, gt, lt, like, contains, startsWith ...)
			.where(
				member.username.eq("member1")
				.and(member.age.eq(10))) // 검색조건은 .and(),.or()를 메서드 체인으로 연결할 수 있다.
			.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	/*
		- 결과 조회
			- fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
			- fetchOne() : 단 건 조회
				결과가 없으면 : null
				결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
			- fetchFirst() : limit(1).fetchOne() (처음 한 건 조회)
			- fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행 (페이징에서 사용) / QueryResults<T>
			- fetchCount() : count 쿼리로 변경해서 count 수 조회
	 */
	@Test
	public void searchAndParam() {
		List<Member> result1 = queryFactory
			.selectFrom(member)
			.where(
				member.username.eq("member1"),
				member.age.eq(10)) // where() 에 파라미터로 검색조건을 추가하면 AND 조건이 추가됨
				// null 값은 무시 -> 메서드 추출을 통해 동적 쿼리를 깔끔하게 만들 수 있다.
			.fetch();

		assertThat(result1.size()).isEqualTo(1);
	}

	/*
		- 정렬
		desc() , asc() : 일반 정렬
		nullsLast() , nullsFirst() : null 데이터 순서 부여 (null 데이터 마지막 또는 처음 출력)
	 */
	@Test
	public void sort() {
		em.persist(new Member(null, 100));
		em.persist(new Member("member5", 100));
		em.persist(new Member("member6", 100));
		List<Member> result = queryFactory
			.selectFrom(member)
			.where(member.age.eq(100))
			.orderBy(member.age.desc(), member.username.asc().nullsLast())
			.fetch();
		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(2);
		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}

	/*
		- 페이징
	 */
	@Test
	public void paging1() {
		// 조회 건수 제한
		List<Member> result = queryFactory
			.selectFrom(member)
			.orderBy(member.username.desc())
			.offset(1) // 0부터 시작(zero index)
			.limit(2) //최대 2건 조회
			.fetch();
		assertThat(result.size()).isEqualTo(2);
	}

	/*
		실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만, count 쿼리는 조인이 필요 없는 경우도 있다.
		이렇게 fetchResults 같이 자동화된 count 쿼리는 원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 안나올 수 있다.
		count 쿼리에 조인이 필요없는 성능 최적화가 필요하다면, count 전용 쿼리를 별도로 작성해야 한다.
	 */
	@Test
	public void paging2() {
		// 전체 조회 수 및 페이징 정보가 필요하면? -> fetchResults
		QueryResults<Member> queryResults = queryFactory
			.selectFrom(member)
			.orderBy(member.username.desc())
			.offset(1)
			.limit(2)
			.fetchResults(); // count 쿼리가 실행되니 성능상 주의!
		assertThat(queryResults.getTotal()).isEqualTo(4);
		assertThat(queryResults.getLimit()).isEqualTo(2);
		assertThat(queryResults.getOffset()).isEqualTo(1);
		assertThat(queryResults.getResults().size()).isEqualTo(2);
	}
}
