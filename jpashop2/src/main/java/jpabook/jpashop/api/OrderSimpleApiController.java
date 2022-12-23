package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.*;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository; //의존관계 주입

    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore (양쪽을 서로 호출하는 순환참조 방지)
     *
     * 쓰지말자 (엔티티 노출 및 불필요한 조회로 성능 저하 / 복잡한 api 스펙)
     * 즉시로딩 사용 x - 항상 조회하기 때문에 성능 최적화 안됨 (튜닝도 힘듬)
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X) - 조인이 있어도 페치 조인이 아니라면 지연로딩이 적용됨
     * - 단점: 지연로딩으로 쿼리 N번 호출 (n+1)
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
     * - fetch join으로 쿼리 1번 호출
     * 참고: fetch join에 대한 자세한 내용은 JPA 기본편 참고(정말 중요함)
     *
     * 한번에 조회해서 가져오기 때문에 지연로딩 동작 x
     * 기본적으로 LAZY를 깔고, 필요한 경우 페치조인으로 객체그래프를 묶어 한번에 가져오면 대부분의 성능문제가 해결된다.
     *
     * 단점: 불필요한 데이터도 같이 가져오고, 엔티티로 변환하는 과정이 있다.
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());
        return result;
    }

    /**
     * V4. 가져올 때 부터 DTO로 필요한 정보만 가져오기
     *
     * 네트워크 성능 최적화 (V3와 사실 크게 성능차이가 발생하진 않음 - 인덱스가 잘못되거나, 데이터가 많을 때 등의 상황에선 성능차이가 발생함)
     * 딱 fit 하게 만들어서 재활용하기엔 힘들다 (딱딱함)
     * api 스펙에 맞춘 레포지토리 코드는 재사용성이 떨어지고, api 스펙이 바뀌면 같이 바뀌어야 한다.
     * 별도의 패키지로 분리하고 쓰면 괜찮다.
     * 레포지토리는 일반적으로 엔티티 객체 그래프를 조회하는 용도이다.
     *
     *   - 권장방식
     *      1. 우선 엔티티를 dto로 변환하는 방식 선택
     *      2. 필요 시 페치 조인으로 성능 최적화
     *      3. 그래도 안되면 dto로 직접 조회
     *      4. 최후의 방법은 네이티브 쿼리나, jdbcTemplate로 sql 직접 사용
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //지연로딩 객체 호출
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //지연로딩 객체 호출
        }
    }

    // layer 의존 관계는 한방향으로 흘러야 한다. (컨트롤러 -> 레포지토리 / 레포지토리 -> 컨트롤러)
    // 갑자기 레포지토리에서 컨트롤러를 의존한다? = 망함
}
