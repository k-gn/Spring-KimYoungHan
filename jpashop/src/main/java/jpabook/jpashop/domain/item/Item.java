package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    /*
        좋은 설계는 높은 응집도와 낮은 결합도를 가지도록 구성하게 하도록 배치하는 것
            응집도가 높으면, 변경 대상과 범위가 명확해지는 장점이 있어서 코드를 수정하기 쉬워진다.
            결합도는 낮을수록 검토해야되는 소스의 수가 적어져서 코드를 수정하기가 쉬워진다.

        응집도 낮은 클래스의 문제점은
            이해하기가 힘들고, 재사용이 힘들다. 또한 유지보수가 매우 쉽지않으며 클래스 변화에 민감하다
        결합도가 낮은 클래스의 문제점은
            클래스의 규모가 커지기 때문에 이해 하기 쉽지 않으며, 변화에 따른 다른 요소들의 변경을 예측하기 쉽지 않다.

        도메인 주도 설계 : 엔티티 자체가 해결할 수 있는 것들은 엔티티 내부에 비즈니스 로직을 넣는것이 객체지향적이다.
        데이터를 가지고 있는 곳에서 비즈니스 로직이 나가는 것이 응집도가 높다.
            밖에서 계산해서 setter 에 넣어주는식으로 하는 것이 아니라 안에서 해당 기능을 제공하면 된다.
     */
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
