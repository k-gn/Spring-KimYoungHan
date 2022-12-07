package jpabook.jpashop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Locker {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "LOCKER_ID")
	private Long id;

	private String name;

	@OneToOne(mappedBy = "locker")
	private Member member;
}
