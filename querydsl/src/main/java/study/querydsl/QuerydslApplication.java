package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;

@SpringBootApplication
public class QuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuerydslApplication.class, args);
    }

    // 다음과 같이 JPAQueryFactory 를 스프링 빈으로 등록해서 주입받아 사용해도 된다.
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        /*
            동시성 문제(멀티쓰레드 환경)는 걱정하지 않아도 된다. 왜냐하면 여기서 스프링이 주입해주는 엔티티 매니저는 실제
            동작 시점에 진짜 엔티티 매니저를 찾아주는 프록시용 가짜 엔티티 매니저이다. 이 가짜 엔티티 매니저는
            실제 사용 시점에 트랜잭션 단위로 각각 분리되어 실제 엔티티 매니저(영속성 컨텍스트)를 할당해준다.
         */
        return new JPAQueryFactory(em);
    }
}
