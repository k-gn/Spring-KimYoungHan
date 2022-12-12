package jpabook.jpashop.repository.datajpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jpabook.jpashop.domain.Member;

/*
	스프링 데이터 JPA는 JpaRepository 라는 인터페이스를 제공하는데, 여기에 기본적인 CRUD 기능이 모두 제공된다. (일반적으로 상상할 수 있는 모든 기능이 다 포함되어 있다.)

	개발자는 인터페이스만 만들면 된다. 구현체는 스프링 데이터 JPA가 애플리케이션 실행시점에 주입해준다.

	스프링 데이터 JPA는 스프링과 JPA를 활용해서 애플리케이션을 만들때 정말 편리한 기능을 많이 제공한다.
	하지만 스프링 데이터 JPA는 JPA를 사용해서 이런 기능을 제공할 뿐이다. = 결국 JPA 자체를 잘 이해하는 것이 가장 중요하다.
 */
public interface MemberRepositoryNew extends JpaRepository<Member, Long> {

	// findByName 처럼 일반화 하기 어려운 기능도 메서드 이름으로 정확한 JPQL 쿼리를 실행한다.
	List<Member> findByName(String name); // auto jpql
}
