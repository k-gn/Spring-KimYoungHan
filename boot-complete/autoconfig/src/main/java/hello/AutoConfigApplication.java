package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
    # 자동 구성 동작 원리 순서
    @SpringBootApplication -> @EnableAutoConfiguration -> @Import(AutoConfigurationImportSelector.class)
    -> resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 파잏을 열어 설정 정보 선택 및 등록
 */
@SpringBootApplication
public class AutoConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoConfigApplication.class, args);
    }

}
