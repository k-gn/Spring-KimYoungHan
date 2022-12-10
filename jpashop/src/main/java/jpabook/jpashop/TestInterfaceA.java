package jpabook.jpashop;

public interface TestInterfaceA {

    int age = 0;

    public String testMethod();

    default void defaultMethod() {
        System.out.println("default method");
    }
}
