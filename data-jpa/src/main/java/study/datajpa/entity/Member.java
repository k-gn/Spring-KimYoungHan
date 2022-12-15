package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(of = {"id, username, age"})
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String username;

	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	/*
		양방향 연관관계는 양 쪽 객체를 모두 신경써야 하는데, 하나의 메소드에서 양측에 관계를 설정하게 해주는 것이 안전하다.
		이렇게 한번에 양방향 관계를 설정하는 메소드를 연관관계 편의 메소드라 부른다.

		Setter 메소드를 사용하면 값을 변경한 의도를 파악하기 힘들다.
		객체의 일관성을 유지하기 어렵다.

		외부에서 쉽게 변경할 수 없게 @Setter를 사용하지않는다.
		그 이유는 @Setter를 사용하면 의도가 불명확하고 변경하면 안되는 중요한 값임에도 불구하고 변경 가능한 값으로 착각할 수 있다. (== 안정성 보장이 안된다.)
	 */
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}
