package com.gabriel.plannerapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.plannerapi.user.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlannerApiApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private UserAccountRepository userRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void contextLoads() {
	}

	@Test
	void rotasDeTarefasUsamTasks() throws IOException {
		String token = tokenDe(post("/auth/register", registro("rotas-tasks@email.com")));

		assertThat(get("/tasks", token).status()).isEqualTo(200);
		assertThat(get("/task", token).status()).isEqualTo(404);
		assertThat(get("/task/", token).status()).isEqualTo(404);
	}

	@Test
	void loginComSenhaErradaRetorna401() throws IOException {
		String email = "login-erro@email.com";

		assertThat(post("/auth/register", registro(email)).status()).isEqualTo(200);
		assertThat(post("/auth/login", login(email, "senha-errada")).status()).isEqualTo(401);
		assertThat(post("/auth/login", login(email, "123456")).status()).isEqualTo(200);
	}

	@Test
	void usuarioSoPodeLerSeusPropriosDados() throws IOException {
		String emailA = "user-a@email.com";
		String emailB = "user-b@email.com";
		String tokenA = tokenDe(post("/auth/register", registro(emailA)));
		String tokenB = tokenDe(post("/auth/register", registro(emailB)));
		UUID idB = userRepository.findByEmail(emailB).orElseThrow().getId();

		assertThat(get("/users/" + idB, tokenA).status()).isEqualTo(404);

		Resposta propria = get("/users/" + idB, tokenB);
		assertThat(propria.status()).isEqualTo(200);
		assertThat(propria.body()).contains(emailB).doesNotContain("password");
	}

	private String registro(String email) {
		return """
				{"name":"Usuario Teste","email":"%s","password":"123456"}
				""".formatted(email);
	}

	private String login(String email, String senha) {
		return """
				{"email":"%s","password":"%s"}
				""".formatted(email, senha);
	}

	private String tokenDe(Resposta resposta) throws IOException {
		return objectMapper.readTree(resposta.body()).get("token").asText();
	}

	private Resposta get(String path, String token) throws IOException {
		HttpURLConnection conexao = (HttpURLConnection) new URL("http://localhost:" + port + path).openConnection();
		conexao.setRequestMethod("GET");
		conexao.setRequestProperty("Authorization", "Bearer " + token);
		return new Resposta(conexao.getResponseCode(), corpo(conexao));
	}

	private Resposta post(String path, String body) throws IOException {
		HttpURLConnection conexao = (HttpURLConnection) new URL("http://localhost:" + port + path).openConnection();
		conexao.setRequestMethod("POST");
		conexao.setRequestProperty("Content-Type", "application/json");
		conexao.setDoOutput(true);
		try (var output = conexao.getOutputStream()) {
			output.write(body.getBytes(StandardCharsets.UTF_8));
		}
		return new Resposta(conexao.getResponseCode(), corpo(conexao));
	}

	private String corpo(HttpURLConnection conexao) throws IOException {
		var stream = conexao.getResponseCode() >= 400 ? conexao.getErrorStream() : conexao.getInputStream();
		return stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	private record Resposta(int status, String body) {}

}
