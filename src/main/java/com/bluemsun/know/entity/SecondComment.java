package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class SecondComment {
    private int id;
    private int firstId;
    private int replyUserId;
    private int repliedUserId;
    private String text;
    private String time;
    private int likeNumber;

    private User replyUser;
    private User repliedUser;

    private int likeStatus;
    private int delStatus;
    private int position;
}
