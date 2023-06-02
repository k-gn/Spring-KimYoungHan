package hello.itemservice.repository;

import lombok.Data;

// DTO 를 만들게 된 이유가 해당 DTO 클래스의 패키지 위치(소속)와 연관됨
@Data
public class ItemSearchCond {

    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
