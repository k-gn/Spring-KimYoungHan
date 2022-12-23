package study.querydsl.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestClassTest {

	@Test
	void given_when_then() {

		TestClass testClass1 = new TestClass();
		testClass1.setAge(10);
		testClass1.setNum(10);

		TestClass testClass2 = new TestClass();
		testClass2.setAge(testClass1.getAge());
		testClass2.setNum(testClass1.getNum());

		System.out.println("testClass1 = " + testClass1);
		System.out.println("testClass2 = " + testClass2);

		System.out.println("------------");

		testClass2.setAge(testClass2.getAge() + 1);
		testClass2.setNum(testClass2.getNum() + 1);

		System.out.println("testClass1 = " + testClass1);
		System.out.println("testClass2 = " + testClass2);
	}

}