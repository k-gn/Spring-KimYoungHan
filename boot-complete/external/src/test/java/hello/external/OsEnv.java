package hello.external;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class OsEnv {

    /*
        - OS 환경 변수를 설정하고 필요한 곳에서 System.getenv() 를 통해 사용할 수 있다.
        - 전역 변수같은 효과이기 때문에 여러 프로그램에서 사용이 가능하다.
            - 애플리케이션을 사용하는 자바 프로그램 안에서만 사용하고 싶을 땐 적절하지 않음
     */
    public static void main(String[] args) {
        Map<String, String> envMap = System.getenv();
        for (String key : envMap.keySet()) {
            log.info("env {}={}", key, System.getenv(key));
        }
    }
}
