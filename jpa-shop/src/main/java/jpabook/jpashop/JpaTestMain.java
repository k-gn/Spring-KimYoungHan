package jpabook.jpashop;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import jpabook.jpashop.test.Team;
import jpabook.jpashop.test.Users;

public class JpaTestMain {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			Team team = new Team();
			team.setName("TeamA");
			em.persist(team);

			Users users = new Users();
			users.setName("member1");
			// team.getUsers().add(users); // 연관관계 값 설정
			users.changeTeam(team); // 연관관계 값 설정 (연관관계 편의 메소드 - 어느 객체에 만들지 상황에 따라 다르고, 둘다에 추가하면 문제가 발생할 수 있다.)
			em.persist(users);

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
