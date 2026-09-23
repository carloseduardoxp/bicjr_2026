package com.openisle.controller;

import com.openisle.model.Reaction;
import com.openisle.model.ReactionType;
import com.openisle.service.ReactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<ReactionDto> reactToPost(@PathVariable Long postId,
                                                  @RequestBody ReactionRequest req,
                                                  Authentication auth) {
        Reaction reaction = reactionService.reactToPost(auth.getName(), postId, req.getType());
        return ResponseEntity.ok(toDto(reaction));
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ReactionDto> reactToComment(@PathVariable Long commentId,
                                                     @RequestBody ReactionRequest req,
                                                     Authentication auth) {
        Reaction reaction = reactionService.reactToComment(auth.getName(), commentId, req.getType());
        return ResponseEntity.ok(toDto(reaction));
    }

    private ReactionDto toDto(Reaction reaction) {
        ReactionDto dto = new ReactionDto();
        dto.setId(reaction.getId());
        dto.setType(reaction.getType());
        dto.setUser(reaction.getUser().getUsername());
        if (reaction.getPost() != null) {
            dto.setPostId(reaction.getPost().getId());
        }
        if (reaction.getComment() != null) {
            dto.setCommentId(reaction.getComment().getId());
        }
        return dto;
    }

    @Data
    private static class ReactionRequest {
        private ReactionType type;
    }

    @Data
    private static class ReactionDto {
        private Long id;
        private ReactionType type;
        private String user;
        private Long postId;
        private Long commentId;
    }
}
