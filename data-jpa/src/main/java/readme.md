

- 스프링 데이터 JPA가 제공하는 공통 인터페이스의 구현체 
    - org.springframework.data.jpa.repository.support.SimpleJpaRepository
    - 안에 들어가서 보면 JPA 내부 기능들을 확인할 수 있다.

- @Repository 적용: JPA 예외를 스프링이 추상화한 예외로 변환 (하부 기술을 바꿔도 예외가 동일함! - 기존 코드에 영향을 주지 않음)
- @Transactional 트랜잭션 적용
    - JPA의 모든 변경은 트랜잭션 안에서 동작
    - 스프링 데이터 JPA는 변경(등록, 수정, 삭제) 메서드를 트랜잭션 처리
    - 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
    - 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
    - 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했음(사실은 트랜잭션이 리포지토리 계층에 걸려있는 것임)

- @Transactional(readOnly = true)
    - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 플러시를 안해서 변경감지 x -> 약간의 성능 향상을 얻을 수 있음

- *save() 메서드*
    - 새로운 엔티티면 저장( persist ) 
    - 새로운 엔티티가 아니면 병합( merge ) (select 쿼리를 한번하고, 값을 전부 교체함, 사용 지양 / 준영속 -> 영속으로 할 때 가끔 사용)
    

---


> 새로운 엔티티를 구별하는 방법
    
- 새로운 엔티티를 판단하는 기본 전략
  - 식별자가 객체일 때 null 로 판단 
  - 식별자가 자바 기본 타입일 때 0 으로 판단
  - Persistable 인터페이스를 구현해서 판단 로직 변경 가능
    
~~~java
    package org.springframework.data.domain;
    
    public interface Persistable<ID> {
        ID getId();
        boolean isNew();
    }
~~~

- JPA 식별자 생성 전략이 @GenerateValue 면 save() 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 동작한다.
- 그런데 JPA 식별자 생성 전략이 @Id 만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save() 를 호출한다. -> 이 경우 merge() 가 호출
- merge() 는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율 적이다.
- Persistable 를 사용해서 새로운 엔티티 확인 여부를 직접 구현하게는 효과적이다.
    - 등록시간( @CreatedDate )을 조합하여 사용하면 새로운 엔티티 여부를 편하게 확인할 수 있다 (@CreatedDate에 값이 없으면 새로운 엔티티로 판단)
    
~~~java

 @Entity
 @EntityListeners(AuditingEntityListener.class)
 @NoArgsConstructor(access = AccessLevel.PROTECTED)
 public class Item implements Persistable<String> {
	
	 @Id
	 private String id;
	 
	 @CreatedDate
	 private LocalDateTime createdDate;
	 
	 public Item(String id) {
		 this.id = id;
	 }
	 
	 @Override
	 public String getId() {
		 return id; 
	 }
	 
	 @Override
	 public boolean isNew() {
		 return createdDate == null;
	 }
 }

~~~


---


### Specifications (명세)

- 스프링 데이터 JPA는 JPA Criteria를 활용해서 이 개념을 사용할 수 있도록 지원

### Query By Example

~~~java

public class QueryByExampleTest {
	
  @Autowired MemberRepository memberRepository;
  @Autowired EntityManager em;
  
  @Test
  public void basic() throws Exception {
    //given
    Team teamA = new Team("teamA");
    em.persist(teamA);
    em.persist(new Member("m1", 0, teamA));
    em.persist(new Member("m2", 0, teamA));
    em.flush();
    
    //when //Probe 생성
    Member member = new Member("m1");
    Team team = new Team("teamA"); //내부조인으로 teamA 가능
    member.setTeam(team);
    //ExampleMatcher 생성, age 프로퍼티는 무시 
    ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
    Example<Member> example = Example.of(member, matcher);
    List<Member> result = memberRepository.findAll(example);
    
    //then
    assertThat(result.size()).isEqualTo(1);
  }
}

~~~

- Probe: 필드에 데이터가 있는 실제 도메인 객체
- ExampleMatcher: 특정 필드를 일치시키는 상세한 정보 제공, 재사용 가능
- Example: Probe와 ExampleMatcher로 구성, 쿼리를 생성하는데 사용


**실무에서 사용하기에는 매칭 조건이 너무 단순하고, LEFT 조인이 안됨 -> 실무에서는 QueryDSL을 사용하자**


### Projections

- 엔티티 대신에 DTO를 편리하게 조회할 때 사용

~~~

// -----인터페이스 기반 Projection
// 조회할 엔티티의 필드를 getter 형식으로 지정하면 해당 필드만 선택해서 조회(Projection)
// 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
public interface UsernameOnly {
    String getUsername();
}

// 메서드 이름은 자유, 반환 타입으로 인지
public interface MemberRepository ... {
    List<UsernameOnly> findProjectionsByUsername(String username);
}

// SQL에서도 select절에서 username만 조회(Projection)하는 것을 확인
List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");


// -----클래스 기반 Projection

// 다음과 같이 인터페이스가 아닌 구체적인 DTO 형식도 가능 (생성자의 파라미터 이름으로 매칭)
public class UsernameOnlyDto {
    private final String username;
    public UsernameOnlyDto(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    } 
}


// -----동적 Projections

// 다음과 같이 Generic type을 주면, 동적으로 프로젝션 데이터 번경 가능
<T> List<T> findProjectionsByUsername(String username, Class<T> type);

List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1", UsernameOnly.class);


// -----중첩 구조 처리
public interface NestedClosedProjection {
    String getUsername();
    
    TeamInfo getTeam();
    interface TeamInfo {
        String getName();
    } 
}


~~~


- 프로젝션 대상이 root 엔티티면, JPQL SELECT 절 최적화 가능
- 로젝션 대상이 ROOT가 아니면 (프로젝션 대상이 root 엔티티를 넘어가면 JPQL SELECT 최적화가 안된다!)
  - LEFT OUTER JOIN 처리
  - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산

- 복잡한 쿼리를 해결하기에는 한계가 있다. -> 실무에서는 단순할 때만 사용하고, 조금만 복잡해지면 **QueryDSL을 사용하자**

