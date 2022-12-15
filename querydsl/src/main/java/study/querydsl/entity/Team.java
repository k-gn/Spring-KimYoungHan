package study.querydsl.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
/*
	빌더 패턴을 적용하면, 객체의 일관성을 유지하기 위해 객체 생성 시점에 값들을 넣어줌으로써, Setter의 사용을 줄일 수 있게 된다.
	그리고, 기본 생성자 접근자를 protected로 변경하면 new Board() 사용을 막을 수 있어 객체의 일관성을 더욱 유지할 수 있게된다.

	@AllArgsConstructor는 필드에 존재하는 모든 변수들을 받는 생성자를 생성하는데
	필드 변수의 순서에도 영향을 받아 순서에 의해 에러가 발생할 수도 있다.
	그리고 접근할 필요가 없는 필드값에 대해서 접근할 수 있게되어 접근가능성을 오히려 최대화하는 문제가 생긴다.
	꼭 받고 나가야 되는 필드값만 넣는 생성자를 따로 만들어서 쓰도록 하자

	@Setter는 데이터값이 변경되도록 의도된 DTO와 같은 곳에서는 자유롭게 사용하지만
	@Entity와 같이 외부에서의 접근가능성을 최소화하여 안정성을 최대화해야 할 때에는 @Setter를 지양해야 한다.
	이때 @Setter를 대체하는 방법으로는 어떤 특별한 의미가 있는(update, pointUp 등)과 같이 메서드를 만들어 그 메서드를 통해 꼭 필요한 파라미터만 주고받는 방법을 사용하는 것을 지향하자
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id, name"})
public class Team {

	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;

	private String name;

	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

	public Team(String name) {
		this.name = name;
	}
}
