package hello.core.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
     # 싱글톤 빈과 함께 사용 시 문제점 / 해결
 */
public class SingletonWithPrototypeTest {

    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean bean1 = context.getBean(PrototypeBean.class);
        bean1.addCount();

        PrototypeBean bean2 = context.getBean(PrototypeBean.class);
        bean2.addCount();

        System.out.println(bean1.getCount());
        System.out.println(bean2.getCount());
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);

        ClientBean bean1 = context.getBean(ClientBean.class);
        int count1 = bean1.logic();

        ClientBean bean2 = context.getBean(ClientBean.class);
        int count2 = bean2.logic();

        /*
            분명 PrototypeBean 는 프로토타입 스코프인데 갑자기 싱글톤 스코프인 ClientBean 에서 사용하니까 count 값이 2가 나온다!
            => 생성 시점에 주입되기 때문에 한번 주입 후 계속 같은걸 사용한다.
         */
        System.out.println("count1 = " + count1);
        System.out.println("count2 = " + count2);
    }

    /*
        의존관계를 외부에서 주입받는게 아니라 직접 필요한 의존관계를 찾는 것 = Dependency Lookup (의존관계 조회)
        # ObjectFactory, ObjectProvider
        - 지정한 빈을 컨테이너에서 찾아주는 DL 서비스를 제공
        - objectProvider.getObject(); 를 통해 항상 새로운 프로토타입 빈이 생성된다.
        - 대신 조회를 해주는 대리자 느낌
        - 스프링에 의존

        - 프로토타입빈을 언제 사용할까?
            - 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요할 때 사용하면 된다.
            - 실무에선 대부분의 문제를 싱글톤 빈으로 해결할 수 있기 때문에 프로토타입 빈을 쓰는 경우는 매우 드물다.
            - Lazy / Optional / 순환참조 관계 등에서 Provider를 활용할 순 있다.
     */
    @Scope("singleton")
    static class ClientBean {

//        private final PrototypeBean prototypeBean; // 생성 시점에 주입.

        @Autowired
        private ObjectProvider<PrototypeBean> objectProvider;

//        public ClientBean(PrototypeBean prototypeBean) {
//            this.prototypeBean = prototypeBean;
//        }

        public int logic() {
            PrototypeBean prototypeBean = objectProvider.getObject();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    /*
        여러 빈에게 같은 프로토타입을 주입받으면, 주입 받는 시점에 각각 새로운 프로토타입 빈이 생성된다.
     */
    @Scope("singleton")
    static class ClientBean2 {
        private final PrototypeBean prototypeBean; // 생성 시점에 주입. (ClientBean 의 prototypeBean 과 다른놈)

        public ClientBean2(PrototypeBean prototypeBean) {
            this.prototypeBean = prototypeBean;
        }

        public int logic() {
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}