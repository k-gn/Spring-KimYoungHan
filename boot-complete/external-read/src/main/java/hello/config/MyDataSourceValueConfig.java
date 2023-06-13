package hello.config;

import hello.datasource.MyDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
public class MyDataSourceValueConfig {

    /*
        # @Value
        - ${} 를 사용하여 외부 설정의 키 값을 주면 주입 받을 수 있다.
        - 필드, 파라미터에 사용 가능
        - 타입도 알아서 변환해준다.
     */
    @Value("${my.datasource.url}")
    private String url;
    @Value("${my.datasource.username}")
    private String username;
    @Value("${my.datasource.password}")
    private String password;
    @Value("${my.datasource.etc.max-connection}")
    private int maxConnection;
    @Value("${my.datasource.etc.timeout}")
    private Duration timeout;
    @Value("${my.datasource.etc.options}")
    private List<String> options;

    @Bean
    public MyDataSource myDataSource1() {
        return new MyDataSource(url, username, password, maxConnection, timeout, options);
    }

    @Bean
    public MyDataSource myDataSource2(
            @Value("${my.datasource.url}") String url,
            @Value("${my.datasource.username}") String username,
            @Value("${my.datasource.password}") String password,
            // 키를 못 찾을 경우 기본값을 사용하려면 ':' 를 사용하여 기본값을 적어줄 수 있다.
            @Value("${my.datasource.etc.max-connection:1}") int maxConnection,
            @Value("${my.datasource.etc.timeout}") Duration timeout,
            @Value("${my.datasource.etc.options}") List<String> options) {

        return new MyDataSource(url, username, password, maxConnection, timeout, options);
    }

}
