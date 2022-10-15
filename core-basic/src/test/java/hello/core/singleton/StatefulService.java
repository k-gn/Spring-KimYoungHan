package hello.core.singleton;


/*
	## 싱글톤 방식의 주의점
		- 여러 클라이언트가 하나의 같은 객체를 공유하기 때문에 싱글톤 객체는 상태를 유지하게 설계하면 안된다. => 무상태 (stateless)
			1. 특정 클라이언트에 의존적인 필드 X
			2. 특정 클라이언트가 값을 변경할 수 있는 필드 X
			3. 가급적 읽기만 가능
			4. 필드 대신 공유되지 않는 지역변수, 파라미터, ThreadLocal 등을 사용
			=> 공유 필드는 반드시 조심하자!
			=> 스프링 빈은 항상 무상태로 설계하자!
		- 위 규칙을 지키지 않을 경우 큰 장애가 발생할 수 있다.
 */
public class StatefulService {

	// private int price; // 상태를 유지하는 필드

	public int order(
		String name,
		int price
	) {
		// this.price = price;
		return price; // 무상태
	}

	// public int getPrice() {
	// 	return price;
	// }
}
