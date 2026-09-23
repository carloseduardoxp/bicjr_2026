package com.openisle.integration;

import com.openisle.model.Role;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import com.openisle.service.EmailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchIntegrationTest {
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private UserRepository users;
    @MockBean
    private EmailSender emailService;

    private String registerAndLogin(String username, String email) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        rest.postForEntity("/api/auth/register", new HttpEntity<>(
                Map.of("username", username, "email", email, "password", "pass123"), h), Map.class);
        User u = users.findByUsername(username).orElseThrow();
        rest.postForEntity("/api/auth/verify", new HttpEntity<>(
                Map.of("username", username, "code", u.getVerificationCode()), h), Map.class);
        ResponseEntity<Map> resp = rest.postForEntity("/api/auth/login", new HttpEntity<>(
                Map.of("username", username, "password", "pass123"), h), Map.class);
        return (String) resp.getBody().get("token");
    }

    private String registerAndLoginAsAdmin(String username, String email) {
        String token = registerAndLogin(username, email);
        User u = users.findByUsername(username).orElseThrow();
        u.setRole(Role.ADMIN);
        users.save(u);
        return token;
    }

    private ResponseEntity<Map> postJson(String url, Map<?,?> body, String token) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) h.setBearerAuth(token);
        return rest.exchange(url, HttpMethod.POST, new HttpEntity<>(body, h), Map.class);
    }

    @Test
    void globalSearchReturnsMixedResults() {
        String admin = registerAndLoginAsAdmin("admin", "a@a.com");
        String user = registerAndLogin("bob", "b@b.com");

        ResponseEntity<Map> catResp = postJson("/api/categories", Map.of("name", "misc"), admin);
        Long catId = ((Number)catResp.getBody().get("id")).longValue();

        ResponseEntity<Map> postResp = postJson("/api/posts",
                Map.of("title", "Hello World", "content", "Some content", "categoryId", catId), user);
        Long postId = ((Number)postResp.getBody().get("id")).longValue();

        postJson("/api/posts/" + postId + "/comments",
                Map.of("content", "Nice article"), admin);

        List<Map<String, Object>> results = rest.getForObject("/api/search/global?keyword=nic", List.class);
        assertEquals(3, results.size());
        assertTrue(results.stream().anyMatch(m -> "user".equals(m.get("type"))));
        assertTrue(results.stream().anyMatch(m -> "post".equals(m.get("type"))));
        assertTrue(results.stream().anyMatch(m -> "comment".equals(m.get("type"))));
    }
}
