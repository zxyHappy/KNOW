package com.bluemsun.know.entity;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Block {

    private int id;
    private String blockName;
    private int postsNumber;
    private int scanNumber;

    public Block() {
    }

    public Block(int id, String blockName, int postsNumber, int scanNumber) {
        this.id = id;
        this.blockName = blockName;
        this.postsNumber = postsNumber;
        this.scanNumber = scanNumber;
    }

    public Block(int id, String blockName) {
        this.id = id;
        this.blockName = blockName;
    }
}
