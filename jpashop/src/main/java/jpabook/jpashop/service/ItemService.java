package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /*
        영속성 컨텍스트가 더는 관리하지 않는 엔티티 : 준영속 엔티티 (식별자를 가지고 있음)
        임의로 만든 엔티티도 식별자가 있다면 준영속 엔티티로 볼 수 있다.

        준영속 엔티티 수정방법
            1. 변경감지 (트랜잭션 내에서 데이터 변경 시 더티체킹 발생하는 것)
                - 원하는 속성만 변경 가능
            2. 병합 (merge, 준영속 -> 영속으로 변경, 식별자가 있으면 update)
                - 준영속 엔티티 식별자로 영속 엔티티 조회 -> 모든 값 교체 -> update
                - 모든 속성이 변경되기 때문에 null로 업데이트 할 위험이 있다. (단순한 경우라면 활용)
                - 병합으로 반환된 엔티티가 영속성에 관리되는 엔티티이다.

        jpa 에선 변경감지가 best practice
        merge 는 실제로 쓸일이 거의 없다.
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // 변경 감지 사용
        Item item = itemRepository.findOne(itemId);
        // 이렇게 set 으로 하는게 아닌 메소드로 만드는게 좋다. (관리, 추적에 좋은 설계)
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
