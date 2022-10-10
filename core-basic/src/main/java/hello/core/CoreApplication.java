package hello.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}

/*
    ## SOLID

    1. 단일 책임의 원칙 - SRP
        - 한 클래스는 하나의 책임만 가진다.
        - 문맥과 상황에 따라 다르다.
        - 중요한 기준은 변경이다.
            - 변경이 있을 때 파급 효과가 적으면 잘 따른 것

    2. 개방 폐쇄의 원칙 - OCP
        - 확장에는 열려있고, 변경에는 닫혀 있어야 한다.
        - 다형성을 활용
            - 인터페이스를 구현한 새로운 클래스를 하나 만들어서 새로운 기능을 구현
        - 역할과 구현의 분리

    3. 리스코프 치환 원칙 - LSP
        - 객체는 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀 수 있어야 한다.
        - ex) 앞으로 가자고 정의한 엑셀 +10 / -10
        - 단순히 컴파일 성공이 아닌 의미적 차원의 이야기

    4. 의존 역전의 원칙 - DIP
        - 추상화에 의존해야지 구체화에 의존하면 안된다.
            - 즉, 구현 클래스에 의존하지 않고 인터페이스에 의존해라.
        - 역할에 의존해야 한다는 것과 같다.
        - 인터페이스에 의존해야 유연하게 구현체를 변경할 수 있다. (아니면 변경이 아주 어려워진다.)

    5. 인터페이스 분리 원칙 - ISP
        - 특정 클라이언트를 위한 인터페이스 여러개가 범용 인터페이스 하나보다 낫다.
        - 자동차 -> 운전 + 정비
        - 사용자 -> 운전자 + 정비사
            - 정비 인터페이스 자체가 변해도 운전자는 영향을 받지 않음
        - 인터페이스가 더 명확해지고 대체 가능성이 높아진다.

 */