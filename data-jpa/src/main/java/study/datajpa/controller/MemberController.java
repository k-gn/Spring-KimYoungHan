package study.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		Member member = memberRepository.findById(id).get();
		return member.getUsername();
	}

	/*
		# 도메인 클래스 컨버터
		HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
		HTTP 요청은 회원 id를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환
		도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티를 찾음 (스프링 데이터가 제공)

		주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 간단한 조회용으로만 사용해야 한다.
		(트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)
	 */
	@GetMapping("/members2/{id}")
	public String findMember(@PathVariable("id") Member member) {
		return member.getUsername();
	}

	/*
		# Web 확장 - 페이징과 정렬
		스프링 데이터가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용할 수 있다.

		파라미터로 Pageable 을 받을 수 있다.
		Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성

		요청 파라미터
		예) /members?page=0&size=3&sort=id,desc&sort=username,desc

		- 기본값
		글로벌 설정: 스프링 부트
			spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
			spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/
		개별 설정
			@PageableDefault 어노테이션을 사용
				@PageableDefault(size = 12, sort = “username”, direction = Sort.Direction.DESC) Pageable pageable
	 */
	@GetMapping("/members")
	public Page<MemberDto> list(Pageable pageable) {
		/*
			- Page 내용을 DTO로 변환하기
				엔티티를 API로 노출하면 다양한 문제가 발생한다. 그래서 엔티티를 꼭 DTO로 변환해서 반환해야 한다.
					내부 설계를 다 노출함, API 스펙이 바뀔 수 있음
				Page는 map() 을 지원해서 내부 데이터를 다른 것으로 변경할 수 있다.
		 */
		// Page<Member> page = memberRepository.findAll(pageable);
		// Page<MemberDto> memberDtos = page.map(MemberDto::new);
		return memberRepository.findAll(pageable).map(MemberDto::new); // 코드 최적화
	}

	/*
		- 페이징 정보가 둘 이상이면 접두사로 구분 @Qualifier 에 접두사명 추가 "{접두사명}_xxx”
		예제: /members?member_page=0&order_page=1

		public String list(
		  @Qualifier("member") Pageable memberPageable,
		  @Qualifier("order") Pageable orderPageable, ...) {}

	  	- Page를 1부터 시작하기
		스프링 데이터는 Page를 0부터 시작한다. 만약 1부터 시작하려면?
		    1. Pageable, Page를 파리미터와 응답 값으로 사용히지 않고, 직접 클래스를 만들어서 처리한다.
		   	  그리고 직접 PageRequest(Pageable 구현체)를 생성해서 리포지토리에 넘긴다.
		      물론 응답값도 Page 대신에 직접 만들어서 제공해야 한다.

			2. spring.data.web.pageable.one-indexed-parameters 를 true 로 설정한다.
			   그런데 이 방법은 web에서 page 파라미터를 -1 처리 할 뿐이다.
			   따라서 응답값인 Page 에 모두 0 페이지 인덱스를 사용하는 한계가 있다.
	 */

	@PostConstruct
	public void init() {
		for (int i = 0; i < 100; i++) {
			memberRepository.save(new Member());
		}
	}
}
