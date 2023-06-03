package hello.itemservice.service;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceV2 implements ItemService {

    /*
        - 스프링 데이터 JPA 기능을 최대한 살리면서, Querydsl도 편리하게 사용할 수 있는 구조. (빠르고 실용적인 구조)
            기본적인 CRUD, 단순 쿼리는 스프링 데이터 JPA가 담당하고
            복잡한 조회 쿼리는 Querydsl이 담당하는 구조.
            너무 복잡하다면 jdbcTemplate or mybatis 를 함께 사용 (거의 5% 미만)

        - JpaTransactionManager 만 있어도 jdbcTemplate, mybatis 모두를 하나의 트랜잭션으로 묶어 사용할 수 있다.
     */
    private final ItemRepositoryV2 itemRepositoryV2;
    private final ItemQueryRepositoryV2 itemQueryRepositoryV2;

    @Override
    public Item save(Item item) {
        return itemRepositoryV2.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = itemRepositoryV2.findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepositoryV2.findById(id);
    }

    @Override
    public List<Item> findItems(ItemSearchCond cond) {
        return itemQueryRepositoryV2.findAll(cond);
    }
}
