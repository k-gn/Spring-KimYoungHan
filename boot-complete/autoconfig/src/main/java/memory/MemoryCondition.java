package memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class MemoryCondition implements Condition { // @Conditional 기능을 사용하려면 Condition 인터페이스를 구현해야한다.
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //-Dmemory=on
        // 스프링은 외부 설정을 추상화하여 Environment 로 통합했다. => 다양한 외부 환경 설정을 Environment 하나로 읽어들일 수 있다.
        String memory = context.getEnvironment().getProperty("memory");
        log.info("memory={}", memory);
        return "on".equals(memory);
    }
}
