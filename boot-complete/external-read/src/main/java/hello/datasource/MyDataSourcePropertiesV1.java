package hello.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
// 스프링은 외부 설정의 묶음 정보를 개체로 변환하는 기능을 제공한다. (타입 안전한 설정 속성)
@ConfigurationProperties("my.datasource") // 묶음의 시작점 입력
public class MyDataSourcePropertiesV1 {

    private String url;
    private String username;
    private String password;
    private Etc etc;

    /*
        # static inner class
            외부 참조가 유지된다는 것은 메모리에 대한 참조가 유지되고 있다는 뜻
            GC가 메모리를 회수할 수 없다. 당연히 이는 메모리 누수를 부르는 치명적인 단점이다.
            항상 외부 인스턴스의 참조를 통해야 하므로 성능 상 비효율적이다.
     */
    @Data
    public static class Etc {
        private int maxConnection;
        private Duration timeout;
        private List<String> options = new ArrayList<>();
    }

}
