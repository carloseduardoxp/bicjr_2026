package com.openisle.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.openisle.model.Role;
import com.openisle.model.User;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TwitterAuthService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${twitter.client-id:}")
    private String clientId;

    @Value("${twitter.client-secret:}")
    private String clientSecret;

    public Optional<User> authenticate(String code, com.openisle.model.RegisterMode mode, String redirectUri) {
        try {
            String tokenUrl = "https://api.twitter.com/2/oauth2/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("client_id", clientId);
            body.put("client_secret", clientSecret);
            body.put("code", code);
            body.put("grant_type", "authorization_code");
            if (redirectUri != null) {
                body.put("redirect_uri", redirectUri);
            }

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> tokenRes = restTemplate.postForEntity(tokenUrl, request, JsonNode.class);
            if (!tokenRes.getStatusCode().is2xxSuccessful() || tokenRes.getBody() == null || !tokenRes.getBody().has("access_token")) {
                return Optional.empty();
            }
            String accessToken = tokenRes.getBody().get("access_token").asText();

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(authHeaders);
            ResponseEntity<JsonNode> userRes = restTemplate.exchange(
                    "https://api.twitter.com/2/users/me", HttpMethod.GET, entity, JsonNode.class);
            if (!userRes.getStatusCode().is2xxSuccessful() || userRes.getBody() == null) {
                return Optional.empty();
            }
            JsonNode userNode = userRes.getBody();
            String username = userNode.hasNonNull("username") ? userNode.get("username").asText() : null;
            String email = null;
            if (userNode.hasNonNull("email")) {
                email = userNode.get("email").asText();
            }
            if (email == null) {
                email = username + "@twitter.com";
            }
            return Optional.of(processUser(email, username, mode));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private User processUser(String email, String username, com.openisle.model.RegisterMode mode) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (!user.isVerified()) {
                user.setVerified(true);
                user.setVerificationCode(null);
                userRepository.save(user);
            }
            return user;
        }
        String baseUsername = username != null ? username : email.split("@")[0];
        String finalUsername = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = baseUsername + suffix++;
        }
        User user = new User();
        user.setUsername(finalUsername);
        user.setEmail(email);
        user.setPassword("");
        user.setRole(Role.USER);
        user.setVerified(true);
        user.setApproved(mode == com.openisle.model.RegisterMode.DIRECT);
        user.setAvatar("https://twitter.com/" + finalUsername + "/profile_image");
        return userRepository.save(user);
    }
}
