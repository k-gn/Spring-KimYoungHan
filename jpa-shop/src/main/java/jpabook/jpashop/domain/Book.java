package jpabook.jpashop.domain;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Book extends Item {

	private String author;

	private String isbn;
}
