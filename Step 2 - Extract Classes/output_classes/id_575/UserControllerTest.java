package com.openisle.controller;

import com.openisle.model.User;
import com.openisle.service.ImageUploader;
import com.openisle.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private ImageUploader imageUploader;

    @Test
    void getCurrentUser() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("a@b.com");
        u.setAvatar("http://x/avatar.png");
        Mockito.when(userService.findByUsername("alice")).thenReturn(Optional.of(u));

        mockMvc.perform(get("/api/users/me").principal(new UsernamePasswordAuthenticationToken("alice","p")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avatar").value("http://x/avatar.png"));
    }

    @Test
    void uploadAvatar() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.png", MediaType.IMAGE_PNG_VALUE, "img".getBytes());
        Mockito.when(imageUploader.upload(any(), eq("a.png"))).thenReturn("http://img/a.png");

        mockMvc.perform(multipart("/api/users/me/avatar").file(file).principal(new UsernamePasswordAuthenticationToken("alice","p")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://img/a.png"));

        Mockito.verify(userService).updateAvatar("alice", "http://img/a.png");
    }
}
