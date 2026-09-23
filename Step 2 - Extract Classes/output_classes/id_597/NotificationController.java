package com.openisle.controller;

import com.openisle.model.Notification;
import com.openisle.model.NotificationType;
import com.openisle.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** Endpoints for user notifications. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDto> list(@RequestParam(value = "read", required = false) Boolean read,
                                      Authentication auth) {
        return notificationService.listNotifications(auth.getName(), read).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/read")
    public void markRead(@RequestBody MarkReadRequest req, Authentication auth) {
        notificationService.markRead(auth.getName(), req.getIds());
    }

    private NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setPostId(n.getPost() != null ? n.getPost().getId() : null);
        dto.setCommentId(n.getComment() != null ? n.getComment().getId() : null);
        dto.setApproved(n.getApproved());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    @Data
    private static class MarkReadRequest {
        private List<Long> ids;
    }

    @Data
    private static class NotificationDto {
        private Long id;
        private NotificationType type;
        private Long postId;
        private Long commentId;
        private Boolean approved;
        private boolean read;
        private LocalDateTime createdAt;
    }
}
