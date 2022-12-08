#### JPA 는 다양한 쿼리 방법을 지원한다.
1. JPQL : JPA 쿼리 표준 문법
2. JPA Criteria : 자바 코드로 JPQL 을 빌드해주는 제너레이터 (표준)
3. QueryDSL : 자바 코드로 JPQL 을 빌드해주는 제너레이터 (라이브러리)
4. Native Query : 실제 표준 SQL 쿼리 작성
    - SQL을 직접 사용하는 기능 (JPQL로 해결할 수 없는 특정 데이터베이스에 의존적인 기능)
        - ex. 오라클 connect by
5. JDBC API, MyBatis, JdbcTemplate 함께 사용가능
    - 대부분 JPQL 로 해결이 되지만 가끔 해결이 안되는 것들이 존재함 -> 4,5번으로 해결
    - JPA 가 제공하는 기능이 아니면 사용 전 영속성 컨텍스트를 적절한 시점에 강제로 플러시 해야한다. (동기화)

---

### JPQL
- JPA를 사용하면 엔티티 객체를 중심으로 개발
- 문제는 검색 같은 쿼리
    - 검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색 (즉, 엔티티 객체를 대상으로 쿼리를 짤 수 있어야 한다.)
    - 검색 시 모든 데이터를 객체로 변환해서 검색하는 것은 불가능
    - 애플리케이션이 필요한 데이터만 불러오려면 결국 검색 조건이 포함된 SQL이 필요하다.

- JPA는 SQL을 추상화한 JPQL 객체지향 쿼리 언어를 제공
- SQL 문법과 유사하다.
- 엔티티 객체를 대상으로 쿼리 (그냥 SQL은 테이블을 대상으로한다.)
    - 결국 JPQL -> SQL 로 번역되긴한다.

- SQL을 추상화 해서 특정 데이터베이스에 의존하지 않음 (객체지향 SQL)
- 쿼리를 단순 문자열로 작성하기 때문에 동적 쿼리가 필요할 경우 매우 어렵다.
    - Criteria or QueryDSL 을 활용하여 해결 가능 - 컴파일 오류를 발생시킬 수 있다. (쿼리를 자바 코드로 짠다.)
    - Criteria 는 사실 안쓴다. 왜?
        - 너무 복잡하고 실용성이 없다. 유지보수도 최악..
    - QueryDSL 사용을 권장
        - 초기 설정만 좀 귀찮고 나머진 단순하고 쉽다. 실무 사용 권장! (JPQL 문법을 알고 써야한다. 문서도 친절하다.)

- JPQL + QueryDSL 이 두개의 조합에 네이티브 쿼리나 다른 데이터베이스 기술 사용을 필요에 따라 추가해서 쓰면 된다.
- update, delete 같은 경우 벌크연산으로 한번에 업데이트할 수 있다. (한건씩 날리는 쿼리는 성능상 느리다.)

### 문법

- 엔티티와 속성은 대소문자 구분 o
- JPQL 키워드는 대소문자 구분 x
- 엔티티 이름을 사용한다 (테이블 이름 아님)
- 별칭 필수 (as 키워드 생략 가능)
- 집합과 정렬 제공 (GROUP BY, HAVING, ORDER BY)
- TypeQuery : 반환 타입이 명확할 때 사용 / Query : 반환 타입이 명확하지 않을 때 사용

- 결과 조회 API
    - query.getResultList(): 결과가 하나 이상일 때, 리스트 반환
        - 결과가 없으면 빈 리스트 반환
    - query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환
        - 결과가 없거나, 둘 이상이면 예외 발생
    

- 파라미터 바인딩 (이름기준 or 위치기준)
  ~~~
    // 이름 기준 (권장)
    SELECT m FROM Member m where m.username=:username 
    query.setParameter("username", usernameParam);
  
    // 위치 기준
    SELECT m FROM Member m where m.username=?1 
    query.setParameter(1, usernameParam);
  ~~~
  
- 프로젝션
    - SELECT 절에 조회할 대상을 지정하는 것 
    - 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타입)
    - SELECT m FROM Member m -> 엔티티 프로젝션 (영속성 컨텍스트에서 관리된다.)
    - SELECT m.team FROM Member m -> 엔티티 프로젝션 (영속성 컨텍스트에서 관리된다. / 조인하는 쿼리가 실행된다.)
        - 이렇게 쓸 경우 조인되는지 알아보기 힘들기 때문에 최대한 SQL과 유사하게 작성해야 한다.
        - SELECT t FROM Member m join m.team t
    - SELECT m.address FROM Member m -> 임베디드 타입 프로젝션
        - 소속과 함께 써야한다. (엔티티로부터 시작 - m.address)
    - SELECT m.username, m.age FROM Member m -> 스칼라 타입 프로젝션 
    - DISTINCT 로 중복 제거 가능
    
- 프로젝션의 여러값 조회
~~~
    SELECT m.username, m.age FROM Member m
    
    /*
        1. Query 타입으로 조회
        2. Object[] 타입으로 조회
        3. new 명령어로 조회
            - 단순 값을 DTO로 조회
            - 패키지명을 포함한 전체 클래스명 입력 
              (SELECT new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m)
            - 순서와 타입이 일치하는 생성자 필요
    */
