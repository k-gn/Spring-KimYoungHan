package hello.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
	# 핵심
	- 스프링 컨테이너 생성
	- WAS(내장 톰캣) 생성
	- jar 안에 MANIUFEST.MF 파일을 읽고 main() 메서드를 실핸한다.
* */
@SpringBootApplication
public class BootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootApplication.class, args);
	}

}
