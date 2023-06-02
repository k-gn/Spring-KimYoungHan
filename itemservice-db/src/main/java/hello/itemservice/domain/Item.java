package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Item {

    /*
        # 기본키 선택 전략
            - 자연 키(전화번호, 주민번호 등) 사용은 지양 (언제든지 변할 수 있다 / 유니크 조건 활용)
            - 대리 키를 사용하자 (비즈니스와 관련 없는 임의의 키)
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
