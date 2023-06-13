package hello;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnvironmentCheck {

    /*
        # 스프링 외부 설정 통합
        - key=value 형식은 동일한대 외부 설정 방법에 따라 가져오는 방식이 전부 다르다.
        - 스프링은 이 문제를 Environment 와 PropertySource 추상화를 통해 해결한다.
            - 로딩 시점에 필요한 PropertySource 들을 생성하고 Environment 에서 사용할 수 있게 연결
            - Environment 의 getProperty(key)를 통해 모든 외부 설정을 조회할 수 있다.
            - 이제 외부 설정 방법이 이제 달라져도 코드를 변경할 필요가 없다.
            - 중복된 외부 설정은 우선순위로 동작된다.
                - args > command line > os env

        # 스프링에서의 기본 우선순위
        - 더 유연한 것이 우선권을 가진다.
        - 범위가 넓은 것 보다 좁은 것이 우선권을 가진다.

        # 외부 properties 파일
        - 파일로 설정값을 관리할 수 있다.
        - 외부 설정을 별도의 파일로 관리하게 되면 설정 파일 자체를 관리하는 번거로움이 있다.
        - 서버가 10대라면 10대 서버의 설정 파일을 모두 관리해야 한다. (변경 시 모두 변경)
        - 설정 파일이 별도로 관리되기 때문에 변경 이력을 확인 및 영향 파악이 어렵다.

        # 내부 파일 분리
        - 설정 파일을 외부가 아닌 내부에서 관리
        - 빌드 시점에 함께 빌드되게 하는 것
        - 프로필을 활용해 설정 환경을 분리해줄 수 있다.
            - application-{profile}.properties
            - --spring.profiles.active=prod
        - 파일을 분리해서 관리하기 때문에 한눈에 전체가 들어오지 않을 수 있다.

        # 내부 파일 합체
        - 하나의 파일 안에서 논리적으로 영역을 구분할 수 있다.
        - properties : #--- or !---
        - yml : ---
        - spring.config.activate.on-profile 로 프로필 값 지정
        - 프로필을 적용하지 않으면 null 로 값이 적용되며, default 프로필(기본값)을 따로 적용했다면 해당 값이 적용된다.

        - 단순하게 문서를 위에서 아래로 읽으면서 값을 설정한다. 이 때 기존 데이터가 있으면 덮어쓴다.
        - 따로 프로필이 적용 안된 값들이 있으면 먼저 읽고(공통), 프로필이 있다면 프로필에 대한 값들로 덮어쓴다.
            - 프로필에 일부 내용만 있다면 그 일부분만 덮어쓴다.
        - spring.config.activate.on-profile 설정이 있으면 해당 프로필을 사용할 때만 적용한다.

        # 전체 우선순위
        - 설정 데이터(properties) < OS 환경변수(env) < 자바 시스템 속성(-D)
           < 커맨드 라인 인수(args) < @TestPropertySource (테스트에서 사용한다)

        # 설정 데이터 우선순위
        - 내부 properties < 내부 profile properties < 외부 properties
     */
    private final Environment env;

    public EnvironmentCheck(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        String url = env.getProperty("url");
        String username = env.getProperty("username");
        String password = env.getProperty("password");
        log.info("env url={}", url);
        log.info("env username url={}", username);
        log.info("env password url={}", password);
    }
}
