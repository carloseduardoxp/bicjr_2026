package com.openisle.service;

import com.openisle.model.Post;
import com.openisle.model.PostStatus;
import com.openisle.model.Comment;
import com.openisle.model.User;
import com.openisle.repository.PostRepository;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword);
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatus(keyword, keyword, PostStatus.PUBLISHED);
    }

    public List<Post> searchPostsByContent(String keyword) {
        return postRepository
                .findByContentContainingIgnoreCaseAndStatus(keyword, PostStatus.PUBLISHED);
    }

    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository
                .findByTitleContainingIgnoreCaseAndStatus(keyword, PostStatus.PUBLISHED);
    }

    public List<Comment> searchComments(String keyword) {
        return commentRepository.findByContentContainingIgnoreCase(keyword);
    }

    public List<SearchResult> globalSearch(String keyword) {
        Stream<SearchResult> users = searchUsers(keyword).stream()
                .map(u -> new SearchResult("user", u.getId(), u.getUsername()));
        Stream<SearchResult> posts = searchPosts(keyword).stream()
                .map(p -> new SearchResult("post", p.getId(), p.getTitle()));
        Stream<SearchResult> titles = searchPostsByTitle(keyword).stream()
                .map(p -> new SearchResult("post_title", p.getId(), p.getTitle()));
        Stream<SearchResult> comments = searchComments(keyword).stream()
                .map(c -> new SearchResult("comment", c.getId(), c.getContent()));
        return Stream.concat(Stream.concat(Stream.concat(users, posts), titles), comments)
                .toList();
    }

    public record SearchResult(String type, Long id, String text) {}
}
