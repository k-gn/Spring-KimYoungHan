package jpabook.jpashop;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.test.Team;
import jpabook.jpashop.test.Users;

public class JpaJPQLMain {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			// -- jpql
			List<Member> members = em.createQuery(
				"select m from  Member m",
				Member.class
			).getResultList();

			// -- Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Member> query = cb.createQuery(Member.class);
			// 루트 클래스 (조회를 시작할 시작점 - 별칭)
			Root<Member> m = query.from(Member.class);
			// 쿼리 생성
			CriteriaQuery<Member> cq
				= query.select(m).where(cb.equal(m.get("username"), "kim"));
			List<Member> resultList = em.createQuery(cq).getResultList();

			// -- native query
			em.createNativeQuery("select city, street, zipcode, MEMBER_ID from MEMBER", Member.class).getResultList();


			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			em.close();
		}
		emf.close();
	}
}
