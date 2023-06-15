package hello.order.gauge;

import hello.order.OrderService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    # 게이지
        - 임의로 오르내릴 수 있는 값
        - 현재 상태를 보는데 사용
            ex. 차량 속도, cpu, memory 사용량
 */
@Configuration
public class StockConfigV1 {

    @Bean
    public MyStockMetric myStockMetric(OrderService orderService, MeterRegistry registry) {
        return new MyStockMetric(orderService, registry);
    }

    @Slf4j
    static class MyStockMetric {
        private OrderService orderService;
        private MeterRegistry registry;

        public MyStockMetric(OrderService orderService, MeterRegistry registry) {
            this.orderService = orderService;
            this.registry = registry;
        }

        @PostConstruct
        public void init() {
            // 게이지 등록
            Gauge.builder("my.stock", orderService, service -> {
                // 외부에서 메트릭을 확인할 때 마다 호출되는 람다 함수
                log.info("stock gauge call");
                // 반환 값이 게이지의 값이다.
                return service.getStock().get();
            }).register(registry);
        }
    }
}
