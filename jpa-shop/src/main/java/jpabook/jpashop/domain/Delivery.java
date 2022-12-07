package jpabook.jpashop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Delivery extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DELIVERY_ID")
	private Long id;

	private String city;

	private String street;

	private String zipcode;

	private DeliveryStatus status;

	@OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
	private Order order;
}
