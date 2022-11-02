package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

public class OrderControllerConcreteProxy extends OrderControllerV2 {

    private final OrderControllerV2 target;
    private final LogTrace logTrace;

    public OrderControllerConcreteProxy(OrderControllerV2 target, LogTrace logTrace) {
        // 클래스 기반 프록시의 단점 : 자바 기본 문접으로 인해 자식 클래스 생성 시 부모 클래스의 생성자를 호출해야 한다.
        super(null);
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderController.request()");
            //target 호출
            String result = target.request(itemId);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    @Override
    public String noLog() {
        return target.noLog();
    }
}

/*
    클라이언트가 요청한 결과를 서버에 직접 요청하는 것이 아니라 어떤 대리자를 통해서 대신 간접적으로 요청하는 것
    이때 대리자를 프록시 라고 한다.

    간접 호출 시 대리자는 중간에서 여러 작업을 할 수 있다. (캐싱, 접근제어, 부가기능, 지연로딩, 프록시 체인 등)

    # 대체 가능
        클라이언트는 서버에 요청을 한건지 프록시에 요청을 한건지 몰라야 한다.
        쉽게 말해 서버와 프록시는 같은 인터페이스를 사용해야 한다.
        서버 객체를 프록시 객체로 변경해도 코드를 변경하지 않고 동작할 수 있다.

    의도에 따라 프록시 패턴과 데코레이터 패턴으로 나뉜다.
    다른 개체에 대한 접근제어 : 프록시
    새로운 기능추가, 확장 : 데코레이터
 */