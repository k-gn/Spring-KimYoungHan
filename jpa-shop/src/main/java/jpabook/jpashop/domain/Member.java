package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
// @Table(indexes = @Index()) // 인덱스도 넣을 수 있다.
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MEMBER_ID")
	private Long id;

	// @Column(length = 10) // 웬만하면 제약같은거 생기면 추가하는게 좋다 => 코드만 봐도 아~ 하고 알 수 있음
	private String name;

	private String city;

	private String street;

	private String zipcode;

	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();

	@OneToOne
	@JoinColumn(name = "LOCKER_ID")
	private Locker locker;
}
