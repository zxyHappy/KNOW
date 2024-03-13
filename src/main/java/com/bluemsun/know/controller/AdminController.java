package com.bluemsun.know.controller;

import com.bluemsun.know.service.BlockService;
import com.bluemsun.know.service.PostService;
import com.bluemsun.know.service.UserService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    BlockService blockService;

    @RequestMapping("/ban/{userId}")
    public Result updateBanUserByAdmin(@PathVariable int userId){
        return userService.updateBanUser(userId);
    }

    @RequestMapping("/user/{typeId}")
    public Result getUserByType(@PathVariable int typeId){
        if(typeId == -1){
            return userService.getAllUserByAdmin();
        }else {
            return userService.getUserByType(typeId);
        }
    }

    @RequestMapping("/search/{userName}")
    public Result searchUserByAdmin(@PathVariable String userName){
        return userService.searchUserByAdmin(userName);
    }

    @RequestMapping("/search/post")
    public Result searchPostByAdmin(@RequestBody Map<String,Object> map){
        return postService.searchPostByAdmin(map);
    }

    @RequestMapping("/showPost/{postId}")
    public Result showReviewPost(@PathVariable int postId){
        return postService.getReviewPostById(postId);
    }

    @RequestMapping("/get-review")
    public Result getReviewPosts(){
        return postService.getReviewPosts();
    }


    @RequestMapping("/update-view")
    public Result reviewPostsByAdmin(@RequestBody Map<String,Object> map){
        return postService.reviewPostsByAdmin(map);
    }

    @RequestMapping("/del")
    public Result delPostByAdmin(@RequestBody Map<String,Object> map){
        return postService.delPostByAdmin(map);
    }
}
