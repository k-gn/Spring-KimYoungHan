package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 가급적 모든 연관관계는 일단 지연로딩으로 설정하자. (즉시로딩은 예측과 추적이 어렵고 jpql에서 N+1 이슈가 자주 일어난다.)
    // 연관된 엔티티를 함께 조회해야 하면 fetch join 이나 엔티티그래프 기능을 사용한다.
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
         Hibernate 에서는 양방향 OneToOne 관계에서는 지연로딩이 동작하지 않는다.
         정확하게는 테이블을 조회할 때 외래 키를 갖고 있는 테이블(연관 관계의 주인)에서는 지연로딩이 동작하지만,
         mappedBy로 연결된 반대편 테이블은 지연로딩이 동작하지 않고 N + 1 쿼리가 발생한다.
         ->  JPA의 구현체인 Hibernate 에서 프록시 기능의 한계로 지연 로딩을 지원하지 못하기 때문에 발생

         DB에 있는 테이블은 연관관계의 주인이 아니기 때문에 외래 키를 관리하지 않기 때문에 외래키 필드는 존재하지 않는다. (null)
         프록시 객체를 만들기 위해서는 연관 객체에 값이 있는지 없는지 알아야 한다.
         연관관계의 주인이 아닌 테이블에선 주인의 값을 알기 위해 무조건 조회를 해야한다.

         반면 @OneToMany 관계는 빈 컬렉션이 초기화될 때(new ArrayList<>() 할 때) Proxy가 생긴다.
         null이 아니고 size 자체가 0일 수 있는 것이기 때문에 @OneToMany 관계는 @OneToOne과 다르게 Lazy Loading이 가능하다.

         ---

         cascade - 생명주기를 주 테이블과 동일하게 하기
         주테이블만 가지고있는(다른데선 참조하지 않는) 관계일 경우 사용하면 좋다.
     */
    @JsonIgnore
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    /*
        하이버네이트 기본 매핑 전략 : SpringPhysicalNamingStrategy
            1. 카멜케이스 -> 언더스코어
            2. . -> _
            3. 대문자 -> 소문자

        네이밍 전략은 따로 수정해줄 수 있다.

        논리명 : 명시적으로 컬럼, 테이블명을 적지 않으면 ImplicitNamingStrategy
        물리명 : 모든 논리명에 적용 + 실제 테이블에 적용 (회사룰로 바꿀 수 있다)
     */
    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 설정 메서드 - 순수 객체지향적으로 양쪽에 매핑해주는게 좋다. (양방향 관계에서 두쪽다 들어가는게 당연하긴함)==//
    // 사람이기 때문에 연관관계 로직을 까먹을 수 있어서 이런 메소드를 만드는 것을 권장한다.
    // 사실 getter setter 관례 때문에 어떠한 로직이 같이 있다면 set 보단 changeMember 같은 이름이 더 적절하다.
    // 1에 만들지 n에 만들지는 상황마다 다르다. (양쪽에 같이 만들면 문제가 발생할 수 있다.)
    public void setMember(Member member) {
        this.member = member;
        // 컬렉션에 넣을 때 중복된 값 검증 등의 로직이 필요할 수 있다.
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 이런식의 주문 생성 같은 기능은 주문 객체가 가지고 있는 것이 관리하기 좋다.
    // 실제로 이런 생성메소드는 더 복잡할 수 있다.
    // 인스턴스 멤버를 사용하지 않는다면 static 을 선언하는 것을 고려하자 (new 로 생성없이 사용 가능하고, 속도도 더 빠름)
    // static은 전역적으로 쉽게 재사용하는 멤버나 잘 변하지 않는 변수나, 메소드를 사용할때 주로 사용 - 사용성이 좋다.
    // 단, static영역을 너무 자주 사용하면 GC가 관리 하지 않기에 과부하를 일으킬 수 있다.
    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        System.out.println("---주문 상태 삭제로 변경---");
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            // 주인이 아닌곳에서 관계 자체가 아닌 값을 수정하는 건 가능한듯
            System.out.println("---주문 상품 삭제 시작---");
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
