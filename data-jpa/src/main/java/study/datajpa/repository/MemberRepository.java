package study.datajpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import study.datajpa.entity.Member;

/*
	- 스프링 데이터 JPA가 구현 클래스 대신 생성

	- org.springframework.data.repository.Repository 를 구현한 클래스는 스캔 대상
		MemberRepository 인터페이스가 동작한 이유? 프록시!
		실제 출력(Proxy) -> memberRepository.getClass() class com.sun.proxy.$ProxyXXX

	- @Repository 애노테이션 생략 가능
		컴포넌트 스캔을 스프링 데이터 JPA가 자동으로 처리
		JPA 예외를 스프링 예외로 변환하는 과정도 자동으로 처리

	- 순수 JPA로 구현한 MemberJpaRepository 대신에 스프링 데이터 JPA가 제공하는 공통 인터페이스 사용

	# Generic
		T: 엔티티 타입
		ID: 식별자 타입(PK)
		S : 엔티티와 그 자식 타입

	# 주의
		T findOne(ID) -> Optional<T> findById(ID) 변경
		boolean exists(ID) -> boolean existsById(ID) 변경

	# 주요 메서드
		save(S) : 새로운 엔티티는 저장하고 이미 있는 엔티티는 병합한다.
		delete(T) : 엔티티 하나를 삭제한다. 내부에서 EntityManager.remove() 호출
		findById(ID) : 엔티티 하나를 조회한다. 내부에서 EntityManager.find() 호출
		getOne(ID) : 엔티티를 프록시로 조회한다. 내부에서 EntityManager.getReference() 호출
		findAll(...) : 모든 엔티티를 조회한다. 정렬( Sort )이나 페이징( Pageable ) 조건을 파라미터로 제공할 수 있다.

	# 쿼리 메소드 기능 (스프링 데이터 JPA가 제공하는 마법 같은 기능)
		1. 메소드 이름으로 쿼리 생성
			- 메소드 이름을 분석해서 JPQL 쿼리 실행
		2. NamedQuery
		3. @Query (레포지토리 메소드에 쿼리 정의)
		4. 파라미터 바인딩
		5. 반환 타입
		6. 페이징과 정렬
		7. 벌크성 수정 쿼리
		8. @EntityGraph

	쿼리 메소드 필터 조건
	스프링 데이터 JPA 공식 문서 참고: (https://docs.spring.io/spring-data/jpa/docs/current/ reference/html/#jpa.query-methods.query-creation)

	 이 기능은 엔티티의 필드명이 변경되면 인터페이스에 정의한 메서드 이름도 꼭 함께 변경해야 한다.
	 그렇지 않으면 애플리케이션을 시작하는 시점에 오류가 발생한다.
	 이렇게 애플리케이션 로딩 시점에 오류를 인지할 수 있는 것이 스프링 데이터 JPA의 매우 큰 장점이다.

	 스프링 데이터 JPA를 사용하면 실무에서 Named Query를 직접 등록해서 사용하는 일은 드물다. 대신 @Query 를 사용해서 리파지토리 메소드에 쿼리를 직접 정의한다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsername(String username);

	/*
		@org.springframework.data.jpa.repository.Query 어노테이션을 사용
		JPA Named 쿼리처럼 애플리케이션 실행 시점에 문법 오류를 발견할 수 있음(매우 큰 장점!)
		실무에서는 메소드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 메서드 이름이 매우 지저분해진다. 따라서 @Query 기능을 자주 사용하게 된다.
	 */
	@Query("select m from Member m where m.username = :username")
	List<Member> findUser(@Param("username") String username);

	/*
		@Query, 값, DTO 조회하기

		단순히 값 하나를 조회
		JPA 값 타입( @Embedded )도 이 방식으로 조회할 수 있다.
	 */
	@Query("select m.username from Member m")
	List<String> findUsernameList();

	/*
		# DTO로 직접 조회
		주의!
		- DTO로 직접 조회 하려면 JPA의 new 명령어를 사용해야 한다. 그리고 다음과 같이 생성자가 맞는 DTO가 필요하다. (JPA와 사용방식이 동일하다.)

		@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " +
			  "from Member m join m.team t")
	    List<MemberDto> findMemberDto();
	 */

	/*
		파라미터 바인딩
		-  코드 가독성과 유지보수를 위해 이름 기반 파라미터 바인딩을 사용하자
	 */
	@Query("select m from Member m where m.username = :name")
	Member findMembers(@Param("name") String username);

	// 컬렉션 바인딩
	@Query("select m from Member m where m.username in :names")
	List<Member> findByNames(@Param("names") List<String> names);

	/*
		- 반환타입
		https://docs.spring.io/spring-data/jpa/docs/current/reference/ html/#repository-query-return-types

		컬렉션
			결과 없음: 빈 컬렉션 반환

		단건 조회
			결과 없음: null 반환
			결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생
				추가로 스프링이 예외를 추상화해준다.

		List<Member> findByUsername(String name); //컬렉션
		Member findByUsername(String name); //단건
		Optional<Member> findByUsername(String name); //단건 Optional
	 */

	/*
		# 스프링 데이터 JPA 페이징과 정렬
		- 페이징과 정렬 파라미터
			org.springframework.data.domain.Sort : 정렬 기능
			org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)

		- 특별한 반환 타입
			org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
			org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능 (내부적으로 limit + 1조회)
				- +1 값으로 다음 페이지 존재 여부 파악
			List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

		개발자는 페이징이나 count 같은 부수적인 요소가 아닌 핵심 비즈니스 로직에만 집중할 수 있다!

		Page<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 (전통적인 페이징)
		Slice<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함 (더보기 버튼 같은 페이징)
		List<Member> findByUsername(String name, Pageable pageable); //count 쿼리 사용 안함
		List<Member> findByUsername(String name, Sort sort);

		count 쿼리를 호출한다 = 페이징 계산을 해야한다.
		두 번째 파라미터로 받은 Pageable 은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한 org.springframework.data.domain.PageRequest 객체를 사용한다.
		PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력한다. 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다.
			PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
				- 정렬 조건이 복잡할 경우 위 코드만으론 잘 안풀리기 때문에 실제 jpql 에서 작업하는게 낫다.

		참고로 페이지는 0부터 시작한다.
		참고: count 쿼리를 다음과 같이 분리할 수 있음 (전체 count 쿼리는 매우 무겁다.)
		  @Query(value = “select m from Member m left join m.team t”,
				 countQuery = “select count(m.username) from Member m”) // count 쿼리는 조인을 할 필요가 없길래 별도로 선언한 것!
		  Page<Member> findMemberAllCountBy(Pageable pageable);


		페이지를 유지하면서 엔티티를 DTO로 변환하기 (엔티티를 노출하면 안되서 변환해주는 작업이 필요하다.)
		Page<Member> page = memberRepository.findByAge(10, pageRequest);
    	Page<MemberDto> dtoPage = page.map(m -> new MemberDto(...));

    	간단한 경우
	    List<Member> findTop3By();

		** 개발간 p6 라이브러리로 jpa 쿼리의 값들을 편하게 볼 수 있다.


	 */
}
