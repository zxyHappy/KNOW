package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Notice {
    private int id;
    private int relatedUserId;
    private User relatedUser;
    private int relatedPostId;
    private Post relatedPost;
    private int noticeType;
    private String text;
    private String time;
    private int viewStatus;
    private String title;

    public Notice() {
    }

    public Notice(int relatedUserId, int relatedPostId, int noticeType, String text) {
        this.relatedUserId = relatedUserId;
        this.relatedPostId = relatedPostId;
        this.noticeType = noticeType;
        this.text = text;
    }

    public Notice(int relatedUserId, int relatedPostId, int noticeType, String title,String text) {
        this.relatedUserId = relatedUserId;
        this.relatedPostId = relatedPostId;
        this.noticeType = noticeType;
        this.text = text;
        this.title = title;
    }
}
