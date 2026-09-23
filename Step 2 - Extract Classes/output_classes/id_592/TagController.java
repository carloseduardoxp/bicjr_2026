package com.openisle.controller;

import com.openisle.model.Tag;
import com.openisle.service.TagService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping
    public TagDto create(@RequestBody TagRequest req) {
        Tag tag = tagService.createTag(req.getName());
        return toDto(tag);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.deleteTag(id);
    }

    @GetMapping
    public List<TagDto> list() {
        return tagService.listTags().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagDto get(@PathVariable Long id) {
        return toDto(tagService.getTag(id));
    }

    private TagDto toDto(Tag tag) {
        TagDto dto = new TagDto();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    @Data
    private static class TagRequest {
        private String name;
    }

    @Data
    private static class TagDto {
        private Long id;
        private String name;
    }
}
