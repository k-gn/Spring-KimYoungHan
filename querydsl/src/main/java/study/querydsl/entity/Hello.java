package study.querydsl.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Hello {

    @Id
    @GeneratedValue
    private Long id;
}
/*
    queryDsl 은 자바 코드로 쿼리를 작성할 수 있다.
    편하게 동적쿼리 가능 + 중복 제거 + 편함
    기존에 문자열로 작성했던 쿼리를 컴파일 시점에 예외를 잡을 수 있게됨!
 */