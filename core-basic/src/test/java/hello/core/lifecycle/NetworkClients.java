package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
	## 빈 생명주기 콜백
	- 데이터베이스 커넥션 풀 : 미리 데이터베이스와의 연결을 미리 여러개 맺어 놓고 요청 시 사용 및 재활용 -> 빠른 응답이 가능
	- 데이터베이스 커넥션 풀이나, 네트워크 소켓처럼 애플리케이션 시작 시점에 필요한 연결을 미리 해두고
	  애플리케이션 종료 시점에 연결을 모두 종료하는 작업을 진행하려면, 객체의 초기화와 종료 작업이 필요하다.
 */
public class NetworkClients { // implements InitializingBean, DisposableBean {

	private String url;

	/*
		객체 생성 -> 의존관계 주입 순으로 라이프 사이클을 가진다.
		스프링 빈은 객체를 생성하고, 의존관계 주입이 다 끝난 다음에야 필요한 데이터를 사용할 수 있는 준비가 완료된다.
		따라서 초기화 작업은 의존관계 주입이 모두 완료되고 난 다음에 호출해야한다.

		스프링은 의존관계 주입이 완료되면 스프링 빈에게 콜백 메서드를 통해서 초기화 시점을 알려주는 다양한 기능을 제공한다.
		스프링은 스프링 컨테이너가 종료되기 직전에 소멸 콜백을 준다. -> 안전한 종료 작업 진행 가능

		# 스프링 빈의 이벤트 라이프사이클 (싱글톤 기준)
		스프링 컨테이너 생성 -> 스프링 빈 생성 -> 의존관계 주입 -> 초기화 콜백 -> 사용 -> 소멸 전 콜백 -> 스프링 종료

		# 객체의 생성과 초기화를 분리하자
		- 생성자는 필수 정보를 받고, 메모리를 할당해서 객체를 생성하는 책임을 가진다. (역할과 책임의 개념)
		- 초기화는 이렇게 생성된 값들을 활용해 외부 커넥션을 연결하는 등 무거운 동작을 수행한다.
		- 생성자 안에서 무거운 초기화 작업을 함께 하는 것 보다는 객체를 생성하는 부분과 초기화 하는 부분을 명확하게 나누는 것이 유지보수에 좋다.
		- 객체 생성 후 실제 최초 외부 커넥션 요청이 올 때 까지 생성만 해놓고 기다릴 수 있다. -> 요청이 오기 전까지 불필요한 자원 낭비를 방지한다.
		- 초기화 작업이 내부 값들만 약간 변경하는 정도면 생성자에서 한번에 처리하는 것이 더 나을 수 있다.

		# 스프링은 크게 3가지 방법으로 빈 생명주기 콜백을 지원한다.
		1. 인터페이스 (InitializingBean, DisposableBean)
			- 스프링 전용 인터페이스로, 해당 코드가 스프링 전용 인터페이스에 의존한다.
			- 초기화, 소멸 메서드의 이름을 변경할 수 없다.
			- 내가 코드를 고칠 수 없는 외부 라이브러리에 적용할 수 없다.
			- 잘 안쓰는 방식이다.

		2. 설정정보에 초기화 메서드, 종료 메서드 지정
			- 메서드 이름을 자유롭게 줄 수 있다.
			- 스프링 빈이 스프링 코드에 의존하지 않는다. (스프링에 종속적인 기술이 아니다.)
			- 외부 라이브러리에도 적용할 수 있다.
			- @Bean 의 속성 destroyMethod 의 기본값이 추론으로 등록되어 있음 (close, shutdown)
			  -> 따라서 따로 지정해주지 않아도 알아서 잘 동작한다. (@Bean 으로 사용 시에만 동작)

			- 일반적으로 종료는 close 란 이름을 많이 쓴다. (AutoCloseable 를 구현하여 활용 가능)

		3. @PostConstruct, @PreDestroy 애노테이션 지원
			- 결론은 이 방법을 쓰면 된다!
			- 최신 스프링에서 가장 권장한다.
			- 매우 편리하다.
			- 스프링이 아닌 다른 컨테이너에서도 동작한다.
			- 컴포넌트 스캔과 잘 어울린다. (@Bean 으로 빈을 등록하지 않아도 사용할 수 있어서)
			- 외부 라이브러리에는 적용하지 못한다. (이땐 2번 @Bean 기능을 사용하자)

	 */
	
	public NetworkClients() {
		System.out.println("url = " + url);
//		connect();
//		call("초기화 연결 메시지");
//		disconnect();
	}

	public void setUrl(String url) {
		this.url = url;
	}

	// 서비스 시작 시 호출
	public void connect() {
		System.out.println("connect = " + url);
	}

	public void call(String message) {
		System.out.println("call = " + url + " message = " + message);
	}

	// 서비스 종료 시 호출
	public void disconnect() {
		System.out.println("close = " + url);
	}

	/*
	// InitiallizeBean method - 의존관계 주입이 끝나면 호출
	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("NetworkClients.afterPropertiesSet");
		connect();
		call("초기화 연결 메시지");
	}

	// DisposableBean method - 종료 시 호출
	@Override
	public void destroy() throws Exception {
		System.out.println("NetworkClients.destroy");
		disconnect();
	}
	*/

	/*
	// @Beam 에 초기화, 종료 메소드 지정
	public void init() {
		System.out.println("NetworkClients.init");
		connect();
		call("초기화 연결 메시지");
	}

	public void close() {
		System.out.println("NetworkClients.close");
		disconnect();
	}
	 */

	@PostConstruct
	public void init() {
		System.out.println("NetworkClients.init");
		connect();
		call("초기화 연결 메시지");
	}

	@PreDestroy
	public void close() {
		System.out.println("NetworkClients.close");
		disconnect();
	}
}
