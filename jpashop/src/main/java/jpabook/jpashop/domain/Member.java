package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// 실무에서는 setter 는 필요한 경우에 추가해주자. setter 를 막 열어두면 엔티티 변경 추적이 점점 힘들어진다.
// 따라서 엔티티 데이터 변경 시 setter 대신 따로 메서드를 별도로 제공하는것이 더 좋다.
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    /*
        테이블은 외래키만 관리하면 되지만 객체는 두곳을 관리해야 한다.
        어디에서 연관관계 객체값이 변경되었을 때 외래키를 바꿔야 하는지 JPA는 모른다.
        JPA에게 외래키 업데이트에 대한 혼동을 주지 않기 위해 주인이라는 개념을 만들었다.

        컬렉션 필드는 바로 초기화하는 것이 안전하다.
            - null 문제 때문에 안전하다.
            - 하이버네이트는 엔티티 영속화 시 컬렉션을 감싸서 자신이 관리하는 내장 컬렉션으로 변경한다.
            - 만약 컬렉션을 잘못 생성하거나 바뀌면 내부 매커니즘에 문제가 발생할 수 있다.
            - 따라서 필드레벨에서 생성하는 것이 가장 안전하고 간결하다.
            - 가급적 컬렉션 생성 후 바꾸지말자
     */
    @JsonIgnore
    @OneToMany(mappedBy = "member") // 주인이 아닌곳에선 읽기전용이 된다.
    private List<Order> orders = new ArrayList<>();

}
