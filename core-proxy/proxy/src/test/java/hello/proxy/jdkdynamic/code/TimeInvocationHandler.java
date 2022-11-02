package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class TimeInvocationHandler implements InvocationHandler { // InvocationHandler 를 구현해야 한다.

    private final Object target;

    public TimeInvocationHandler(Object target) {
        this.target = target;
    }

    /*
        Object proxy : 프록시 자신
        Method method : 호출한 메서드
        Object[] args : 메서드를 호출할 때 전달한 인수
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = method.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;
        log.info("TimeProxy 종료 resultTime={}", resultTime);
        return result;
    }
}
