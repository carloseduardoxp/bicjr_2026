package com.openisle.service;

import com.openisle.model.Category;
import com.openisle.model.Draft;
import com.openisle.model.Tag;
import com.openisle.model.User;
import com.openisle.repository.CategoryRepository;
import com.openisle.repository.DraftRepository;
import com.openisle.repository.TagRepository;
import com.openisle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DraftService {
    private final DraftRepository draftRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public Draft saveDraft(String username, Long categoryId, String title, String content, List<Long> tagIds) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Draft draft = draftRepository.findByAuthor(user).orElse(new Draft());
        draft.setAuthor(user);
        draft.setTitle(title);
        draft.setContent(content);
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            draft.setCategory(category);
        } else {
            draft.setCategory(null);
        }
        Set<Tag> tags = new HashSet<>();
        if (tagIds != null && !tagIds.isEmpty()) {
            tags.addAll(tagRepository.findAllById(tagIds));
        }
        draft.setTags(tags);
        return draftRepository.save(draft);
    }

    public Optional<Draft> getDraft(String username) {
        return userRepository.findByUsername(username)
                .flatMap(draftRepository::findByAuthor);
    }

    public void deleteDraft(String username) {
        userRepository.findByUsername(username)
                .ifPresent(draftRepository::deleteByAuthor);
    }
}
