package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class Post {
    private int id;
    private int userId;
    private String title;
    private String body;
    private String time;
    private int scanNumber;
    private int likeNumber;
    private int collectNum;
    private int viewStatus;
    private int postStatus;

    private String photo;
    private int commentNum;

    private List<String> blocksName;

    private List<Integer> blocksId;

    private float weight;

    private int likeStatus;
    private int collectStatus;

    private List<Label> labels;

    private int editStatus;

    private List<Post> relate;

    public Post() {
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, float weight) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.weight = weight;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, int commentNum, float weight) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.commentNum = commentNum;
        this.weight = weight;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, int commentNum, List<String> blocksName, float weight) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.commentNum = commentNum;
        this.blocksName = blocksName;
        this.weight = weight;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, int commentNum, List<String> blocksName, List<Integer> blocksId, float weight) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.commentNum = commentNum;
        this.blocksName = blocksName;
        this.blocksId = blocksId;
        this.weight = weight;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, String photo, int commentNum, List<String> blocksName, List<Integer> blocksId, float weight) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.photo = photo;
        this.commentNum = commentNum;
        this.blocksName = blocksName;
        this.blocksId = blocksId;
        this.weight = weight;
    }

    public Post(int id, int userId, String title, String body, String time, int scanNumber, int likeNumber, int viewStatus, int postStatus, String photo, int commentNum, List<String> blocksName, List<Integer> blocksId, float weight, int likeStatus, int collectStatus) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.time = time;
        this.scanNumber = scanNumber;
        this.likeNumber = likeNumber;
        this.viewStatus = viewStatus;
        this.postStatus = postStatus;
        this.photo = photo;
        this.commentNum = commentNum;
        this.blocksName = blocksName;
        this.blocksId = blocksId;
        this.weight = weight;
        this.likeStatus = likeStatus;
        this.collectStatus = collectStatus;
    }
}
