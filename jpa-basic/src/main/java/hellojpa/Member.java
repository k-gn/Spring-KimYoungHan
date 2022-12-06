package hellojpa;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
// @Table(name = "MBR")

// @SequenceGenerator(
// 	name = “MEMBER_SEQ_GENERATOR",
// 	sequenceName = “MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
// 	initialValue = 1, allocationSize = 1)
// initialValue : 초기값, allocationSize : 생성할 증가값 (allocationSize = 50 이면 50개씩 생성) - 성능 최적화 -> 매번 시퀀스를 호출하는 게 아닌 미리 만들어서 메모리에 올려놓고 사용 (동시성 이슈 없음)


// @TableGenerator(
// 	name = "MEMBER_SEQ_GENERATOR",
// 	table = "MY_SEQUENCES",
// 	pkColumnValue = “MEMBER_SEQ", allocationSize = 1)
public class Member {

	@Id // PK mapping
	// @GeneratedValue(strategy = GenerationType.AUTO) // 자동 키 생성 전략
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR") // generator 이름은 SequenceGenerator 이름과 동일
	/*
		직접 할당: @Id만 사용
		자동 생성(@GeneratedValue)
			- IDENTITY: 데이터베이스에 위임 (주로 MySQL, PostgreSQL, SQL Server, DB2에서 사용)
				- ID 값을 INSERT SQL 실행 이후에 알 수 있다 -> 이 경우에만 커밋 전에 insert 쿼리를 날리고 키값을 세팅해준다. -> 키값을 알 수 있어졌지만, 모아서 보내는 이점을 못누린다.
				- 사실 한 네트워크에서 여러번 INSERT 한다고 해서 비약적으로 성능 이슈가 생기는 건 아니다.
			- SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE / @SequenceGenerator 필요
				- 시퀀스가 생성되고 키값이 영속성에 들어갈 때 먼저 시퀀스를 호출하고 키값을 바로 세팅한다.
			- TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용 / @TableGenerator 필요 / 성능 이슈가 있다, 잘안씀
			- AUTO: 방언에 따라 자동 지정, 기본값

		권장하는 식별자 전략
			- 기본키 제약조건 : null 이 아니며 유일하고 변하면 안됨
			- 제약조건을 만족하는 키를 찾기 어렵다. -> 대체키 사용
			- Long 형 + 대체키(rule) + 키 생성 전략을 조합하여 사용 권장
	*/
	private Long id;

	// unique 조건 같은 경우 애플리케이션에 적용이 아닌 데이터 베이스에 쿼리로 적용되는 것 (사실 컬럼 어노테이션에선 유니크를 잘 쓰지 않고 테이블 어노테이션에서 쓴다.)
	// @Column(name = "username", unique = true, length = 10, nullable = true)
	@Column(name = "name", columnDefinition = "varchar(100) default 'anonymous'")
	private String username;

	private Integer age;

	@Enumerated(EnumType.STRING) // enum mapping, ORDINAL 사용 x
	private RoleType roleType;

	@Temporal(TemporalType.TIMESTAMP) // date mapping
	private Date createdDate;

	private LocalDateTime createdAt; // LocalDate 사용 시 생략 가능

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	@Lob // big string contents (문자면 clob, 나머지는 blob)
	private String description;

	@Transient // 필드매핑 x -> 엔티티에 포함시키지 않음 (메모리에서만 사용하곗다는 의미)
	private int temp;
}
