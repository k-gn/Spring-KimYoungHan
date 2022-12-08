package jpql;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Team {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TEAM_ID")
	private Long id;

	private String name;

	@OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE)
	private List<Member> members = new ArrayList<>();
}
