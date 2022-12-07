package jpabook.jpashop.domain;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseEntity {

	// 이런애들 자동으로 JPA 에서 이벤트로 넣을 수 있음.
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
}
