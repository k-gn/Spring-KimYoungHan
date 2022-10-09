package hello.core.member;

public class MemberServiceImpl implements MemberService {

//    private final MemberRepository memberRepository = new MemoryMemberRepository();
    /*
        생성자 주입을 활용하여 추상화에만 의존하도록 변경
        구체적인 애들을 이젠 전혀 모른다. (구현객체와의 의존성이 사라짐 - DIP 와 OCP 를 지킨다.)
        오직 외부에 의해 구체적인 애들이 결정된다.
        의존관계에 대한 고민은 외부에 맞기고 나는 실행에만 집중

        의존관계를 외부에서 주입 -> DI
        
        => 관심사의 분리 : 객체를 생성하고 연결하는 역할과 실행하는 역할을 명확히 분리했다.
     */
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
