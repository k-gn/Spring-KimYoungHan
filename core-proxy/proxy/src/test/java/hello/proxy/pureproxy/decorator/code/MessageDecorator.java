package hello.proxy.pureproxy.decorator.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecorator extends Decorator {

    public MessageDecorator(Component component) {
        super(component);
    }

    /*
        데코레이터는 Component 를 가지고 있어야 하는 중복이 있다.
        따라서 인터페이스가 아닌 해당 정보를 가지고 있는 추상클래스를 만드는 방법을 생각해볼 수 있다.
     */

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        //data -> *****data*****
        String result = component.operation();
        String decoResult = "*****" + result + "*****";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
        return decoResult;
    }
}
