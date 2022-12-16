package study.querydsl;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
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

	/*
		# 집합
		 select
		 COUNT(m), //회원수
		 SUM(m.age), //나이 합
		 AVG(m.age), //평균 나이
		 MAX(m.age), //최대 나이
		 MIN(m.age) //최소 나이
		 from Member m
		 
		 Tuple : 여러 다양한 값이 있을 때 받을 수 있는 타입 ( 엔티티 형태가 아닌 여러 컬럼으로 이루어진 정보를 조회)
		 -> 사용하기 편하긴 하지만 사용자 입장에서 어떤 속성들을 조회할지 인지하기가 힘들다.
		 -> DTO 로 깔끔하게 넘기는 게 더 좋다.
	 */
	@Test
	public void aggregation() throws Exception {
		List<Tuple> result = queryFactory
				.select(member.count(),
						member.age.sum(),
						member.age.avg(),
						member.age.max(),
						member.age.min())
				.from(member)
				.fetch();
		Tuple tuple = result.get(0);
		assertThat(tuple.get(member.count())).isEqualTo(4);
		assertThat(tuple.get(member.age.sum())).isEqualTo(100);
		assertThat(tuple.get(member.age.avg())).isEqualTo(25);
		assertThat(tuple.get(member.age.max())).isEqualTo(40);
		assertThat(tuple.get(member.age.min())).isEqualTo(10);
	}


	/*
	  # GroupBy 사용

	  * groupBy , 그룹화된 결과를 제한하려면 having
			.groupBy(item.price)
			.having(item.price.gt(1000))

	 */
	@Test
	public void group() throws Exception {
		List<Tuple> result = queryFactory
				.select(team.name, member.age.avg())
				.from(member)
				.join(member.team, team)
				.groupBy(team.name)
				.fetch();
		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);
		assertThat(teamA.get(team.name)).isEqualTo("teamA");
		assertThat(teamA.get(member.age.avg())).isEqualTo(15);
		assertThat(teamB.get(team.name)).isEqualTo("teamB");
		assertThat(teamB.get(member.age.avg())).isEqualTo(35);
	}

	/*
		# 조인 - 기본 조인
		- 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭(alias)으로 사용할 Q 타입을 지정하면 된다
			- join(조인 대상, 별칭으로 사용할 Q타입)

		join() , innerJoin() : 내부 조인(inner join)
		leftJoin() : left 외부 조인(left outer join)
		rightJoin() : rigth 외부 조인(rigth outer join)
	 */
	@Test
	public void join() throws Exception {
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		List<Member> result = queryFactory
				.selectFrom(member)
				.join(member.team, team)
				.where(team.name.eq("teamA"))
				.fetch();

		assertThat(result)
				.extracting("username")
				.containsExactly("member1", "member2");
	}

	/*
		# 세타 조인 (cross join)
		- 연관관계가 없는 필드로 조인

		from 절에 여러 엔티티를 선택해서 세타 조인
		조인 on을 사용하면 외부 조인 가능
	 */
	@Test
	public void theta_join() throws Exception {
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));
		List<Member> result = queryFactory
				.select(member)
				.from(member, team) // 모든 값들을 다 가져와서 조인한다. (성능이 좋진 않은 것 같다)
				.where(member.username.eq(team.name))
				.fetch();

		assertThat(result)
				.extracting("username")
				.containsExactly("teamA", "teamB");
	}

	/*
		조인 - on절

		ON절을 활용한 조인(JPA 2.1부터 지원)
		1. 조인 대상 필터링
		2. 연관관계 없는 엔티티 외부 조인

		- 조인 대상 필터링

		on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면,
		where 절에서 필터링 하는 것과 기능이 동일하다

		따라서 on 절을 활용한 조인 대상 필터링을 사용할 때,
		내부조인 이면 익숙한 where 절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.

	 */
	@Test
	public void join_on_filtering() throws Exception {
		// 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
		// JPQL : SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
		// SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and t.name='teamA'
		List<Tuple> result = queryFactory
				.select(member, team)
				.from(member)
				.leftJoin(member.team, team).on(team.name.eq("teamA"))
				.fetch();
		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}

	/*
		 - 연관관계 없는 엔티티 외부 조인
	 */
	@Test
	public void join_on_no_relation() throws Exception {
		// 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
		// JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
		// SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));
		List<Tuple> result = queryFactory
				.select(member, team)
				.from(member)
				// .leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
				.leftJoin(team).on(member.username.eq(team.name))
				.fetch();
		for (Tuple tuple : result) {
			System.out.println("t=" + tuple);
		}
	}

	/*
		# 조인 - 페치 조인

		페치 조인은 SQL에서 제공하는 기능은 아니다. SQL조인을 활용해서 연관된 엔티티를 SQL 한번에
		조회하는 기능이다. 주로 성능 최적화에 사용하는 방법이다.

		join(), leftJoin() 등 조인 기능 뒤에 fetchJoin() 이라고 추가하면 된다.
	 */
	@Test
	public void fetchJoinUse() throws Exception {
		em.flush();
		em.clear();
		Member findMember = queryFactory
				.selectFrom(member)
				.join(member.team, team).fetchJoin()
				.where(member.username.eq("member1"))
				.fetchOne();
	}

	/*
		# 서브 쿼리

		com.querydsl.jpa.JPAExpressions 사용

		- select, where 절에서 지원

		- from 절의 서브쿼리 한계
		JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 당연히 Querydsl
		도 지원하지 않는다. 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다. Querydsl도
		하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다

		- from 절의 서브쿼리 해결방안
		1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
		2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
		3. nativeSQL을 사용한다.

		* 너무 화면에 맞추려고 쿼리를 만들지 말자. 너무 복잡해진다.
			-> 데이베이스는 데이터를 퍼오는 용도로 쓰고 그외에 로직은 애플리케이션이나 화면단에서 처리하자!

		* 한방 쿼리가 무조건 좋은게 아니다. 나누는게 더 좋을 수 있다!
			-> 실시간 트래픽이 매우 중요할 경우 쿼리 하나하나가 아깝지만, 그렇지 않다면 고민해보자.
	 */
	@Test
	public void subQuery() throws Exception {
		// 나이가 가장 많은 회원 조회
		// 서브 쿼리 eq 사용
		QMember memberSub = new QMember("memberSub");
		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(
						JPAExpressions
								.select(memberSub.age.max())
								.from(memberSub)
				))
				.fetch();

		assertThat(result).extracting("age")
				.containsExactly(40);
	}

	@Test
	public void subQueryGoe() throws Exception {
		// 나이가 평균 나이 이상인 회원
		// 서브 쿼리 goe 사용
		QMember memberSub = new QMember("memberSub");
		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.goe(
						JPAExpressions
								.select(memberSub.age.avg())
								.from(memberSub)
				))
				.fetch();
		assertThat(result).extracting("age")
				.containsExactly(30,40);
	}

	@Test
	public void subQueryIn() throws Exception {
		// 서브쿼리 여러 건 처리, in 사용
		QMember memberSub = new QMember("memberSub");
		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.in(
						JPAExpressions
								.select(memberSub.age)
								.from(memberSub)
								.where(memberSub.age.gt(10))
				))
				.fetch();
		assertThat(result).extracting("age")
				.containsExactly(20, 30, 40);
	}

	@Test
	public void subQueryInSelect() throws Exception {
		// select 절에 subquery
		QMember memberSub = new QMember("memberSub");
		List<Tuple> fetch = queryFactory
				.select(member.username,
						JPAExpressions
								.select(memberSub.age.avg())
								.from(memberSub)
				).from(member)
				.fetch();

		for (Tuple tuple : fetch) {
			System.out.println("username = " + tuple.get(member.username));
			System.out.println("age = " +
					tuple.get(JPAExpressions.select(memberSub.age.avg())
							.from(memberSub)));
		}
	}

	/*
		# Case 문

		- select, 조건절(where), order by에서 사용 가능
		- case 로 전환하고 바꾸고 이런 작업은 도움이 되기도 하지만 가급적 데이터베이스에서 이런 작업을 하는 것 보다
		  애플리케이션으로 그냥 가져와서 바꾸는걸 권장한다. (데이터베이스는 데이터를 퍼올리는 역할)

		- 단순
		List<String> result = queryFactory
		 .select(member.age
			 .when(10).then("열살")
			 .when(20).then("스무살")
			 .otherwise("기타")
		 )
		 .from(member)
		 .fetch();
		 
		 - 복잡
		 List<String> result = queryFactory
		 .select(new CaseBuilder()
			 .when(member.age.between(0, 20)).then("0~20살")
			 .when(member.age.between(21, 30)).then("21~30살")
			 .otherwise("기타")
		 )
		 .from(member)
		 .fetch();

		 - orderBy에서 Case 문 함께 사용하기
		 - Querydsl은 자바 코드로 작성하기 때문에 rankPath 처럼 복잡한 조건을 변수로 선언해서 select 절, orderBy 절에서 함께 사용할 수 있다.
		NumberExpression<Integer> rankPath = new CaseBuilder()
			 .when(member.age.between(0, 20)).then(2)
			 .when(member.age.between(21, 30)).then(1)
			 .otherwise(3);

		List<Tuple> result = queryFactory
		 .select(member.username, member.age, rankPath)
		 .from(member)
		 .orderBy(rankPath.desc())
		 .fetch();
	 */

	/*
		# 상수, 문자 더하기

		- 상수가 필요하면 Expressions.constant(xxx) 사용
		Tuple result = queryFactory
			 .select(member.username, Expressions.constant("A"))
			 .from(member)
			 .fetchFirst();

		- 위와 같이 최적화가 가능하면 SQL에 constant 값을 넘기지 않는다. 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 constant 값을 넘긴다

		- 문자 더하기 (concat)
		String result = queryFactory
			 .select(member.username.concat("_").concat(member.age.stringValue()))
			 .from(member)
			 .where(member.username.eq("member1"))
			 .fetchOne();
		 - member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue() 로
		   문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.
	 */

}
