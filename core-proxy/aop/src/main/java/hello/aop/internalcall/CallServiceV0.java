package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV0 {

    // 스프링은 프록시 방식의 AOP를 사용한다. 프록시 방식의 AOP는 메서드 내부 호출에 프록시를 적용할 수 없다.
    // 객체의 내부에서 메서드 호출이 발생하면 프록시를 거치지 않고 대상 객체를 직접 호출하는 문제가 발생한다
    // 내부 호출은 프록시를 거치지 않는다. 따라서 어드바이스도 적용할 수 없다.
    public void external() {
        log.info("call external");
        internal(); //내부 메서드 호출(this.internal())
    }

    public void internal() {
        log.info("call internal");
    }
}
