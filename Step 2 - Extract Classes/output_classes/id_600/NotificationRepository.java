package com.openisle.repository;

import com.openisle.model.Notification;
import com.openisle.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repository for Notification entities. */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadOrderByCreatedAtDesc(User user, boolean read);
}
