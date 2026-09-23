package com.openisle.controller;

import com.openisle.model.Tag;
import com.openisle.service.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    void createAndGetTag() throws Exception {
        Tag t = new Tag();
        t.setId(1L);
        t.setName("java");
        Mockito.when(tagService.createTag(eq("java"))).thenReturn(t);
        Mockito.when(tagService.getTag(1L)).thenReturn(t);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"java\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"));

        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listTags() throws Exception {
        Tag t = new Tag();
        t.setId(2L);
        t.setName("spring");
        Mockito.when(tagService.listTags()).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("spring"));
    }
}
