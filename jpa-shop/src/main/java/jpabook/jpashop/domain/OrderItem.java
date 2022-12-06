package jpabook.jpashop.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ORDER_ITEM_ID")
	private Long id;

	@Column(name = "ORDER_ID")
	private Long orderId;

	@Column(name = "ITEM_ID")
	private Long itemId;

	private int orderPrice;

	private int count;
}
