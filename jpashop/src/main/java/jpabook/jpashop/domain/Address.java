package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

/*
    값타임은 기본적으로 변경이 되면 안된다. (setter 없음)

    기본적으로 클래스는 타당한 이유가 없다면 불변으로 설계하는 것이 좋다.
    불변객체 :  객체 생성 이후 내부의 상태가 변하지 않는 객체 (ex. Integer, String)

    왜?
        1. Thread-Safe하여 병렬 프로그래밍에 유용하며, 동기화를 고려하지 않아도 된다.
        2. 실패 원자적인(Failure Atomic) 메소드를 만들 수 있다.
        3. Cache나 Map 또는 Set 등의 요소로 활용하기에 더욱 적합하다.
        4. 부수 효과(Side Effect)를 피해 오류가능성을 최소화할 수 있다.
        5. 다른 사람이 작성한 함수를 예측가능하며 안전하게 사용할 수 있다.
        6. 가비지 컬렉션의 성능을 높일 수 있다.
        7. 불변성이 보장된 객체라면 내부를 보지 않아 시간을 절감할 수 있다.

    클래스의 변수에 가능하다면 final을, final이 불가능하다면 Setter를 최소화하도록 하자.
    객체가 변경 가능한 데이터를 많이 가지고 있는 경우엔 불변이 오히려 부적절한 경우가 있다. (이것도 상황에 따라 선택하는듯)
 */
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() { 
        // jpa 스펙상 엔티티나 임베디드 타입은 기본생성자 필수 (public, protected) / protected 권장
        // jpa 구현 라이브러리가 프록시나 리플랙션 같은 기술을 써야해서 사용할 수 있게 지원해야하기 때문에 필요

        /*
            리플랙션
                - Reflection은 접근 제어자와 상관 없이 클래스 객체를 동적으로 생성하는(런타임 시점) Java API이다.
                - Reflection은 무조건 기본 생성자가 필요하다
                    - java Reflection이 가져올 수 없는 정보 중 하나가 바로 생성자의 인자 정보들이다.
                    - 따라서 기본 생성자 없이 파라미터가 있는 생성자만 존재한다면 java Reflection이 객체를 생성할 수 없게 되는 것이다.
                - 클래스로더를 통해 읽어온 클래스 정보(거울에 반사된 정보)를 사용하는 기술
                - 클래스를 읽어오거나, 인스턴스를 만들거나, 메서드를 실행하거나, 필드의 값을 가져오거나 변경하는 것이 가능
                - 구체적인 클래스 타입을 모를때 사용하는 방법

                - Reflection 으로 인해 기본생성자를 생성하면 불변성 보장이 힘들 수 있다.
                    - 따라서, private로 빈 객체 생성을 막는 게 최선
         */
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
