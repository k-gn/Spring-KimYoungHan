package hello.advanced.trace;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class TestClass {

	@Test
	void given_when_then() {
		System.out.println(generateCode());
	}

	private String generateCode() {
		Random random = new Random();
		String hexString = Long.toHexString(98456).toUpperCase();
		String generatedString = random.ints(48, 123)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(10 - hexString.length())
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		return shift(hexString + generatedString, random.nextInt(generatedString.length()));
	}

	private String shift(
		String code,
		int count
	) {
		for (int i = 1; i <= count; i++)
			code = code.charAt(code.length() - 1) + code.substring(0, code.length() - 1);

		return code;
	}

}
