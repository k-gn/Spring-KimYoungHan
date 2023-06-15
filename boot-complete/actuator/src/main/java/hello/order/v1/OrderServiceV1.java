package hello.order.v1;

import hello.order.OrderService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OrderServiceV1 implements OrderService {

    private final MeterRegistry registry; // MeterRegistry : 마이크로미터 기능을 제공하는 핵심 컴포넌트
    
    private AtomicInteger stock = new AtomicInteger(100);

    public OrderServiceV1(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet();

        /*
            # Counter
            - 단조롭게 증가하는 단일 누적 측정 항목
                - 단일 값
                - 보통 하나씩 증가
                - 누적이므로 전체 값 포함
            - 값을 증가하거나 0으로 초기화 하는 것만 가능
            - 마이크로미터에서 값을 감소하는 기능도 지원하지만, 목적에 맞지 않음
            - ex. http 요청수
            - 프로메테우스에선 "." -> "_" 로 변경된다.
                - 카운트는 마지막에 "_total" 을 붙인다.
         */
        Counter.builder("my.order") // metric name
                .tag("class", this.getClass().getName())
                .tag("method", "order")
                .description("order")
                .register(registry).increment();
    }

    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();

        Counter.builder("my.order")
                .tag("class", this.getClass().getName())
                .tag("method", "cancel")
                .description("order cancel")
                .register(registry).increment();
    }

    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}
