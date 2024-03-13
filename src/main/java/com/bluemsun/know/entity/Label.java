package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Label {
    private int value;
    private String label;

    public Label(Block block) {
        this.value = block.getId();
        this.label = block.getBlockName();
    }
}
