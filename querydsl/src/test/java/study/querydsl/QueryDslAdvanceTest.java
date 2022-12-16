package study.querydsl;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import study.querydsl.dto.MemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QueryDslAdvanceTest {

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

	/*
		# 프로젝션: select 대상 지정

		- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음
		- 프로젝션 대상이 둘 이상이면 튜플이나 DTO로 조회
	 */
	@Test
	void projection() {
		// 프로젝션 대상이 하나
		List<String> result = queryFactory
			.select(member.username)
			.from(member)
			.fetch();

		// 튜플 조회 - 프로젝션 대상이 둘 이상일 때
		List<Tuple> results = queryFactory
			.select(member.username, member.age)
			.from(member)
			.fetch();

		for (Tuple tuple : results) {
			String username = tuple.get(member.username);
			Integer age = tuple.get(member.age);
			System.out.println("username=" + username);
			System.out.println("age=" + age);
		}
	}

	@Test
	void projection_dto() {
		/*
			- 순수 JPA에서 DTO 조회 코드
				순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야함
				DTO의 package이름을 다 적어줘야해서 지저분함
				생성자 방식만 지원함
		 */

		List<MemberDto> jpaResult = em.createQuery(
			"select new study.querydsl.dto.MemberDto(m.username, m.age) " +
				"from Member m", MemberDto.class)
			.getResultList();

		/*
			- Querydsl 빈 생성(Bean population)
				결과를 DTO 반환할 때 사용
					1. 프로퍼티 접근
					2. 필드 직접 접근
					3. 생성자 사용
		 */

		// 프로퍼티 접근 - setter
		List<MemberDto> setterResult = queryFactory
			.select(Projections.bean(MemberDto.class,
				member.username,
				member.age))
			.from(member)
			.fetch();

		// 필드 직접 접근
		List<MemberDto> fieldResult = queryFactory
			.select(Projections.fields(MemberDto.class,
				member.username,
				member.age))
			.from(member)
			.fetch();

		QMember memberSub = new QMember("memberSub");

		// 별칭이 다를 때
		// 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
		List<UserDto> asResult = queryFactory
			.select(Projections.fields(UserDto.class,
				member.username.as("name"), // username.as("memberName") : 필드에 별칭 적용
				ExpressionUtils.as( // ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
					JPAExpressions
						.select(memberSub.age.max())
						.from(memberSub), "age")
			)).from(member)
			.fetch();

		// 생성자 사용
		List<MemberDto> constructorResult = queryFactory
			.select(Projections.constructor(MemberDto.class,
				member.username,
				member.age))
			.from(member)
			.fetch();
	}

	/*
		# 프로젝션과 결과 반환 - @QueryProjection
	 */

}
