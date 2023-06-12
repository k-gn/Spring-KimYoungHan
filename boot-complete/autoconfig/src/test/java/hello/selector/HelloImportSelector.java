package hello.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/*
    # ImportSelector
    - 동적으로 설정 정보를 선택할 수 있는 인터페이스
 */
public class HelloImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 문자열 안에 문자들에 맞는 대상을 설정 정보로 사용한다.
        return new String[]{"hello.selector.HelloConfig"};
    }
}
