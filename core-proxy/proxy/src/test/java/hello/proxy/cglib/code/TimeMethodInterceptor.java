package hello.proxy.cglib.code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/*
    CGLIB 는 MethodInterceptor 를 제공한다.
 */
@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {

    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    /*
        obj : CGLIB가 적용된 객체
        method : 호출된 메서드
        args : 메서드를 호출하면서 전달된 인수
        proxy : 메서드 호출에 사용

        Object target : 프록시가 호출할 실제 대상
        proxy.invoke(target, args) : 실제 대상을 동적으로 호출한다.
        참고로 method 를 사용해도 되지만, CGLIB는 성능상 MethodProxy proxy 를 사용하는 것을 권장한다.
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
