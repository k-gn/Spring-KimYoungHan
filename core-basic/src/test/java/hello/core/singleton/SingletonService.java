package hello.core.singleton;

// 싱글톤 클래스
// 진짜 잘 설계한 객체는 컴파일 오류 처리로 대부분의 오류를 잡을 수 있다.
public class SingletonService {

	private static final SingletonService instance = new SingletonService();

	private SingletonService() {
	}

	public static SingletonService getInstance() {
		return instance;
	}

	public void logic() {
		System.out.println("싱글톤 객체 호출");
	}
}
