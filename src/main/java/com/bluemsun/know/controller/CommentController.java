package com.bluemsun.know.controller;

import com.bluemsun.know.entity.FirstComment;
import com.bluemsun.know.entity.SecondComment;
import com.bluemsun.know.service.CommentService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @RequestMapping("/addFirst")
    public Result addFirstComment(HttpServletRequest request,@RequestBody FirstComment comment){
        return commentService.addFirstComment(request,comment);
    }

    @RequestMapping("/addSecond")
    public Result addSecondComment(HttpServletRequest request, @RequestBody SecondComment comment){
        return commentService.addSecondComment(request,comment);
    }

    @RequestMapping("/show/{sortType}/{postId}")
    public Result showComments(HttpServletRequest request, @PathVariable int postId, @PathVariable int sortType){
        return commentService.getCommentByPost(postId,sortType,request);
    }

    @RequestMapping("/updateLike/{likeType}/{commentId}")
    public Result updateCommentLike(HttpServletRequest request,@PathVariable int likeType, @PathVariable int commentId){
        return commentService.updateCommentLike(request,commentId,likeType);
    }

    @RequestMapping("/delFirst/{commentId}")
    public Result delFirst(HttpServletRequest request, @PathVariable int commentId){
        return commentService.delFirstComment(request,commentId);
    }

    @RequestMapping("/delSecond/{commentId}")
    public Result delSecond(HttpServletRequest request,@PathVariable int commentId){
        return commentService.delSecondComment(request,commentId);
    }
}
