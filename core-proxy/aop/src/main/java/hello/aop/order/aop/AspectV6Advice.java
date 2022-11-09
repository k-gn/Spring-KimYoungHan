package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/*
    모든 어드바이스는 org.aspectj.lang.JoinPoint 를 첫번째 파라미터에 사용할 수 있다. (생략해도 된다.)
    단 @Around 는 ProceedingJoinPoint 을 사용해야 한다.
    또한 proceed() 가 호출되어야 다음 대상이 호출된다.

   # JoinPoint 인터페이스의 주요 기능
    getArgs() : 메서드 인수를 반환합니다.
    getThis() : 프록시 객체를 반환합니다.
    getTarget() : 대상 객체를 반환합니다.
    getSignature() : 조언되는 메서드에 대한 설명을 반환합니다.
    toString() : 조언되는 방법에 대한 유용한 설명을 인쇄합니다.

    # ProceedingJoinPoint 인터페이스의 주요 기능
     proceed() : 다음 어드바이스나 타켓을 호출한다.
 */
@Slf4j
@Aspect
public class AspectV6Advice {

    /*
        메서드의 실행의 주변에서 실행된다.
        메서드 실행 전후에 작업을 수행한다.

        가장 강력한 어드바이스
            조인 포인트 실행 여부 선택 joinPoint.proceed() 호출 여부 선택
            전달 값 변환: joinPoint.proceed(args[])
            반환 값 변환
            예외 변환
            트랜잭션 처럼 try ~ catch~ finally 모두 들어가는 구문 처리 가능

        어드바이스의 첫 번째 파라미터는 ProceedingJoinPoint 를 사용해야 한다.
            proceed() 를 통해 대상을 실행한다.
            proceed() 를 여러번 실행할 수도 있음(재시도)
     */
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            //@Before
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed(); // 여러번 proceed() 호출 가능
            //@AfterReturning
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            //@AfterThrowing
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            //@After
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        log.info("[before] {}", joinPoint.getSignature());
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) { // 결과값은 올바른 리턴타입이어야 한다.
        log.info("[return] {} return={}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) { // 올바른 예외타입이어야 한다.
        log.info("[ex] {} message={}", ex);
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()") // = finally, 일반적으로 리소스 해제에 사용한다.
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }

}
