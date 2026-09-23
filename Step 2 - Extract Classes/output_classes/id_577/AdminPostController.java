package com.openisle.controller;

import com.openisle.model.Post;
import com.openisle.service.PostService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints for administrators to manage posts.
 */
@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class AdminPostController {
    private final PostService postService;

    @GetMapping("/pending")
    public List<PostDto> pendingPosts() {
        return postService.listPendingPosts().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/approve")
    public PostDto approve(@PathVariable Long id) {
        return toDto(postService.approvePost(id));
    }

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setAuthor(post.getAuthor().getUsername());
        dto.setCategory(post.getCategory().getName());
        dto.setViews(post.getViews());
        return dto;
    }

    @Data
    private static class PostDto {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private String author;
        private String category;
        private long views;
    }
}
