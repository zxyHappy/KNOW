package com.bluemsun.know.service;

import com.bluemsun.know.entity.Notice;
import com.bluemsun.know.entity.User;
import com.bluemsun.know.mapper.NoticeMapper;
import com.bluemsun.know.mapper.PostMapper;
import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class NoticeService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    NoticeMapper noticeMapper;


    public int getAllNotReadNotice(int userId){
        return noticeMapper.getNoticeNoReadNum(userId,-1);
    }

    /**
     *
     * @param comment 内容 评论内容
     * @param userId 发给谁
     * @param relatedUserId 通知里相关的用户 xx回复了你
     * @param relatedPostId 相关的帖子
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCommentNotice(String comment,int userId, int relatedUserId, int relatedPostId){
        Notice notice = new Notice(relatedUserId,relatedPostId,1,comment);
        noticeMapper.addNotice(notice);
        noticeMapper.addRelation(notice.getId(),userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addFollowNotice(int userId, int relatedUserId, int relatedPostId){
        String text;
        if(relatedPostId == -1 || relatedPostId == 0 || postMapper.getPostById(relatedPostId) == null){
            text = "你的主页";
        }else {
            text = postMapper.getPostById(relatedPostId).getTitle();
        }
        Notice notice = new Notice(relatedUserId,relatedPostId,0,text);
        noticeMapper.addNotice(notice);
        noticeMapper.addRelation(notice.getId(),userId);
    }

    /**
     *
     * @param text 赞/收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public void addLikeNotice(String text,int userId, int relatedUserId, int relatedPostId){
        Notice notice = new Notice(relatedUserId,relatedPostId,2,text);
        noticeMapper.addNotice(notice);
        noticeMapper.addRelation(notice.getId(),userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSystemNotice(String title, String text, int relatedUserId, int relatedPostId){
        if(relatedUserId != -1){
            title = "您的关注：@"+userMapper.getUserById(relatedUserId).getNickName()+"发布新帖子："+postMapper.getPostById(relatedPostId).getTitle();
            text = postMapper.getPostById(relatedPostId).getBody();
        }
        Notice notice = new Notice(relatedUserId,relatedPostId,3,title,text);
        noticeMapper.addNotice(notice);
        if(relatedUserId != -1){
            List<User> fans = userMapper.getFans(relatedUserId,0);
            if(fans != null && !fans.isEmpty()){
                for(User u:fans){
                    noticeMapper.addRelation(notice.getId(),u.getId());
                }
            }
        }else {
            List<User> users = userMapper.getAllUser();
            for(User u: users){
                noticeMapper.addRelation(notice.getId(),u.getId());
            }
        }
    }


    public Result getNoticeNum(HttpServletRequest request){
        try {
            int userId = (int) request.getAttribute("id");
            Map<String, Object> map = new HashMap<>();
            map.put("comment", noticeMapper.getNoticeNoReadNum(userId, 1));
            map.put("fans", noticeMapper.getNoticeNoReadNum(userId, 0));
            map.put("like", noticeMapper.getNoticeNoReadNum(userId, 2));
            map.put("system", noticeMapper.getNoticeNoReadNum(userId, 3));
            return Result.success("获取成功", map);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getNoticeByType(HttpServletRequest request, int noticeType){
        int userId = (int) request.getAttribute("id");
        try{
            switch (noticeType) {
                case 0:
                    List<Notice> notices0 = noticeMapper.getNoticeByType(userId, 0);
                    noticeMapper.setRead(0,userId);
                    for (Notice n : notices0) {
                        n.setRelatedUser(userMapper.getUserById(n.getRelatedUserId()));
                        if(userMapper.getFollowStatus(n.getRelatedUserId(),userId) != null){
                            User user = n.getRelatedUser();
                            user.setFollowedStatus(1);
                            n.setRelatedUser(user);
                        }
                        if (n.getRelatedPostId() != -1 && n.getRelatedPostId() != 0) {
                            n.setRelatedPost(postMapper.getPostById(n.getRelatedPostId()));
                        }
                    }
                    return Result.success("获取消息成功", notices0);
                case 1:
                    List<Notice> notices1 = noticeMapper.getNoticeByType(userId, 1);
                    noticeMapper.setRead(1,userId);
                    for (Notice n : notices1) {
                        n.setRelatedPost(postMapper.getPostById(n.getRelatedPostId()));
                        n.setRelatedUser(userMapper.getUserById(n.getRelatedUserId()));
                    }
                    return Result.success("获取消息成功", notices1);
                case 2:
                    List<Notice> notices2 = noticeMapper.getNoticeByType(userId, 2);
                    noticeMapper.setRead(2,userId);
                    for (Notice n : notices2) {
                        n.setRelatedUser(userMapper.getUserById(n.getRelatedUserId()));
                        n.setRelatedPost(postMapper.getPostById(n.getRelatedPostId()));
                    }
                    return Result.success("获取消息成功", notices2);
                case 3:
                    List<Notice> notices3 = noticeMapper.getNoticeByType(userId, 3);
                    noticeMapper.setRead(3,userId);
                    for (Notice n : notices3) {
                        if (n.getRelatedUserId() != 0 && n.getRelatedUserId() != -1) {
                            n.setRelatedUser(userMapper.getUserById(n.getRelatedUserId()));
                        }
                        if(n.getRelatedPostId() != 0 && n.getRelatedPostId() != -1){
                            n.setRelatedPost(postMapper.getPostById(n.getRelatedPostId()));
                        }
                    }
                    return Result.success("获取消息成功", notices3);
                default:
                    return Result.error(300, "类型错误", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result delNotice(HttpServletRequest request, int noticeId){
        try{
            int userId = (int) request.getAttribute("id");
            noticeMapper.delNotice(noticeId, userId);
            return Result.success("删除成功", null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSystemNoticeByadmin(String title, String text, int relatedPostId){
        switch (title){
            case "帖子删除通知":
                text = "您的帖子《"+postMapper.getPostById(relatedPostId).getTitle()+"》因"+text+"已被管理员删除，若要重新发布，请修改相关内容";
                int userId1 = postMapper.getPostById(relatedPostId).getUserId();
                Notice notice1 = new Notice(-1,relatedPostId,3,title,text);
                noticeMapper.addNotice(notice1);
                noticeMapper.addRelation(notice1.getId(),userId1);
                break;
            case "帖子发布成功":
                text = "您的帖子《"+postMapper.getPostById(relatedPostId).getTitle()+"》已通过管理员审核，现已成功发布";
                int userId2 = postMapper.getPostById(relatedPostId).getUserId();
                Notice notice2 = new Notice(-1,relatedPostId,3,title,text);
                noticeMapper.addNotice(notice2);
                noticeMapper.addRelation(notice2.getId(),userId2);
                break;
            case "帖子审核失败":
                text = "您的帖子《"+postMapper.getPostById(relatedPostId).getTitle()+"》因"+text+"未通过管理员审核，请修改后重试";
                int userId3 = postMapper.getPostById(relatedPostId).getUserId();
                Notice notice3 = new Notice(-1,relatedPostId,3,title,text);
                noticeMapper.addNotice(notice3);
                noticeMapper.addRelation(notice3.getId(),userId3);
                break;
            default:break;
        }
    }
}
