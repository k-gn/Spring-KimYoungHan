package hello.order;

import java.util.concurrent.atomic.AtomicInteger;

/*
    # 커스텀 메트릭
        - 비즈니스에 특화된 부분을 모니터링 하고 싶을 때
            ex. 주문수, 취소수, 재고 수량 등
        - 시스템 운영에 상당히 도움이 되는 메트릭을 만들 수 있다.
            - 비즈니스 문제 파악 가능

 */
public interface OrderService {
    void order();
    void cancel();
    AtomicInteger getStock(); // multi thread 고려
}
