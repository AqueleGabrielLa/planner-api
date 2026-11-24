package com.gabriel.plannerapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlannerApiApplicationTests {

	@LocalServerPort
	private int port;

	@Test
	void contextLoads() {
	}

	@Test
	void loginComSenhaErradaRetorna401() throws IOException {
		String email = "login-erro@email.com";
		String registro = """
				{"name":"Usuario Teste","email":"%s","password":"123456"}
				""".formatted(email);

		assertThat(post("/auth/register", registro)).isEqualTo(200);
		assertThat(post("/auth/login", """
				{"email":"%s","password":"senha-errada"}
				""".formatted(email))).isEqualTo(401);
		assertThat(post("/auth/login", """
				{"email":"%s","password":"123456"}
				""".formatted(email))).isEqualTo(200);
	}

	private int post(String path, String body) throws IOException {
		HttpURLConnection conexao = (HttpURLConnection) new URL("http://localhost:" + port + path).openConnection();
		conexao.setRequestMethod("POST");
		conexao.setRequestProperty("Content-Type", "application/json");
		conexao.setDoOutput(true);
		try (var output = conexao.getOutputStream()) {
			output.write(body.getBytes(StandardCharsets.UTF_8));
		}
		return conexao.getResponseCode();
	}

}
