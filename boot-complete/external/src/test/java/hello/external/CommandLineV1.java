package hello.external;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandLineV1 {

    /*
        # 커맨드 라인 인수
        - 애플리케이션 실행 시점에 외부 설정 값을 main(args) 파라미터로 전달하는 방법
            ex. java -jar app.jar dataA dataB (마지막 위치에 필요한 데이터를 스페이스로 구분해서 전달)
        - 공백을 구분이 아닌 연결하려면 "" 로 감싸야 한다.
        - 기본적으로 key=value 형식이 아니다.
     */
    public static void main(String[] args) {
        for (String arg : args) {
            log.info("arg {}", arg);
        }
    }
}
