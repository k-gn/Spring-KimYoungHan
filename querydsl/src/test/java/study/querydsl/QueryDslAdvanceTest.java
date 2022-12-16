package study.querydsl;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.*;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.assertj.core.api.Assertions;
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
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberRepository;

@SpringBootTest
@Transactional
public class QueryDslAdvanceTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@Autowired
	MemberRepository memberRepository;

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
		// repository 계층에서 사용하자. (하부 구현 로직을 앞단에서 아는 건 좋지 않은 의존관계)
		// Tuple을 repository 안에서 쓰고 내보낼 땐 DTO로 정리해서 내보내는것이 좋다.
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
				기본생성자 필요
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
		- 생성자 + @QueryProjection
			- ./gradlew compileQuerydsl
			- QMemberDto 생성 확인

		이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다. 다만 DTO에 QueryDSL
		어노테이션을 유지해야 하는 점(의존관계)과 DTO까지 Q 파일을 생성해야 하는 단점이 있다.
	 */

	@Test
	void projection_annotation() {
		List<MemberDto> result = queryFactory
				.select(new QMemberDto(member.username, member.age))
				.from(member)
				.fetch();

		// distinct
		List<String> distinctResult = queryFactory
				.select(member.username).distinct()
				.from(member)
				.fetch();
	}

	/*
		동적 쿼리를 해결하는 두가지 방식
			- BooleanBuilder
			- Where 다중 파라미터 사용

		# 동적 쿼리 - BooleanBuilder 사용
	 */
	@Test
	public void 동적쿼리_BooleanBuilder() throws Exception {
		String usernameParam = "member1";
		Integer ageParam = 10;
		List<Member> result = searchMember1(usernameParam, ageParam);
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	private List<Member> searchMember1(String usernameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder(); // 생성자에서 초기값 넣기 가능
		if (usernameCond != null) {
			builder.and(member.username.eq(usernameCond));
		}
		if (ageCond != null) {
			builder.and(member.age.eq(ageCond));
		}
		return queryFactory
				.selectFrom(member)
				.where(builder)
				.fetch();
	}

	/*
		동적 쿼리 - Where 다중 파라미터 사용
	 */
	@Test
	public void 동적쿼리_WhereParam() throws Exception {
		/*
			where 조건에 null 값은 무시된다.
				null 체크는 주의해서 처리해야함 (BooleanExpression 절에 null이 들어가면 예외 발생)
			메서드를 다른 쿼리에서도 재활용 할 수 있다.
			쿼리 자체의 가독성이 높아진다.
		 */
		String usernameParam = "member1";
		Integer ageParam = 10;
		List<Member> result = searchMember2(usernameParam, ageParam);
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	private List<Member> searchMember2(String usernameCond, Integer ageCond) {
		return queryFactory
				.selectFrom(member)
				.where(usernameEq(usernameCond), ageEq(ageCond))
				.fetch();
	}
	private BooleanExpression usernameEq(String usernameCond) {
		return usernameCond != null ? member.username.eq(usernameCond) : null;
//		return member.username.eq(usernameCond);
	}

	private BooleanExpression ageEq(Integer ageCond) {
		return ageCond != null ? member.age.eq(ageCond) : null;
	}

	// 조합 가능 (유용하다.)  isValid + dateIn = isServiceable 이런 느낌
	private BooleanExpression allEq(String usernameCond, Integer ageCond) {
		return usernameEq(usernameCond).and(ageEq(ageCond));
	}

	/*
		# 수정, 삭제 벌크 연산

		변경감지는 개별데이터 건건으로 쿼리가 발생한다. -> 한번에 처리하고 싶을 때 (성능 up) = 벌크연산!
		JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에 배치 쿼리를
		실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다. (안그러면 서로 데이터가 다른 현상 발생)
	 */
	@Test
	void bulk_calc() {
		// 쿼리 한번으로 대량 데이터 수정
		long count1 = queryFactory
				.update(member)
				.set(member.username, "비회원")
				.where(member.age.lt(28))
				.execute();

		// 영속성 비우기
		em.flush();
		em.clear();

		// 기존 숫자에 1 더하기
		long count2 = queryFactory
				.update(member)
				.set(member.age, member.age.add(1)) // 곱하기: multiply(x)
				.execute();

		// 쿼리 한번으로 대량 데이터 삭제
		long count3 = queryFactory
				.delete(member)
				.where(member.age.gt(18))
				.execute();
	}

	/*
		# SQL function 호출하기
		- SQL function은 JPA와 같이 Dialect에 등록된 함수만 호출할 수 있다.
		- Expressions 을 통해 사용
	 */
	@Test
	void test() {
		// member -> M으로 변경하는 replace 함수 사용
		String result1 = queryFactory
				.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})", member.username, "member", "M"))
				.from(member)
				.fetchFirst();

		// 소문자로 변경해서 비교해라.
		String result2 = queryFactory
		.select(member.username)
				.from(member)
				.where(member.username.eq(Expressions.stringTemplate("function('lower', {0})", member.username)))
				//.where(member.username.eq(member.username.lower())) // lower 같은 ansi 표준 함수들은 querydsl이 상당부분 내장하고 있다.
				.fetchFirst();
	}
}
