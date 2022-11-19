package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 구조를 변경(분리)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallServiceV3 {

    // 아예 구조를 변경
    // 내부 호출 자체가 사라지고, callService internalService 를 호출하는 구조로 변경
    private final InternalService internalService;

    public void external() {
        log.info("call external");
        System.out.println(internalService.getClass());
        internalService.internal(); //외부 메서드 호출
    }

    // AOP는 주로 트랜잭션 적용이나 주요 컴포넌트의 로그 출력 기능에 사용된다.
    // 인터페이스에 메서드가 나올 정도의 규모에 AOP를 적용하는 것이 적당하다.
    // AOP는 public 메서드에만 적용한다. private 메서드처럼 작은 단위에는 AOP를 적용하지 않는다.
    // -> AOP 적용을 위해 private 메서드를 외부 클래스로 변경하고 public 으로 변경하는 일은 거의 없다.
    // AOP가 잘 적용되지 않으면 내부 호출을 의심해보자.
}
