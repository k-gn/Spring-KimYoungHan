package study.datajpa.repository;

import java.util.List;

import study.datajpa.entity.Member;

/*
	# 사용자 정의 리포지토리 구현
	- 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
	- 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
	- 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
		1. JPA 직접 사용( EntityManager )
		2. 스프링 JDBC Template 사용
		3. MyBatis 사용
		4. Querydsl 사용
		5. 데이터베이스 커넥션 직접 사용 등등...

 */
public interface MemberRepositoryCustom {

	List<Member> findMemberCustom();
}
