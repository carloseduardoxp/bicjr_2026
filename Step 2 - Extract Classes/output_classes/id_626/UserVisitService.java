package com.openisle.service;

import com.openisle.model.User;
import com.openisle.model.UserVisit;
import com.openisle.repository.UserRepository;
import com.openisle.repository.UserVisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserVisitService {
    private final UserVisitRepository userVisitRepository;
    private final UserRepository userRepository;

    public void recordVisit(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDate today = LocalDate.now();
        userVisitRepository.findByUserAndVisitDate(user, today).orElseGet(() -> {
            UserVisit visit = new UserVisit();
            visit.setUser(user);
            visit.setVisitDate(today);
            return userVisitRepository.save(visit);
        });
    }

    public long countVisits(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userVisitRepository.countByUser(user);
    }
}
