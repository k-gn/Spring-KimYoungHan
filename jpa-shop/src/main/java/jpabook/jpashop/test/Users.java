package jpabook.jpashop.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class Users {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "USERNAME")
	private String name;

	private int age;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	@ToString.Exclude
	private Team team;

	public void changeTeam(Team team) {
		this.team = team;
		team.getUsers().add(this);
	}
}
