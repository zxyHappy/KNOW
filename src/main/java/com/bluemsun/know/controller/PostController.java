package com.bluemsun.know.controller;


import com.bluemsun.know.entity.EditorResp;
import com.bluemsun.know.entity.Page;
import com.bluemsun.know.entity.Post;
import com.bluemsun.know.service.PostService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;

    @RequestMapping("/add")
    public Result addPost(HttpServletRequest request, @RequestBody Post post){
        return postService.addPost(request,post);
    }

    @RequestMapping("/getSelf")
    public Result getSelfPosts(HttpServletRequest request, @RequestBody Map<String, Object> map){
        int userId = (int) request.getAttribute("id");
        return postService.getSelfPost(userId,map);
    }

    @RequestMapping("/getSelfCollect")
    public Result getCollect(HttpServletRequest request){
        int userId = (int) request.getAttribute("id");
        return postService.getCollectPost(userId);
    }

    @RequestMapping("/show/{postId}")
    public Result showPost(@PathVariable int postId, HttpServletRequest request){
        return postService.getPostMessage(postId,request);
    }

    @RequestMapping("/collect/{postId}")
    public Result collectPost(@PathVariable int postId, HttpServletRequest request){
        return postService.updateCollect(request,postId);
    }

    @RequestMapping("/like/{postId}")
    public Result likePost(@PathVariable int postId, HttpServletRequest request){
        return postService.updatePostLike(request,postId);
    }

    @RequestMapping("/update")
    public Result updatePost(HttpServletRequest request,@RequestBody Post post){
        return postService.updatePost(post,request);
    }

    @RequestMapping("/addPhoto")
    public EditorResp addPhoto(MultipartFile photo){
        return postService.addPhoto(photo);
    }

    @RequestMapping("/showUser")
    public Result getPostsByUser(HttpServletRequest request, @RequestBody Map<String,Object> map){
        return postService.getPostsByUser(request,map);
    }

    @RequestMapping("/showCollect/{userId}")
    public Result getCollectPostByUser(HttpServletRequest request, @PathVariable int userId){
        return postService.getCollectPostByUser(request,userId);
    }

    @RequestMapping("/del/{postId}")
    public Result delPost(HttpServletRequest request,@PathVariable int postId){
        return postService.delPost(request,postId);
    }
}
