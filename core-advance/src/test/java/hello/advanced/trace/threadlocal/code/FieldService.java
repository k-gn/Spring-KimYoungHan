package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldService  {

	/*
		동시성 문제는 지역변수에서는 발생하지 않는다.
		지역변수는 쓰레드마다 각각 다른 메모리에 할당된다.

		동시성 문제는 인스턴스의 필드(주로 싱글톤), static 같은 공용필드에 접근할 때 발생한다.

		이럴 때 사용하는 것이 쓰레드 로컬이다.
		## ThreadLocal
			- 해당 쓰레드만 접근할 수 있는 특별한 저장소
			- 사용자별로 확실하게 물건을 구분해준다.
	 */
	private String nameStore;

	public String logic(String name) {
		log.info("저장 name={} -> nameStore={}", name, nameStore);
		nameStore = name;
		sleep(1000);
		log.info("조회 nameStore={}", nameStore);
		return nameStore;
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}