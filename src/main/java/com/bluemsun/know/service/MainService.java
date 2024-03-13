package com.bluemsun.know.service;

import com.bluemsun.know.entity.Block;
import com.bluemsun.know.entity.Label;
import com.bluemsun.know.entity.Post;
import com.bluemsun.know.entity.User;
import com.bluemsun.know.mapper.*;
import com.bluemsun.know.util.GsonUtil;
import com.bluemsun.know.util.JWTUtil;
import com.bluemsun.know.util.RedisUtil;
import com.bluemsun.know.util.Result;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Service
public class MainService {

    @Autowired
    BlockMapper blockMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    CommentMapper commentMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    NoticeService noticeService;


    private int userId = 1;

    @Scheduled(fixedRate = 3000)
    public void resortHot(){
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        try{
            if (userId == 1) {
                jedis.select(2);
            } else {
                jedis.select(1);
            }
            List<Block> blocks = blockMapper.getAllBlock();
            List<Label> labels = new ArrayList<>();
            jedis.del("labels");
            for (Block b : blocks) {
                labels.add(new Label(b));
                jedis.rpush("labels", GsonUtil.toJson(new Label(b)));
            }
            List<Post> hotPosts = postMapper.getPostsByHot();
            jedis.del("hotPosts");
            for (Post p : hotPosts) {
                int collectNum = postMapper.getCollectNumByPost(p.getId());
                p.setWeight((float) (p.getLikeNumber() * 0.3 + collectNum * 0.6 + p.getScanNumber() * 0.1));
                jedis.zadd("hotPosts", p.getWeight(), GsonUtil.toJson(p));
            }

            for (Label l : labels) {
                jedis.del("hotPosts_" + l.getValue());
                List<Post> posts = postMapper.getPostsByBlockHot(l.getValue());
                for (Post p : posts) {
                    p.setCollectNum(postMapper.getCollectNumByPost(p.getId()));
                    p.setCommentNum(commentMapper.getFirstCommentNum(p.getId()));
                    p.setWeight((float) (p.getLikeNumber() * 0.6 + p.getCollectNum() * 0.3 + p.getScanNumber() * 0.1));
                    jedis.zadd("hotPosts_" + l.getValue(), p.getWeight(), GsonUtil.toJson(p));
                }
            }
            if (userId == 1) {
                userId = 2;
            } else {
                userId = 1;
            }
            RedisUtil.closeJedis(jedis);
        }catch (Exception e){
            e.printStackTrace();
            RedisUtil.closeJedis(jedis);
        }
    }

    public Result getMain(HttpServletRequest request) {
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        try {
            Map<String, Object> map = new HashMap<>();
            jedis.select(userId);

            // 处理 labels
            List<String> labelJsonList = jedis.lrange("labels", 0, -1);
            List<Label> labels = new ArrayList<>();
            for (String labelJson : labelJsonList) {
                Label label = GsonUtil.fromJson(labelJson, Label.class);
                labels.add(label);
            }

            // 处理 hotPosts
            List<Tuple> hotPostsTupleList = jedis.zrevrangeWithScores("hotPosts", 0, 29);
            List<Post> hotPosts = new ArrayList<>();
            for (Tuple tuple : hotPostsTupleList) {
                Post post = GsonUtil.fromJson(tuple.getElement(), Post.class);
                post.setWeight((float) tuple.getScore());
                hotPosts.add(post);
            }

            RedisUtil.closeJedis(jedis);
            map.put("labels", labels);
            map.put("hotPosts", hotPosts);

            // 其他逻辑，例如获取用户的未读通知数量
            // int selfId = (int) request.getAttribute("id");
            // map.put("noticeNum", noticeService.getAllNotReadNotice(selfId));

            return Result.success("首页获取成功", map);
        } catch (Exception e) {
            RedisUtil.closeJedis(jedis);
            return Result.error(250, e.getMessage(), null);
        }
    }


    public Result getMainPostsByBlock(HttpServletRequest request, int blockId) {
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        try {
            int selfId = (int) request.getAttribute("id");
            jedis.select(userId);


            List<Tuple> hotPostsTupleList = jedis.zrevrangeWithScores("hotPosts_" + blockId, 0, -1);

            List<Post> hotPosts = new ArrayList<>();
            for (Tuple tuple : hotPostsTupleList) {
                Post post = GsonUtil.fromJson(tuple.getElement(), Post.class);
                post.setWeight((float) tuple.getScore()); // 设置分值信息


                if (userMapper.getLikeStatus(post.getId(), selfId, 0) != null) {
                    post.setLikeStatus(1);
                }
                if (postMapper.getCollectStatus(selfId, post.getId()) != null) {
                    post.setCollectStatus(1);
                }

                hotPosts.add(post);
            }
            RedisUtil.closeJedis(jedis);
            return Result.success("获取帖子成功", hotPosts);
        } catch (Exception e) {
            RedisUtil.closeJedis(jedis);
            return Result.error(250, e.getMessage(), null);
        }
    }


    public Result getNavigation(HttpServletRequest request){
        try{
            int selfId = (int) request.getAttribute("id");
            User user = userMapper.getUserById(selfId);
            Map<String, Object> map = new HashMap<>();
            map.put("nickName", user.getNickName());
            map.put("photo",user.getPhoto());
            map.put("noticeNum", noticeService.getAllNotReadNotice(selfId));
            return Result.success("获取成功", map);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

}
