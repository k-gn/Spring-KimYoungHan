package hello.springtx.order;

// 비즈니스 예외
public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
