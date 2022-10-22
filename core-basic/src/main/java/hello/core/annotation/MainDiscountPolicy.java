package hello.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

// - @Qualifier 를 사용하면 문자가 컴파일 시 타입체크가 안된다. 따라서 직접 어노테이션을 만들어서 해결할 수 있다.
// 애노테이션은 상속이라는 개념이 없다. 여러 애노테이션을 모아서 사용하는 기능은 스프링이 지원해주는 기능이다.
// 무분별하게 정의하는 것은 유지보수에 더 혼란을 일으킬 수 있다.
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier("mainDiscountPolicy")
public @interface MainDiscountPolicy {
}

