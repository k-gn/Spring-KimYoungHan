package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/*
     Controller단에서 @Transactional이 안먹히는 이유는 Spring AOP 때문이다.
     Spring AOP는 기본적으로 다이내믹 프록시 기법을 사용하는데 이 프록시를 적용하려면 인터페이스가 필요하다.
     일반적으로 Controller는 인터페이스가 없기때문에 적용되지 않았던 것이다.

     Controller 단에 @Transactional 어노테이션이 필요한 경우는 대부분 코드가 지저분하고,
     MVC 모델에도 맞지 않고, 클래스간의 의존성 및 중복 코딩이 여기저기 널부러져 있는 경우가 대부분이다.
     따라서 리팩토링을 통해 Service 클래스에 트랜잭션이 필요한 비지니스 로직을 작성하고,
     Controller 클래스에서는 단순히 HTTP 요청에 맞게 비지니스 로직을 호출하는 구조로 변경하자.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);
        
        //주문상품 생성
        //주문상품이 여러개일 경우 item 과 orderItem 을 컬렉션으로 변경하면 된다. (받는 파라미터도 itemId, count를 가진 객체의 컬렉션)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        System.out.println("---삭제할 주문 조회---");
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    //검색
    //이렇게 단순 위임일 경우 컨트롤러에서 바로 호출해도 괜찮다.
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
