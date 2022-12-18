package study.querydsl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(of = {"id, username, age"}) // team 이 들어갈 경우 무한참조가 발생할 수 있음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	/*
		PK에는 Long(Wrapper Class) 을 사용한다
		Wrapper Class 를 사용함으로써 Null을 대입해놓을 수 있는데, 명시적으로 PK가 아직 할당되지 않았음을 의미할수 있다.
		반면 primivite type은 null 을 표시할 방법이 없다
	 */
	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;

	private String username;

	/*
		- Primitive Type 의 장점
			공유참조문제가 없다 (하지만 Wrapper Class 도 이뮤터블 객체이다 )
			성능이 조금이라도 더 좋다 (아니 좋을것같다)
			nullPointException 발생이 원천봉쇄된다

		- Wrapper Class 사용의 장점
			멀티스레드에서 사용할 수 있다
			nullPointException 이 발생할수는 있지만 null 임을 표시할수 있다.
			명시적으로 vaildataion 을 사용할 수 있다
				- @NonNull, @NotNull.. 등등

		Primitive Type vs Wrapper Class
			가장 일반적인 구현체인 Hibernate의 문서를 보면 명시적으로 non-primitive type의 사용을 권장하고 있다.
			판단기준은 Null을 원천봉쇄 하느냐 아니냐의 차이인데, 일부 필드에서 primitive Type 도 사용하고 있다.
	 */
	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	public Member(String username) {
		this(username, 0);
	}

	public Member(
		String username,
		int age
	) {
		this(username, age, null);
	}

	public Member(
		String username,
		int age,
		Team team
	) {
		this.username = username;
		this.age = age;
		if (team != null) {
			changeTeam(team);
		}
	}

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
