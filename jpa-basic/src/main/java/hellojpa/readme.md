## JPA 에서 가장 중요한 2가지

- ORM 
- 영속성 컨텍스트
---

### 영속성 컨텐스트

- 엔티티를 영구 저장하는 환경
  - 데이터 등록 시 데이터베이스가 아닌 영속성 컨텍스트에 먼저 저장이 된다.
- 엔티티 매니저를 통해 접근한다.

---

> 엔티티의 생명주기
1. 비영속 : 영속성 컨텍스트와 관계없는 새로운 상태 (ex. 객체를 생성만 한 상태)
2. 영속 : 영속성 컨텍스트에 관리되는 상태 (ex. em.persist, em.find)
3. 준영속 : 영속성 컨텍스트에 저장되었다가 분리된 상태 (ex. em.detach)
4. 삭제 : 삭제된 상태 (ex. em.remove)

> 영속성 컨텍스트의 이점
1. 1차 캐시 : persist 시 1차 캐시에 저장되고, 이 후 조회 시 DB가 아닌 1차 캐시에서 조회한다. 
   (동일한 트랜잭션 내에서만 동작 - 트랜잭션이 끝나면 비운다.)
2. 동일성 보장 
   (동일한 트랜잭션 내에서는 동일한 결과, 애플리케이션 단에서 REPEATABLE READ)
   - REPEATABLE READ : 트랜잭션이 시작되기 전(쿼리가 시작되기 전이 아닌) commit 된 결과를 참조 - 다른 트랜잭션의 commit 여부와 무관하게 항상 같은 결과가 출력
   - 동일한 트랜잭션 내에서 같은 객체를 여러번 find 해도 항상 같은 값이다.
3. 트랜잭션을 지원하는 쓰기 지연 : SQL을 쓰기지연 SQL 저장소에 쌓아두었다가 트랜잭션이 커밋하는 순간 데이터베이스에 SQL을 보낸다. 
   (데이터 변경 시 반드시 트랜잭션을 시작해야한다.)
   - hibernate.jdbc.batch_size option 을 통해 한번에 쿼리를 보낼 수 있다.
     (hibernate.show_sql: true 기반 로그에는 제대로 표시가 안되어 제대로 Batch Insert가 진행된건지 확인하기 어려움 -> Mysql의 실제 로그로 확인)
4. 변경 감지 (Dirty Checking)
   - em.update 같은 것이 없어도 알아서 엔티티의 변경을 감지하여 update query 를 만든다. (엔티티와 스냅샷 비교)
5. 지연 로딩 (Lazy)
   - 엔티티를 사용하는 시점에 조회해올 수 있다.

- JPA는 자바 컬렉션에서 데이터를 다루는 방법과 유사하다.
- 플러시 : 영속성 컨텍스트 변경내용을 데이터베이스에 반영 (호출 방법 - em.flush(), 트랜잭션 커밋, JPQL 쿼리 실행)
   - 영속성 컨텍스트를 비우지 않음
   - 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
   - 트랜잭션 작업 단위가 중요 -> 커밋 직전에만 동기화하면 된다.
- JPA는 데이터 동기화나 동시성 관련된 것들을 전부 데이터베이스 트랜잭션에 위임한다.

> 준영속 상태

- 영속성 컨텍스트가 제공하는 기능을 사용하지 못한다.
- 준영속 상태로 만드는 방법
   - em.detach(entity) : 특정 엔티티만 준영속 상태로 변환
   - em.clear() : 영속성 컨텍스트를 완전히 초기화
   - em.close() : 영속성 컨텍스트를 종료
   
---

### 엔티티 매핑

- 객체와 테이블 매핑 : @Entity, @Table
- 필드와 컬럼 매핑 : @Column
- 기본 키 매핑 : @Id
- 연관관계 매핑 : @ManyToOne, @JoinColumn

> @Entity
- @Entity 가 붙은 클래스는 JPA 가 관리한다. (엔티티라 한다.)
- JPA를 사용해서 테이블과 매핑할 클래스는 필수로 붙여야 한다.
- 기본 생성자 필수 
- final class, enum, interface, inner class 사용 x
- 저장할 필드에 final 사용 x
    - name 속성 지정 가능 (사용할 엔티티 이름 지정, 기본값은 클래스 이름, 가급적 기본값 사용)
 
> @Table
- 엔티티와 매핑할 테이블을 지정
    - name : 매핑할 테이블 이름
    - catalog : 데이터베이스 catalog 매핑
    - schema : 데이터베이스 schema 매핑
    - uniqueConstraints : DDL 생성 시 유니크 제약 조건 지정
    

---

### 데이터베이스 스키마 자동 생성
- DDL 을 애플리케이션 실행 시점에 자동 생성
- 테이블 중심 -> 객체 중심
- 생성된 DDL 은 적절히 다듬은 후 운영서버로 반영
- hibernate.hbm2ddl.auto 옵션으로 지원
    - create : 기존 테이블 삭제 후 다시 생성
    - create-drop : create 와 동일하며, 종료 시점에 테이블 drop
    - update : 변경 사항만 반영 (지우는 변경은 적용되지 않음)
    - validate : 엔티티와 테이블이 정상 매핑 되었는지 확인 (정상 매핑이 아니라면 예외)
    - none : 사용하지 않음
- 데이터베이스 방언별로 쿼리가 달라진다!
- 운영 장비에는 절대 create, create-drop, update 를 사용하면 안된다. (데이터가 증발하거나, 장애가 발생할 수 있다..!)
  - 사실 근본적으로 데이터베이스 계정을 권한별로 분리하는 것이 중요하다.
    - 개발 초기 : create or update
    - 테스트 서버 : update or validate
    - 스테이징, 운영 서버 : validate or none
    
- DDL 생성 기능
    - 테이블 생성, 컬럼 추가
    - 제약조건 (아래 예시)
      - @Column(nullable = false, length = 10)
      - @Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", columnNames = {"NAME", "AGE"})})
    - DDL을 자동 생성할 때만 사용되고, 실행 로직에는 영향을 주지 않는다.