~~~

- 페이징
    - setFirstResult(int startPosition): 조회 시작위치 (0부터 시작)
    - setMaxResults(int maxResult): 조회할 데이터 수
    - 이 두값을 사용만 하면 알아서 페이징 된다.

- 조인
    - 내부조인 : SELECT m FROM Member m [INNER] JOIN m.team t
        - member 는 있고 team 이 없을 때 검색 안됨
    - 외부조인 : SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
        - member 있고 team 없어도 team 값을 null 로 하고 조회됨
    - 세타조인 : select count(m) from Member m, Team t where m.username = t.name
        - 관계 없는 테이블 간 조인 (cross join)
    - ON 절을 활용한 조인 
        - 조인 대상 필터링
            - ex. 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
            - SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'A'
        - 연관관계 없는 엔티티 외부 조인
            - ex. 회원의 이름과 팀의 이름이 같은 대상 외부 조인
            - SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
    

- 서브 쿼리
    - 메인과 서브쿼리가 서로 관계가 없을 때 성능이 잘나온다.
    - 나이가 평균보다 많은 회원 
        - select m from Member m where m.age > (select avg(m2.age) from Member m2) 
        - 주 쿼리 엔티티를 서브 쿼리에서 사용하지 않고 새로운 m2 를 사용함으로 성능이 잘나온다.

    - 한 건 이라도 주문한 고객
        - select m from Member m where (select count(o) from Order o where m = o.member) > 0
        - 주 쿼리 엔티티를 서브 쿼리에서도 사용함으로써 성능이 떨어진다.
    
    - 지원 함수
        - [NOT] EXISTS (subquery): 서브 쿼리에 결과가 존재하면 참 (select m from Member m where exists (select t from m.team t where t.name = ‘팀A'))
            - ALL | ANY | SOME (subquery)
            - ALL : 모두 만족하면 참 == a and b (select o from Order o where o.orderAmount > ALL (select p.stockAmount from Product p))
            - ANY, SOME : 조건을 하나라도 만족하면 참 == a or b (select m from Member m where m.team = ANY (select t from Team t))
        - [NOT] IN (subquery): 서브 쿼리의 결과중 하나라도 있으면 참
    
    - WHERE, HAVING, SELECT 절에서만 서브쿼리 사용 가능
    - FROM 절의 서브쿼리는 JPQL에서 현재 불가능하다. 
        - 조인으로 풀 수 있으면 풀어서 해결
        - 서브쿼리 조회 후 애플리케이션에서 조작해서 해결
        - 쿼리를 분해해서 날려 해결
        - 정 안되면 native sql 사용
    - 서브쿼리로 쿼리 수를 줄이고, 메인쿼리는 보여주는 포멧을 결정하는 경우가 있는데 이런 포맷(뷰)은 애플리케이션 단에서 조작하는게 낫다. (서브 쿼리 자체가 줄어든다.)
    
- JPQL 타입표현
    - 문자 : 'HELLO', 'SHE''s'
    - 숫자 : 10L, 10D, 10F
    - Boolean : TRUE, FALSE
    - ENUM: [package].[enumClass] (파라미터 바인딩하면 패키지를 적을 필요가 없다.)
    - 엔티티: TYPE(m) = Member (상속관계에서 사용 / ex. 부모타입 조회 시 DTYPE으로 검색)
        - ("select i from Item i where type(i) = Book", Item.class)
    
- 기타
    - SQL과 문법이 같은 식
    - EXISTS, IN
    - AND, OR, NOT
    - =, >, >=, <, <=, <>
    - BETWEEN, LIKE, IS NULL
    

- 조건식 - CASE
    ~~~
        # 기본
        select 
            case when m.age <= 10 then 'A'
                 when m.age >= 60 then 'B'
                else 'C'
            end  
        from Member m
        
        # 단순
        select 
            case t.name
                when 'A' then '10%'
                when 'B' then '20%'
                else '5%'
            end  
        from Member m
  
        # COALESCE : 하나씩 조회해서 null 이 아니면 반환
        select coalesce(m.name, 'none') from Member m (사용자 이름이 없으면 none 반환)
  
        # NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
        select NULLIF(m.name, 'admin') from Member m (사용자 이름이 관리자면 null, 아니면 본인 이름 반환)
    ~~~

- JPQL 기본 함수
    - CONCAT
    - SUBSTRING
    - TRIM
    - LOWER, UPPER
    - LENGTH
    - LOCATE (찾는 문자 인덱스 반환)
    - ABS, SQRT, MOD (수학적 함수)
    - SIZE (컬렉션의 크기), INDEX (@OrderColumn 사용 시 컬렉션 위치값 구하기 - 잘 안쓴다.) 
    
- 사용자 정의 함수 호출
    - 하이버네이트는 사용전 방언에 추가해야 한다.
    - 사용하는 DB방언을 상속받고, 사용자 정의 함수를 등록한다. (생성자에서 함수 등록 - 방언 클래스에 들어가보면 방법들이 있다.)
    - function() 사용
        - select function('group_concat', i.name) from Item i
    
---

- 경로 표현식
    - .(점) 을 찍어 객체 그래프를 탐색하는 것
    