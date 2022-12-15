package study.datajpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/*
	- 등록자, 수정자, 등록자, 수정자는 기본적으로 가져가는 데이터

	JPA 주요 이벤트 어노테이션
	- @PrePersist, @PostPersist @PreUpdate, @PostUpdate

	---

	# 스프링 데이터 JPA auditing

	@EnableJpaAuditing 스프링 부트 설정 클래스에 적용해야함
	@EntityListeners(AuditingEntityListener.class) 엔티티에 적용

	사용 어노테이션
		@CreatedDate
		@LastModifiedDate
		@CreatedBy
		@LastModifiedBy
 */
@MappedSuperclass // 자식에게 속성을 공유 (상속 개념은 아니다)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseTimeEntity {

	// 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록을 추가로 해야한다.
	// 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만, 등록자, 수정자는 없을 수도 있다.
	@CreatedBy
	@Column(updatable = false)
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;

	/*@PrePersist
	public void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdDate = now;
		updatedDate = now;
	}

	@PreUpdate
	public void preUpdate() {
		updatedDate = LocalDateTime.now();
	}*/
}
