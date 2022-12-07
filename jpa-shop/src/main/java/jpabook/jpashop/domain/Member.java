package jpabook.jpashop.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// @Table(indexes = @Index()) // 인덱스도넣을 수 있다.
public class Member extends BaseEntity {

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
}
