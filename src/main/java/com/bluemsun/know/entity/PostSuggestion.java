package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class PostSuggestion {
    private int key;
    private String title;
    private List<Label> labels;

    public PostSuggestion() {
    }

    public PostSuggestion(int key, String title, List<Label> labels) {
        this.key = key;
        this.title = title;
        this.labels = labels;
    }
}
