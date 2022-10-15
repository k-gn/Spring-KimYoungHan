package hello.core.scan.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 어노테이션 위치
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 라이프 사이클
/*
	RetentionPolicy.SOURCE : 소스 코드(.java)까지 남아있는다.
	RetentionPolicy.CLASS : 클래스 파일(.class)까지 남아있는다.(= 바이트 코드)
	RetentionPolicy.RUNTIME : 런타임까지 남아있는다.(= 사실상 안 사라진다.)
 */
@Documented
public @interface MyExcludeComponent {
}
