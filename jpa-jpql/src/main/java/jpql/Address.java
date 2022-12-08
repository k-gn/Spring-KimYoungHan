package jpql;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Address {

	private String city;

	private String street;

	private String zipcode;
}
