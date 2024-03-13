package com.bluemsun.know.service;

import com.bluemsun.know.entity.FirstComment;
import com.bluemsun.know.entity.Page;
import com.bluemsun.know.entity.SecondComment;
import com.bluemsun.know.mapper.CommentMapper;
import com.bluemsun.know.mapper.PostMapper;
import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    NoticeService noticeService;

    public Result getCommentByPost(int postId, int sortType, HttpServletRequest request){

        List<FirstComment> commentList = null;

        // 时间顺序
        try{
            if (sortType == 0) {
                commentList = commentMapper.getFirstCommentByPost(postId);
            } else {
                // 点赞量排序
                commentList = commentMapper.getFirstHotCommentByPost(postId);
            }

            int userId = 0;
            if (request.getAttribute("id") != null) {
                userId = (int) request.getAttribute("id");
            }

            for (FirstComment comment : commentList) {
                comment.setPosition(1);
                if (userMapper.getLikeStatus(comment.getId(), userId, 1) != null) {
                    comment.setLikeStatus(1);
                }
                comment.setUser(userMapper.getUserById(comment.getUserId()));
                if(userId == comment.getUserId() || userId == postMapper.getPostById(postId).getUserId()){
                    comment.setDelStatus(1);
                }
                List<SecondComment> secondComments = commentMapper.getSecondCommentByFirstId(comment.getId());
                for(SecondComment secondComment : secondComments){
                    secondComment.setPosition(2);
                    if(userMapper.getLikeStatus(secondComment.getId(),userId,2) != null){
                        secondComment.setLikeStatus(1);
                    }
                    if(userId == secondComment.getReplyUserId() || userId == comment.getUserId() || userId == postMapper.getPostById(postId).getUserId()){
                        secondComment.setDelStatus(1);
                    }
                    secondComment.setReplyUser(userMapper.getUserById(secondComment.getReplyUserId()));
                    secondComment.setRepliedUser(userMapper.getUserById(secondComment.getRepliedUserId()));
                }
                comment.setSecondComments(secondComments);
            }
            return Result.success("获取评论成功", commentList);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result addFirstComment(HttpServletRequest request, FirstComment comment){
        try{
            int userId = (int) request.getAttribute("id");
            comment.setUserId(userId);
            int i = commentMapper.addFirstComment(comment);
            noticeService.addCommentNotice(comment.getText(),postMapper.getPostById(comment.getPostId()).getUserId(),userId,comment.getPostId());
            if (i != 1) {
                return Result.error(300, "未知错误", null);
            } else {
                return Result.success("评论成功", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result delFirstComment(HttpServletRequest request, int commentId){
        try {
            int userId = (int) request.getAttribute("id");
            FirstComment comment = commentMapper.getCommentById(commentId);
            int postUserId = postMapper.getPostById(comment.getPostId()).getUserId();
            if (comment.getUserId() == userId || postUserId == userId) {
                commentMapper.delFirstComment(commentId);
                return Result.success("删除成功", null);
            }
            return Result.error(300,"删除失败",null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updateCommentLike(HttpServletRequest request, int commentId, int likeType){
        try{
            int userId = (int) request.getAttribute("id");
            if (userMapper.getLikeStatus(commentId, userId, likeType) != null) {
                userMapper.delLike(commentId, userId, likeType);
                if(likeType == 1){
                    commentMapper.delFirstLike(commentId);
                }else {
                    commentMapper.delSecondLike(commentId);
                }
                return Result.success("取消成功", null);
            } else {
                userMapper.addLike(commentId, userId, likeType);
                if(likeType == 1){
                    commentMapper.addFirstLike(commentId);
                }else {
                    commentMapper.addSecondLike(commentId);
                }
                return Result.success("点赞成功", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Result addSecondComment(HttpServletRequest request, SecondComment secondComment){
        try{
            int userId = (int) request.getAttribute("id");
            secondComment.setReplyUserId(userId);
            int i = commentMapper.addSecondComment(secondComment);
            noticeService.addCommentNotice(secondComment.getText(),secondComment.getRepliedUserId(),userId,commentMapper.getCommentById(secondComment.getFirstId()).getPostId());
            if (i == 1) {
                return Result.success("回复成功", null);
            } else {
                return Result.error(300, "回复失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Result delSecondComment(HttpServletRequest request, int commentId){
        try {
            int selfId = (int) request.getAttribute("id");
            SecondComment comment = commentMapper.getSecondCommentById(commentId);
            int firstUserId = commentMapper.getCommentById(comment.getFirstId()).getUserId();
            int postUserId = postMapper.getPostById(commentMapper.getCommentById(comment.getFirstId()).getPostId()).getUserId();
            int userId = commentMapper.getSecondCommentById(commentId).getReplyUserId();
            if (selfId == firstUserId || selfId == postUserId || selfId == userId) {
                commentMapper.delSecondComment(commentId);
                return Result.success("删除成功", null);
            }
            return Result.error(300, "删除失败", null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

}
