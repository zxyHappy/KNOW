package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SearchKey {
    private int id;
    private String keyText;
    private int num;

    public SearchKey() {
    }

    public SearchKey(int id, String keyText, int num) {
        this.id = id;
        this.keyText = keyText;
        this.num = num;
    }

    public SearchKey(String keyText, int num) {
        this.keyText = keyText;
        this.num = num;
    }
}
