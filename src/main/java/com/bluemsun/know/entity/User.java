package com.bluemsun.know.entity;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {
    private int id;
    private String userName ;
    private String nickName ; //用户名
    private String password ; //密码
    private String email ; //邮箱
    private String photo;
    private int banStatus;
    private String selfIntroduce;


    private int followNum;
    private int fansNum;
    private int followedStatus;
    private int blackedStatus;

    private int postNum;

    private String code;

    public User() {
    }

    public User(int id, String userName, String nickName, String password, String email, String photo, int banStatus, String selfIntroduce) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.banStatus = banStatus;
        this.selfIntroduce = selfIntroduce;
    }

    public User(int id, String userName, String nickName, String password, String email, String photo, int banStatus, String selfIntroduce, int followNum, int fansNum) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.banStatus = banStatus;
        this.selfIntroduce = selfIntroduce;
        this.followNum = followNum;
        this.fansNum = fansNum;
    }

    public User(int id, String userName, String nickName, String password, String email, String photo, int banStatus, String selfIntroduce, int followNum, int fansNum, int followedStatus) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.banStatus = banStatus;
        this.selfIntroduce = selfIntroduce;
        this.followNum = followNum;
        this.fansNum = fansNum;
        this.followedStatus = followedStatus;
    }

    public User(int id, String userName, String nickName, String password, String email, String photo, int banStatus, String selfIntroduce, int followNum, int fansNum, int followedStatus, int blackedStatus) {
        this.id = id;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.email = email;
        this.photo = photo;
        this.banStatus = banStatus;
        this.selfIntroduce = selfIntroduce;
        this.followNum = followNum;
        this.fansNum = fansNum;
        this.followedStatus = followedStatus;
        this.blackedStatus = blackedStatus;
    }
}
