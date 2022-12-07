package jpabook.jpashop.domain;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Album extends Item {

	private String artist;

	private String etc;
}
