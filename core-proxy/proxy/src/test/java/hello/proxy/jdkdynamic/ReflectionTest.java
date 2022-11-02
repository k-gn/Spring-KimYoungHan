package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    @Test
    void reflection0() {
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); //호출하는 메서드가 다음
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB(); //호출하는 메서드가 다음
        log.info("result={}", result2);
        //공통 로직2 종료
    }

    /*
        ## 리플렉션 : 클래스의 정보를 다루는 기술

        Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello") : 클래스 메타정보를 획득한다. 내부 클래스는 구분을 위해 $ 를 사용한다.
        classHello.getMethod("call") : 해당 클래스의 call 메서드 메타정보를 획득한다.
        methodCallA.invoke(target) : 획득한 메서드 메타정보로 실제 인스턴스의 메서드를 호출한다.

        그런데 target.callA() 나 target.callB() 메서드를 직접 호출하면 되지 이렇게 메서드 정보를 획득해서 메서드를 호출하면 어떤 효과가 있을까?
        => 여기서 중요한 핵심은 클래스나 메서드 정보를 동적으로 변경할 수 있다는 점이다.
     */
    @Test
    void reflection1() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target);
        log.info("result2={}", result2);
    }

    @Test
    void reflection2() throws Exception {
        //클래스 정보
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    /*
        1. Method method : 첫 번째 파라미터는 호출할 메서드 정보가 넘어온다. 이것이 핵심이다. 기존에는
                           메서드 이름을 직접 호출했지만, 이제는 Method 라는 메타정보를 통해서 호출할 메서드 정보가 동적으로 제공된다.
        2. Object target : 실제 실행할 인스턴스 정보가 넘어온다. 타입이 Object 라는 것은 어떠한 인스턴스도 받을 수 있다는 뜻이다.
                           단, 해당 인스턴스에 해당 메서드가 존재해야한다. (없으면 예외)

        => 정적인 target.callA() , target.callB() 코드를 리플렉션을 사용해서 Method 라는 메타정보로 동적으로 사용할 수 있게 추상화함.
           (공통 로직을 만들 수 있게됨)

        리플렉션을 사용하면 클래스와 메서드의 메타정보를 사용해서 애플리케이션을 동적으로 유연하게 만들 수 있다.
        하지만 리플렉션 기술은 런타임에 동작하기 때문에, 컴파일 시점에 오류를 잡을 수 없다.
        => 부분적으로 주의해서 사용해야한다!
     */
    private void dynamicCall(Method method, Object target) throws Exception {
        log.info("start");
        // target 의 method 를 호출
        Object result = method.invoke(target);
        log.info("result={}", result);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
