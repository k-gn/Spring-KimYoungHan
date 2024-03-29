package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        // model 을 통해 화면에 데이터 전달
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    /*
        그냥 엔티티로 받을 수도 있지만 MemberForm으로 받은 이유는
        실제로 엔티티와 받는 데이터의 양식 차이가 크기 때문에 딱 필요한 dto를 만드는 것이 좋다.
        엔티티는 비즈니스 로직 외에 최대한 순수하게 유지되는 것이 관리하는데 좋다.
     */
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        // @Valid 이후 BindingResult가 있으면 오류가 있을 때 BindingResult에 담기면서 예외가 발생하지 않음
        if (result.hasErrors()) {
            // 이 때 스프링이 BindingResult를 폼에서 쓸 수 있게 해준다. (에러 메시지 핸들링 가능)
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        // 사실 여기서도 엔티티를 반환하는것 보다 필요한 애들만 추출한 dto 로 변환하여 반환하는 것이 좋다.
        // 추가로 api를 만들땐 절대 엔티티를 반환하면 안된다. 왜? -> 엔티티 변경 시 api 스펙도 함께 변해지기 때문에 사용하는 입장에서 불안전하고, 불편하다.
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

}
