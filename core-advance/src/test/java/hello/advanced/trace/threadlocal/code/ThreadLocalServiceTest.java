package hello.advanced.trace.threadlocal.code;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ThreadLocalServiceTest {

	private ThreadLocalService service = new ThreadLocalService();

	@Test
	void field() {
		log.info("main start");
		Runnable userA = () -> {
			service.logic("userA");
		};
		Runnable userB = () -> {
			service.logic("userB");
		};

		Thread threadA = new Thread(userA);
		threadA.setName("thread-A");
		Thread threadB = new Thread(userB);
		threadB.setName("thread-B");
		threadA.start(); // A 실행
		// sleep(2000); // 동시성 문제 발생 X
		sleep(100); //동시성 문제 발생 해결!
		threadB.start(); // B 실행
		sleep(3000); // 메인 쓰레드 종료 대기
		log.info("main exit");
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}