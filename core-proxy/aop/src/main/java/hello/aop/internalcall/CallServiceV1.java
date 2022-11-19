package hello.aop.internalcall;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallServiceV1 {

    private CallServiceV1 callServiceV1;

    // 생성자 주입 시 순환참조로 인해 생성이 안되서 수정자 주입을 통해 자기 자신을 주입한다.
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        System.out.println(callServiceV1.getClass());
        callServiceV1.internal(); //외부 메서드 호출
    }

    public void internal() {
        log.info("call internal");
    }
}
