package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class FirstComment {
    private int id;
    private int userId;
    private int postId;
    private String text;
    private String time;
    private int likeNumber;
    private User user;
    private int likeStatus;

    private int delStatus;

    private int position;

    private List<SecondComment> secondComments;

    public FirstComment() {
    }

    public FirstComment(int id, int userId, int postId, String text, String time, int likeNumber) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.time = time;
        this.likeNumber = likeNumber;
    }

    public FirstComment(int id, int userId, int postId, String text, String time, int likeNumber, User user, int likeStatus) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.text = text;
        this.time = time;
        this.likeNumber = likeNumber;
        this.user = user;
        this.likeStatus = likeStatus;
    }
}
