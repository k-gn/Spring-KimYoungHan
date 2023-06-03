package hello.itemservice.service;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceV1 implements ItemService {

        /*
            - JPA 를 의존할 건지 이렇게 중간 어댑터(ItemRepository - 추상화)를 도입할건진 트레이드 오프다.
                - DI, OCP 를 지키기 위해 어댑터를 도입하고, 더 많은 코드를 유지하거나
                - DI, OCP를 포기하고 어댑터를 제거하고 구조를 단순하게 가져가거나
                - 구조의 안정성 vs 단순한 구조와 개발의 편리성

            - 어떤 상황엔 구조의 안정성이 중요하고, 어떤 상황엔 단순한 것이 더 나은 선택일 수 있다.
            - 프로젝트 규모와 시간 등의 제약에 따라 다를 수도 있다.
            - 미래의 기술을 변경할 가능성도 고려한다.
            - 개발을 할 때는 항상 자원이 무한한 것이 아니다.
            - 어설픈 추상화는 오히려 독이되며 추상화 인터페이스 또한 비용이 든다. (비용 - 유지보수 관점, 추상화의 흐름을 따라가는 고민)
            - 추상화 비용을 넘어설 만큼 효과가 있을 때 추상화를 도입하는 것이 실용적이다.

            - 이미 프로젝트 규모가 크고 구조적으로 개선해야 한다면?
            - 프로젝트 규모가 이제 시작이라면?
            - 추상화 vs 편리함
                -> 일단 편리하게 간단한 구조로 하다가 이 후 여러 요소가 추가되어 추상화 도입이 필요하다면 리펙토링으로 정리하는 걸 권장한다.
         */
        private final ItemRepository itemRepository;

        @Override
        public Item save(Item item) {
            return itemRepository.save(item);
        }

        @Override
        public void update(Long itemId, ItemUpdateDto updateParam) {
        itemRepository.update(itemId, updateParam);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<Item> findItems(ItemSearchCond cond) {
        return itemRepository.findAll(cond);
    }
}
