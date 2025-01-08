package com.ywcheong.short4;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("테스트 시작")
class Short4ApplicationTests {

	@LocalServerPort
	int localServerPort;

	@Test
	@DisplayName("난수 생성된 포트에서 테스트 서버 구동")
	void contextLoads() {
		System.out.printf("""
				Event handler contextLoads() :: server opened with random port :: port [%d]%n""", localServerPort);
	}

}
