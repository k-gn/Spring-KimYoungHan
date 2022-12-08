package jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class JpaMain {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			// Member member = new Member();
			// member.setName("member1");
			// member.setAge(26);
			// em.persist(member);

			// -- 조회
			// Query memberQuery = em.createQuery("select m.name, m.age from Member m");
			// TypedQuery<Member> memberQuery = em.createQuery("select m from Member m", Member.class);
			// memberQuery.getResultList();
			// memberQuery.getSingleResult();

			// -- 파라미터
			// Member result = em.createQuery("select m from Member m where m.name = :name", Member.class)
			// 	.setParameter("name", "member1")
			// 	.getSingleResult();

			// -- 프로젝션
			// em.createQuery("select o.address from Order o", Address.class); // 임베디드 타입으로 조회 가능
			// Query query = em.createQuery("select distinct m.name, m.age from Member m"); // Object List
			// List<Object[]> resultList = em.createQuery("select distinct m.name, m.age from Member m").getResultList();
			// List<UserDTO> resultList = em.createQuery("select distinct new jpql.UserDTO(m.name, m.age) from Member m", UserDTO.class).getResultList();

			// -- 페이징
			// List<Member> resultList = em.createQuery("select m.name, m.age from Member m order by m.age desc",
			// 	Member.class)
			// 	.setFirstResult(0)
			// 	.setMaxResults(10)
			// 	.getResultList();

			// -- 조인
			// Team team = new Team();
			// team.setName("teamA");
			// em.persist(team);
			// Member member = new Member();
			// member.setName("member1");
			// member.setAge(26);
			// member.setTeam(team);
			// em.persist(member);

			// String query = "select m from Member m inner join m.team t"; // 내부조인
			// String query = "select m from Member m left join m.team t"; // 외부조인
			// String query = "select m from Member m, Team t where m.name = t.name"; // 세타조인 (cross join)
			// List<Member> result = em.createQuery(query, Member.class).getResultList();

			// em.createQuery("select o.address, 'HELLO', true, 'SHE''s', 10 from Order o", Address.class);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.close();
		}
		emf.close();
	}
}
