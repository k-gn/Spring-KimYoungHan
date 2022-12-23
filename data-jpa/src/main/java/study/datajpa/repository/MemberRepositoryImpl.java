package study.datajpa.repository;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

/*
	사용자 정의 구현 클래스
		규칙: 리포지토리 인터페이스 이름 + Impl / 사용자 정의 인터페이스 명 + Impl
		스프링 데이터 JPA가 인식해서 스프링 빈으로 등록

		Impl 대신 다른 이름으로 변경하고 싶으면 (권장안함 - 이왕이면 규칙을 따르자.)
		@EnableJpaRepositories(basePackages = "study.datajpa.repository", repositoryImplementationPostfix = "Impl")

		* 실무에서는 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용

		클래스로 만들고 스프링 빈으로 등록해서 그냥 직접 사용해도 된다. 물론 이 경우 스프링 데이터 JPA와는 아무런 관계 없이 별도로 동작한다.
			핵심 비즈니스 로직 레포지토리와 화면에 맞춘 복잡한 로직의 레포지토리는 아예 분리하는게 관리하는데 좋다.

		구조 분리에 대한 고민
			1. 커맨드와 쿼리의 분리
				Query
					결과값을 반환하고, 시스템의 관찰가능한 상태를 변화시키지 않는다.
					따라서 부작용에서 자유롭다.(free of side effects)

				Command
					결과를 반환하지 않고, 대신 시스템의 상태를 변화시킨다.
			2. 핵심 비즈니스와 화면 비즈니스의 분리
			3. 라이프 사이클
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final EntityManager em;

	@Override
	public List<Member> findMemberCustom() {
		return null;
	}
}
