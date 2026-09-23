package com.openisle.service;

import com.openisle.model.User;
import com.openisle.repository.CommentRepository;
import com.openisle.repository.PostRepository;
import com.openisle.repository.ReactionRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LevelService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final UserVisitService userVisitService;

    private static final int[] LEVEL_EXP = {100,200,300,600,1200,10000};

    private int addExperience(User user, int amount) {
        user.setExperience(user.getExperience() + amount);
        userRepository.save(user);
        return amount;
    }

    public int awardForPost(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        long count = postRepository.countByAuthorAfter(username, start);
        if (count >= 1) return 0;
        return addExperience(user,30);
    }

    public int awardForComment(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        long count = commentRepository.countByAuthorAfter(username, start);
        if (count >= 3) return 0;
        return addExperience(user,10);
    }

    public int awardForReaction(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        long count = reactionRepository.countByUserAfter(username, start);
        if (count >= 3) return 0;
        return addExperience(user,5);
    }

    public int awardForSignin(String username) {
        boolean first = userVisitService.recordVisit(username);
        if (!first) return 0;
        User user = userRepository.findByUsername(username).orElseThrow();
        return addExperience(user,5);
    }

    public int getLevel(int exp) {
        int level = 0;
        for (int t : LEVEL_EXP) {
            if (exp >= t) level++; else break;
        }
        return level;
    }

    public int nextLevelExp(int exp) {
        for (int t : LEVEL_EXP) {
            if (exp < t) return t;
        }
        return LEVEL_EXP[LEVEL_EXP.length-1];
    }
}
