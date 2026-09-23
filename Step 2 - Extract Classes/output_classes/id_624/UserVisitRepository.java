package com.openisle.repository;

import com.openisle.model.User;
import com.openisle.model.UserVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserVisitRepository extends JpaRepository<UserVisit, Long> {
    Optional<UserVisit> findByUserAndVisitDate(User user, LocalDate visitDate);
    long countByUser(User user);
}
