package jpabook.jpashop.service;

import jpabook.jpashop.TestInterfaceA;
import jpabook.jpashop.TestInterfaceB;

public class TestClass implements TestInterfaceA, TestInterfaceB {

    @Override
    public String testMethod() {
        return null;
    }
}
