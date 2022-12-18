> https://frogand.tistory.com/156 (일반조인과 페치조인 차이)

---

>Fetch Join

조회 주체가 되는 엔티티와 연관 관계의 엔티티(JOIN) 까지 모두 조회하여 영속화한다.
즉, 2개의 엔티티 모두 영속성 컨텍스트로 관리되어진다.

>일반 Join

조회 주체가 되는 엔티티만 조회하고 영속화한다.
만약 연관 관계의 엔티티 데이터를 사용해야 할 경우 별도의 조회 쿼리문을 실행 해야 함.
FetchType.EAGER 일 경우, 연관 관계의 엔티티를 영속화하기 위해 N번의 쿼리를 발생시킴.
FetchType.LAZY 일 경우, 최초 조회시 획득한 id 로 조회를 N번해야함.

---

주어진 요구사항이 단순히 데이터 조회만을 수행하는 것이면 D(일반 Join with DTO) 와 같이 코드를 작성하더라도 N+1 문제 걱정 없이 요구사항을 해결할 수 있다.

https://velog.io/@heoseungyeon/Fetch-Join-vs-%EC%9D%BC%EB%B0%98-Joinfeat.DTO#2-fetch-join-%EA%B3%BC-%EC%9D%BC%EB%B0%98-join


https://cobbybb.tistory.com/18