package hello;

import hello.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

/*
    - 유지보수하기 좋은 애플리케이션 개발의 가장 기본 원칙은 변하는 것과 변하지 않는 것을 분리하는 것이다.

    - 스프링은 Environment 를 활용해서 더 편리하게 외부 설정을 읽는 방법들을 제공한다.
        - Environment
        - @Value
        - @ConfigurationProperties
 */
//@Import(MyDataSourceEnvConfig.class)
//@Import(MyDataSourceValueConfig.class)
//@Import(MyDataSourceConfigV1.class)
//@Import(MyDataSourceConfigV2.class)
@Import(MyDataSourceConfigV3.class)
@SpringBootApplication(scanBasePackages = {"hello.datasource", "hello.pay"})
// @EnableConfigurationProperties(MyDataSourcePropertiesV1.class) 를 쓰기 귀찮으면 아래 어노테이션을 추가하면 된다.
@ConfigurationPropertiesScan
public class ExternalReadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }

}
