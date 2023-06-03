package hello.springtx.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    //JPA는 트랜잭션 커밋 시점에 Order 데이터를 DB에 반영한다.
    @Transactional
    public void order(Order order) throws NotEnoughMoneyException {
        log.info("order 호출");
        orderRepository.save(order);

        log.info("결제 프로제스 진입");
        if (order.getUsername().equals("예외")) {
            log.info("시스템 예외 발생");
            throw new RuntimeException("시스템 예외");

        } else if (order.getUsername().equals("잔고부족")) {
            log.info("잔고 부족 비즈니스 예외 발생");
            order.setPayStatus("대기");
            // # 체크 예외 활용 예제 (NotEnoughMoneyException)
            // - 커밋은 시켜 주문정보를 살리고, 주문 상태를 대기로 만드는 비즈니스 처리
            // - 별도의 계좌로 입금처리 하도록 고객에게 알림을 주는 방식 등으로 주문을 처리할 수 있게 된다.
            // - 체크 예외가 아닌 어떤 상태값을 응답해주는 방식으로 사용해도 되긴 하다.
            throw new NotEnoughMoneyException("잔고가 부족합니다");
        } else {
            //정상 승인
            log.info("정상 승인");
            order.setPayStatus("완료");
        }
        log.info("결제 프로세스 완료");
    }
}
