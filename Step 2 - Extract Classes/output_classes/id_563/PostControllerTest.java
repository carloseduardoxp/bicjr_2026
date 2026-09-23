package com.openisle.controller;

import com.openisle.model.Post;
import com.openisle.model.User;
import com.openisle.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Test
    void createAndGetPost() throws Exception {
        User user = new User();
        user.setUsername("alice");
        Post post = new Post();
        post.setId(1L);
        post.setTitle("t");
        post.setContent("c");
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(user);
        Mockito.when(postService.createPost(eq("alice"), eq("t"), eq("c"))).thenReturn(post);
        Mockito.when(postService.getPost(1L)).thenReturn(post);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content("{\"title\":\"t\",\"content\":\"c\"}")
                        .principal(new UsernamePasswordAuthenticationToken("alice", "p")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("t"));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listPosts() throws Exception {
        User user = new User();
        user.setUsername("bob");
        Post post = new Post();
        post.setId(2L);
        post.setTitle("hello");
        post.setContent("world");
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(user);
        Mockito.when(postService.listPosts()).thenReturn(List.of(post));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("hello"));
    }
}
