package com.openisle.service;

import com.openisle.model.*;
import com.openisle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final PostSubscriptionRepository postSubRepo;
    private final CommentSubscriptionRepository commentSubRepo;
    private final UserSubscriptionRepository userSubRepo;
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;

    public void subscribePost(String username, Long postId) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Post post = postRepo.findById(postId).orElseThrow();
        postSubRepo.findByUserAndPost(user, post).orElseGet(() -> {
            PostSubscription ps = new PostSubscription();
            ps.setUser(user);
            ps.setPost(post);
            return postSubRepo.save(ps);
        });
    }

    public void unsubscribePost(String username, Long postId) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Post post = postRepo.findById(postId).orElseThrow();
        postSubRepo.findByUserAndPost(user, post).ifPresent(postSubRepo::delete);
    }

    public void subscribeComment(String username, Long commentId) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        commentSubRepo.findByUserAndComment(user, comment).orElseGet(() -> {
            CommentSubscription cs = new CommentSubscription();
            cs.setUser(user);
            cs.setComment(comment);
            return commentSubRepo.save(cs);
        });
    }

    public void unsubscribeComment(String username, Long commentId) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        commentSubRepo.findByUserAndComment(user, comment).ifPresent(commentSubRepo::delete);
    }

    public void subscribeUser(String username, String targetName) {
        if (username.equals(targetName)) return;
        User subscriber = userRepo.findByUsername(username).orElseThrow();
        User target = userRepo.findByUsername(targetName).orElseThrow();
        userSubRepo.findBySubscriberAndTarget(subscriber, target).orElseGet(() -> {
            UserSubscription us = new UserSubscription();
            us.setSubscriber(subscriber);
            us.setTarget(target);
            return userSubRepo.save(us);
        });
    }

    public void unsubscribeUser(String username, String targetName) {
        User subscriber = userRepo.findByUsername(username).orElseThrow();
        User target = userRepo.findByUsername(targetName).orElseThrow();
        userSubRepo.findBySubscriberAndTarget(subscriber, target).ifPresent(userSubRepo::delete);
    }

    public List<User> getSubscribedUsers(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return userSubRepo.findBySubscriber(user).stream().map(UserSubscription::getTarget).toList();
    }

    public List<User> getSubscribers(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return userSubRepo.findByTarget(user).stream().map(UserSubscription::getSubscriber).toList();
    }

    public List<User> getPostSubscribers(Long postId) {
        Post post = postRepo.findById(postId).orElseThrow();
        return postSubRepo.findByPost(post).stream().map(PostSubscription::getUser).toList();
    }

    public List<User> getCommentSubscribers(Long commentId) {
        Comment c = commentRepo.findById(commentId).orElseThrow();
        return commentSubRepo.findByComment(c).stream().map(CommentSubscription::getUser).toList();
    }


    public long countSubscribers(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return userSubRepo.countByTarget(user);
    }

    public long countSubscribed(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        return userSubRepo.countBySubscriber(user);
    }
}
