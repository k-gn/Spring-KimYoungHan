package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {
	private Long id;
	private String username;

	public MemberDto(Member m) {
		this.id = m.getId();
		this.username = m.getUsername();

	}
}
