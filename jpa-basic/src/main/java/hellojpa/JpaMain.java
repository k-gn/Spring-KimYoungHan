package hellojpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {

	public static void main(String[] args) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			// 비영속
			// Member saveMember1 = new Member();
			// saveMember1.setId(5L);
			// saveMember1.setName("gyunam");

			// Member saveMember2 = new Member();
			// saveMember2.setId(6L);
			// saveMember2.setName("gyunam");
			// 영속
			// em.persist(saveMember1); // INSERT
			// em.persist(saveMember2); // INSERT

			// 준영속
			// em.detach(saveMember);

			// 삭제
			// em.remove(saveMember);

			// Member findMember = em.find(Member.class, 1L);
			// findMember.setName("dongyu"); // UPDATE

			/*
				# JPQL (쿼리를 사용해야 할 경우)
					SQL을 추상화한 객체지향 쿼리언어
					검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색
					필요한 데이터만 DB 에서 불러오려면 결국 검색조건이 포함된 SQL이 필요
					테이블이 아닌 객체를 대상으로 검색하는 쿼리
			 */
			// List<Member> members = em.createQuery("select m from Member m", Member.class)
			// 	// .setFirstResult()
			// 	// .setMaxResults()
			// 	.getResultList();
			// members.forEach(System.out::println);

			tx.commit(); // 이 때 쿼리가 실행된다. = flush()
		} catch (Exception e) {
			tx.rollback();
		} finally {
			// 엔티티 매니저 팩토리는 하나만 생성 후 전체 공유
			// 엔티티 매니저는 쓰레드간에 공유가 일어나면 안되서 사용하고 버린다.
			// JPA 의 모든 데이터 변경은 트랜잭션 안에서 실행한다.
			// 자원을 다쓰면 꼭 닫아주자.
			em.close();
		}
		emf.close();
	}
}
