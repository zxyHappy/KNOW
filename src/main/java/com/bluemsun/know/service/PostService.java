package com.bluemsun.know.service;


import com.bluemsun.know.entity.*;
import com.bluemsun.know.mapper.CommentMapper;
import com.bluemsun.know.mapper.PostMapper;
import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.Result;
import com.bluemsun.know.util.UUIDUtil;
import lombok.Data;
import org.openjsse.net.ssl.OpenJSSE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Value("${file.upload.url}")
    private String uploadPath;

    @Value("${file.show.url}")
    private String showUrl;

    @Autowired
    PostMapper postMapper;
    @Autowired
    Page<Post> page;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    NoticeService noticeService;

    @Transactional(rollbackFor = Exception.class)
    public Result addPost(HttpServletRequest request, Post post){
        try{
            int userId = (int) request.getAttribute("id");
            post.setUserId(userId);
            boolean i = postMapper.insertPost(post);
            if (i) {
                for(int id:post.getBlocksId()){
                    postMapper.addBelong(post.getId(),id);
                }
                if(post.getPostStatus() == 1 && post.getViewStatus() == 1){
                    noticeService.addSystemNotice("","",userId,post.getId());
                }
                return Result.success("发布成功", null);
            } else {
                return Result.error(300, "发布失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getSelfPost(int userId, Map<String, Object> map){
        try{
            map.put("userId", userId);
//            int pageIndex = (int) map.get("pageIndex");
            int count = postMapper.getSelfPostNum(map);
            page.setPage(1, 10000, count);
            map.put("startIndex",page.getStartIndex());
            List<Post> list = postMapper.getSelfPosts(map);
            for(Post post:list){
                post.setBlocksName(postMapper.getBlockByPost(post.getId()));
                post.setEditStatus(1);
                if(postMapper.getCollectStatus(userId,post.getId()) != null){
                    post.setCollectStatus(1);
                }
                if(userMapper.getLikeStatus(post.getId(),userId,0) != null){
                    post.setLikeStatus(1);
                }
                post.setCollectNum(postMapper.getCollectNumByPost(post.getId()));
                post.setCommentNum(commentMapper.getFirstCommentNum(post.getId()));
            }
            page.setList(list);
            return Result.success("获取成功", page);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getCollectPost(int userId){
        try{
            int count = postMapper.getCollectPostNum(userId);
            page.setPage(1, 10000, count);
            List<Post> list = postMapper.getCollectPost(userId, page.getStartIndex());
            for(Post post:list){
                post.setBlocksName(postMapper.getBlockByPost(post.getId()));
                if(post.getUserId() == userId){
                    post.setEditStatus(1);
                }
                post.setCollectStatus(1);
                if(userMapper.getLikeStatus(post.getId(),userId,0) != null){
                    post.setLikeStatus(1);
                }
                post.setCommentNum(commentMapper.getFirstCommentNum(post.getId()));
                post.setCollectNum(postMapper.getCollectNumByPost(post.getId()));
            }
            page.setList(list);
            return Result.success("获取成功", page);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getPostMessage(int postId, HttpServletRequest request){
        try{
            postMapper.addScan(postId);
            Post post = postMapper.getPostById(postId);
            User user = userMapper.getUserById(post.getUserId());
            user.setFollowNum(userMapper.getFollowNum(user.getId()));
            user.setFansNum(userMapper.getFansNum(user.getId()));
            int selfId = 0;
            if(request.getAttribute("id") != null){
                selfId = (int) request.getAttribute("id");
            }
            if (userMapper.getFollowStatus(user.getId(), selfId) != null) {
                user.setFollowedStatus(1);
            }
            Map<String, Object> mapPost = new HashMap<>();
            mapPost.put("userId",post.getUserId());
            mapPost.put("viewStatus",1);
            mapPost.put("postStatus",1);
            user.setPostNum(postMapper.getSelfPostNum(mapPost));
            if (postMapper.getCollectStatus(selfId,postId) != null){
                post.setCollectStatus(1);
            }
            if(userMapper.getLikeStatus(postId,selfId,0) != null){
                post.setLikeStatus(1);
            }
            post.setCollectNum(postMapper.getCollectNumByPost(postId));
            post.setBlocksName(postMapper.getBlockByPost(postId));
            post.setBlocksId(postMapper.getBlockIdByPost(postId));
            post.setCommentNum(commentMapper.getFirstCommentNum(postId));

            List<Label> labels = new ArrayList<>();
            for (int i = 0; i < post.getBlocksId().size(); i++){
                labels.add(new Label(new Block(post.getBlocksId().get(i),post.getBlocksName().get(i))));
            }
            post.setLabels(labels);
            List<PostSuggestion> postSuggestions = new ArrayList<>();
            for(Label i:post.getLabels()){
                List<Post> posts = postMapper.getPostByBlock(i.getValue(),selfId);
                for(Post p: posts){
                    List<Integer> list = postMapper.getBlockIdByPost(p.getId());
                    List<String> list1 = postMapper.getBlockByPost(p.getId());
                    List<Label> labelList = new ArrayList<>();
                    for(int j = 0; j < list.size(); j++){
                        labelList.add(new Label(new Block(list.get(j),list1.get(j))));
                    }
                    if(p.getId() != postId){
                        postSuggestions.add(new PostSuggestion(p.getId(),p.getTitle(),labelList));
                    }
                }
            }
            if(postSuggestions.size()>15){
                postSuggestions = postSuggestions.subList(0,15);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("post", post);
            map.put("postSuggestions",postSuggestions);
            return Result.success("获取成功", map);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updateCollect(HttpServletRequest request, int postId){
        try{
            int userId = (int) request.getAttribute("id");
            int i = postMapper.getCollectStatus(userId, postId) == null ? 0 : 1;
            if (i == 0) {
                postMapper.addCollect(userId, postId);
                noticeService.addLikeNotice("收藏",postMapper.getPostById(postId).getUserId(),userId,postId);
                return Result.success("收藏成功", null);
            } else {
                postMapper.delCollect(userId, postId);
                return Result.success("取消成功", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updatePostLike(HttpServletRequest request, int postId){
        try{
            int userId = (int) request.getAttribute("id");
            boolean i = userMapper.getLikeStatus(postId, userId, 0) == null;
            if (i) {
                userMapper.addLike(postId, userId, 0);
                postMapper.addLike(postId);
                noticeService.addLikeNotice("赞",postMapper.getPostById(postId).getUserId(),userId,postId);
                return Result.success("点赞成功", null);
            } else {
                userMapper.delLike(postId, userId, 0);
                postMapper.delLike(postId);
                return Result.success("取消成功", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updatePost(Post post, HttpServletRequest request){
        try{
            int selfId = (int) request.getAttribute("id");
            int userId = postMapper.getPostById(post.getId()).getUserId();
            if (selfId != userId) {
                return Result.error(300, "无法修改他人帖子", null);
            }
            List<Integer> labels = post.getBlocksId();
            postMapper.delAllBelongByPost(post.getId());
            for(int i:labels){
                postMapper.addBelong(post.getId(),i);
            }
            boolean flag = false;
            Post p = postMapper.getPostById(post.getId());
            if(p.getViewStatus() != 1 || p.getPostStatus() != 1){
                flag = true;
            }
            postMapper.updatePost(post);
            if(post.getPostStatus() == 1 && post.getViewStatus() == 1 && flag){
                noticeService.addSystemNotice("","",selfId,post.getId());
            }
            return Result.success("修改成功", null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public EditorResp addPhoto(MultipartFile photo){
        String photoName = photo.getOriginalFilename();
        String suffix = photoName.substring(photoName.lastIndexOf("."));
        String saveName = UUIDUtil.get();
        File dest = new File(uploadPath + '/' + saveName + suffix);
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }
        try {
            photo.transferTo(dest);
        } catch (IOException e) {
            return EditorResp.error(e.getMessage());
        }
        return EditorResp.success(showUrl + '/' + saveName + suffix,photoName);
    }


    public Result getPostsByUser(HttpServletRequest request, Map<String, Object> map){
        int selfId = (int) request.getAttribute("id");
        int count = postMapper.getSelfPostNum(map);
        page.setPage(1, 10000, count);
        map.put("startIndex",page.getStartIndex());
        List<Post> list = postMapper.getSelfPosts(map);
        for(Post post:list){
            post.setBlocksName(postMapper.getBlockByPost(post.getId()));
            if(postMapper.getCollectStatus(selfId,post.getId()) != null){
                post.setCollectStatus(1);
            }
            if(userMapper.getLikeStatus(post.getId(),selfId,0) != null){
                post.setLikeStatus(1);
            }
            post.setCollectNum(postMapper.getCollectNumByPost(post.getId()));
            post.setCommentNum(commentMapper.getFirstCommentNum(post.getId()));
        }
        page.setList(list);
        return Result.success("获取成功", page);
    }

    public Result getCollectPostByUser(HttpServletRequest request, int userId){
        int selfId = (int) request.getAttribute("id");
        int count = postMapper.getCollectPostNum(userId);
        page.setPage(1, 10000, count);
        List<Post> list = postMapper.getCollectPost(userId, page.getStartIndex());
        for(Post post:list){
            post.setBlocksName(postMapper.getBlockByPost(post.getId()));
            if(postMapper.getCollectStatus(selfId,post.getId()) != null){
                post.setCollectStatus(1);
            }
            if(userMapper.getLikeStatus(post.getId(),selfId,0) != null){
                post.setLikeStatus(1);
            }
            post.setCollectNum(postMapper.getCollectNumByPost(post.getId()));
            post.setCommentNum(commentMapper.getFirstCommentNum(post.getId()));
        }
        page.setList(list);
        return Result.success("获取成功", page);
    }

    public Result delPost(HttpServletRequest request, int postId){
        try{
            int selfId = (int) request.getAttribute("id");
            if (postMapper.getPostById(postId).getUserId() == selfId) {
                postMapper.delPostById(postId);
                return Result.success("删除成功", null);
            }
            return Result.error(300, "删除失败", null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result delPostByAdmin(Map<String, Object> map){
        try {
            int postId = (int) map.get("postId");
            String text = (String) map.get("text");
            noticeService.addSystemNoticeByadmin("帖子删除通知",text,postId);
            postMapper.delPostById(postId);
            return Result.success("删除成功", null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    /**
     * @param map postId 审核的帖子 reviewStatus 0成功，1失败 text 失败原因
     */
    @Transactional(rollbackFor = Exception.class)
    public Result reviewPostsByAdmin(Map<String,Object> map) {
        int postId =(int) map.get("postId");
        int reviewStatus = (int) map.get("reviewStatus");
        String text = (String) map.get("text");
        int postStatus = postMapper.getPostById(postId).getPostStatus();
        if (postStatus != 2) {
            return Result.error(300, "帖子类型错误" + postStatus, null);
        }
        if (reviewStatus == 0) {
            Post post = new Post();
            post.setId(postId);
            post.setPostStatus(1);
            postMapper.updatePostByAdmin(post);
            noticeService.addSystemNoticeByadmin("帖子发布成功", null, postId);
            noticeService.addSystemNotice("","",postMapper.getPostById(postId).getUserId(),post.getId());
            return Result.success("帖子发布成功", null);
        } else {
            postMapper.setReview(postId);
            noticeService.addSystemNoticeByadmin("帖子审核失败", text, postId);
            return Result.success("帖子驳回成功", null);
        }
    }

    public Result getReviewPosts(){
        List<Post> posts = postMapper.getReviewPosts();
        List<PostInfo> postInfos = new ArrayList<>();
        for(Post post:posts) {
            post.setBlocksName(postMapper.getBlockByPost(post.getId()));
            post.setBlocksId(postMapper.getBlockIdByPost(post.getId()));
            List<Label> labels = new ArrayList<>();
            for (int i = 0; i < post.getBlocksId().size(); i++) {
                labels.add(new Label(new Block(post.getBlocksId().get(i), post.getBlocksName().get(i))));
            }
            post.setLabels(labels);
            User user = userMapper.getUserById(post.getUserId());
            postInfos.add(new PostInfo(post,user));
        }
        return Result.success("获取成功",postInfos);
    }

    public Result getReviewPostById(int postId){
        Post post = postMapper.getPostById(postId);
        post.setBlocksName(postMapper.getBlockByPost(post.getId()));
        post.setBlocksId(postMapper.getBlockIdByPost(post.getId()));
        List<Label> labels = new ArrayList<>();
        for (int i = 0; i < post.getBlocksId().size(); i++){
            labels.add(new Label(new Block(post.getBlocksId().get(i),post.getBlocksName().get(i))));
        }
        post.setLabels(labels);
        User user = userMapper.getUserById(post.getUserId());
        Map<String, Object> map = new HashMap();
        map.put("post",post);
        map.put("user",user);
        return Result.success("获取待审核帖子详情成功",map);
    }

    public Result searchPostByAdmin(Map<String, Object> map){
        int postStatus = (int) map.get("postStatus");
        int viewStatus = (int) map.get("viewStatus");
        String text = (String) map.get("text");
        List<Post> posts = postMapper.searchPostByAdmin(text,viewStatus,postStatus);
        List<PostInfo> postInfos = new ArrayList<>();
        for(Post post:posts){
            post.setBlocksName(postMapper.getBlockByPost(post.getId()));
            post.setBlocksId(postMapper.getBlockIdByPost(post.getId()));
            List<Label> labels = new ArrayList<>();
            for (int i = 0; i < post.getBlocksId().size(); i++){
                labels.add(new Label(new Block(post.getBlocksId().get(i),post.getBlocksName().get(i))));
            }
            post.setLabels(labels);
            postInfos.add(new PostInfo(post,userMapper.getUserById(post.getUserId())));
        }
        return Result.success("搜索成功",postInfos);
    }


}

@Component
@Data
class PostInfo{
    Post post;
    User user;

    public PostInfo(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
