package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // controller + responseBody
@RequiredArgsConstructor
// 패키지 기준으로 공통처리 하는 경우가 있기 때문에 차이가 있는 것들은 분리해주는 것이 좋다.
public class MemberApiController {

    private final MemberService memberService;

    // 화면을 내리는게 아니라 api 를 통해 데이터를 넘겨준다.
    // 엔티티로 받고 응답하는 것의 궁극적인 단점은 변경이 발생 시 관리 및 대응하기 힘들고, 협업 간 API의 명확한 데이터 파악이 힘든 점 같다.
    /*
        View와 통신하는 Dto 클래스는 자주 변경이 된다 ( UI 요건에 따라서 )
        하지만, 테이블에 매핑되는 Entity는 그에 비해 변경도 적고, 영향범위도 매우 크다
        테이블에 매핑되는 정보가, 실제 View에서 원하는 정보와 다를 수 있다.
        ( 이러한 경우에는 변환하는 로직이 필요한데, 같이 쓰게 되면 해당 로직이 Entity에 들어가게 되어서 Entity가 지저분해진다 )
        DB로부터 조회된 모든 Entity를 View로 넘기게 되면, 원하지 않는 정보까지 전달하게 되어 정보 노출에 대한 문제가 생길 수 있고,
        이를 막기 위한 비즈니스 로직과는 상관없는 방어 로직들이 생기게 된다
     */
    /**
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 다른 사람 입장에서 이 API 가 어떤 값을 필요로 요청받는지 판단하기 힘들다.
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *   - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등) -> 객체 설계를 망가뜨리게 되고, 점점 관리 및 예측하기 힘들어진다.
     *   - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     * - 엔티티를 외부에 노출하는 건 안하는게 좋다.
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // @RequestBody : json body mapping
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // 권장
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정 API
     *
     * 멱등 : 어떤 조작을 몇 번을 반복해도 결과가 동일한 것 (post를 제외한 전부가 멱등)
     * get의 파라미터, put의 데이터, delete의 파라미터가 동일할 때 각 메소드는 멱등하게 기능한다.
     * 멱등성이 보장되지 않는 메소드는 로직단에서 중복 검사를 통해 사용자 피해를 예방하는 방어코드를 추가해주는 것이 좋다.
     * 서버-클라이언트간 통신에 이상이 있어서 재요청을 해야하는 자동 복구 메커니즘에서 멱등을 보장하는 메소드와 아닌 것을 구분할 수 있어야 한다
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 조회 V1: 응답 값으로 엔티티를 직접 외부에 노출한다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *   - 기본적으로 엔티티의 모든 값이 노출된다.
     *   - 응답 스펙을 맞추기 위해 로직이 추가된다. (@JsonIgnore, 별도의 뷰 로직 등등)
     *   - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 프레젠테이션 응답 로직을 담기는 어렵다.
     * - 엔티티가 변경되면 API 스펙이 변한다.
     * - 추가로 컬렉션을 직접 반환하면 항후 API 스펙을 변경하기 어렵다.(별도의 Result 클래스 생성으로 해결)
     * 결론
     * - API 응답 스펙에 맞추어 별도의 DTO를 반환한다.
     * - 필요한 것만 노출하자.
     */
    //조회 V1: 안 좋은 버전, 모든 엔티티가 노출, @JsonIgnore -> 이건 정말 최악, api가 이거 하나인가! 화면에 종속적이지 마라!
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    /**
     * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다. (권장)
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {

        List<Member> findMembers = memberService.findMembers();
        //엔티티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    /*
        클래스에 static을 붙일 경우에는 내부 클래스에 붙이는 경우가 많다.
        내부 클래스에 static을 붙이는 이유는 외부 클래스의 인스턴스 생성없이 내부 클래스를 접근하기 위한 용도이다.
        static 내부 클래스로 선언하면 메모리 누수의 일반적인 원인을 예방할 수 있고, 클래스의 각 인스턴스당 더 적은 메모리를 사용한다.
        => 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 정적 멤버 클래스로 만들자.

        데이터만 왓다갔다 하는 용도의 DTO는 편하게 @Data 를 많이 쓴다. (실용적)
     */
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private long count; // 유연성 증가 (값이 추가되도 크게 문제없음)
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}