package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {

    // 같은 request scope 내에선 ObjectProvider.getObject() 를 따로 호출해도 (컨트롤러와 서비스단에서 각각 호출) 같은 스프링 빈이 반환된다.
    private final MyLogger myLogger;
//    private final ObjectProvider<MyLogger> myLoggerObjectProvider;

    public void logic(String id) {
//        MyLogger myLogger = myLoggerObjectProvider.getObject();
        myLogger.log("service id : " + id);
    }
}
