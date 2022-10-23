package hello.core.web;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    private final LogDemoService logDemoService;

    // Scope 가 request 인 빈은 요청이 들어와야 생성되기 때문에 그 이전에 바로 주입받을 수 없다.
    private final MyLogger myLogger;
//    private final ObjectProvider<MyLogger> myLoggerObjectProvider;

    @GetMapping("/log-demo")
    @ResponseBody
    public String logDemo(HttpServletRequest request) throws InterruptedException {
//        MyLogger myLogger = myLoggerObjectProvider.getObject();
        String requestUrl = request.getRequestURI();
        myLogger.setRequestURL(requestUrl);

        System.out.println("myLogger : " + myLogger);
        // 사실 이런 로그 처리는 공통 처리가 가능한 스프링 인터셉터나, 서블릿 필터, AOP 등을 활용하는 것이 좋다.
        myLogger.log("controller test - " + Thread.currentThread());
        Thread.sleep(1000);
        // 웹과 관련된 부분은 컨트롤러 계층에서 처리하고, 서비스 계층은 웹 기술에 종속되지 않고, 가급적 순수하게 유지하는 것이 좋다.
        // -> 유지보수 측면에서 좋고, 넘어가는 파라미터가 많으면 지저분해보일 수 있다(가독성 떨어짐)
        // 여기선 requestUrl 을 컨트롤러에서 먼저 처리하고 서비스로 넘어가는 것이 해당함
        logDemoService.logic("testId");
        return "OK";
    }
}
