package com.learning.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class QuizServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
