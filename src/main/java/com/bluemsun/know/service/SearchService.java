package com.bluemsun.know.service;

import com.bluemsun.know.entity.*;
import com.bluemsun.know.mapper.KeyMapper;
import com.bluemsun.know.mapper.PostMapper;
import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.GsonUtil;
import com.bluemsun.know.util.RedisUtil;
import com.bluemsun.know.util.Result;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SearchService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    PostMapper postMapper;
    @Autowired
    KeyMapper keyMapper;

    @Scheduled(fixedRate = 5000)
    public void getKeys(){
        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        jedis.select(3);
        jedis.del("keys");
        List<SearchKey> searchKeys = keyMapper.getKeys();
        for(SearchKey searchKey:searchKeys){
            jedis.zadd("keys",searchKey.getNum(), GsonUtil.toJson(searchKey));
        }
        RedisUtil.closeJedis(jedis);
    }

    public Result getkey(String text){
        Jedis jedis = RedisUtil.getJedis();
        try{
            assert jedis != null;
            jedis.select(3);

            if ("".equals(text) || text == null) {
                List<Tuple> sortedKeys = jedis.zrevrangeWithScores("keys", 0, 15);
                List<KeyInfo> keys = new ArrayList<>();
                for (Tuple tuple : sortedKeys) {
                    SearchKey key = GsonUtil.fromJson(tuple.getElement(),SearchKey.class);
                    keys.add(new KeyInfo(key.getKeyText(),tuple.getScore()));
                }
                RedisUtil.closeJedis(jedis);
                return Result.success("获取成功", keys);
            } else {
                List<Tuple> sortedKeys = jedis.zrevrangeWithScores("keys", 0, -1);
                List<KeyInfo> keys = new ArrayList<>();
                for (Tuple tuple : sortedKeys) {
                    SearchKey key = GsonUtil.fromJson(tuple.getElement(),SearchKey.class);
                    if (key.getKeyText().contains(text) || text.contains(key.getKeyText())) {
                        keys.add(new KeyInfo(key.getKeyText(),tuple.getScore()));
                    }
                }
                RedisUtil.closeJedis(jedis);
                return Result.success("获取成功", keys);
            }
        }catch (Exception e){
            RedisUtil.closeJedis(jedis);
            return Result.error(250,e.getMessage(),null);
        }
    }

    /**
     * @param map text 搜索内容  sortType 0代表点赞量，1代表时间 searchType 0代表帖子，1代表用户
     */
    public Result getSearch(HttpServletRequest request, Map<String,Object> map){
        String text = (String) map.get("text");
        int sortType = (int) map.get("sortType");
        int searchType = (int) map.get("searchType");
        int userId = (int) request.getAttribute("id");
        try{
            SearchKey searchKey = keyMapper.getKeyByText(text);
            if(searchKey != null){
                keyMapper.updateKey(searchKey.getId());
            }else {
                keyMapper.addKey(new SearchKey(text,1));
            }
            if(searchType == 0){
                List<Post> posts = null;
                if(sortType == 0){
                    posts = postMapper.searchPostByHot(text);
                }else {
                    posts = postMapper.searchPostByTime(text,userId);
                }
                for(Post post:posts){
                    post.setCollectNum(postMapper.getCollectNumByPost(post.getId()));
                    if(postMapper.getCollectStatus(userId,post.getId()) != null){
                        post.setCollectStatus(1);
                    }
                    if(userMapper.getLikeStatus(post.getId(),userId,0) != null){
                        post.setLikeStatus(1);
                    }
                    post.setBlocksId(postMapper.getBlockIdByPost(post.getId()));
                    post.setBlocksName(postMapper.getBlockByPost(post.getId()));
                    List<Label> labels = new ArrayList<>();
                    for(int i = 0; i < post.getBlocksId().size(); i++){
                        labels.add(new Label(new Block(post.getBlocksId().get(i),post.getBlocksName().get(i))));
                    }
                    post.setLabels(labels);
                }
                return Result.success("搜索帖子成功",posts);
            }else {
                List<User> users = userMapper.searchUser(text,userId);
                for(User user:users){
                    if(userMapper.getFollowStatus(user.getId(),userId) != null){
                        user.setFollowedStatus(1);
                    }
                    user.setFansNum(userMapper.getFansNum(user.getId()));
                    user.setFollowNum(userMapper.getFollowNum(user.getId()));
                }
                return Result.success("搜索用户成功",users);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }


}

@Data
@Component
class KeyInfo {
    private String key;
    private double num;

    public KeyInfo() {
    }

    public KeyInfo(String key, double num) {
        this.key = key;
        this.num = num;
    }
}
