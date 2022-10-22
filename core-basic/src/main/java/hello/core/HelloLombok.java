package hello.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelloLombok {

	private String name;
	private int age;

	public static void main(String[] args) {
		HelloLombok helloLombok = new HelloLombok();
		helloLombok.setName("name");
		helloLombok.setAge(10);

		System.out.println(helloLombok.getName());
		System.out.println(helloLombok.getAge());
	}
}
