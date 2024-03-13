package com.bluemsun.know.mapper;

import com.bluemsun.know.entity.Block;
import com.bluemsun.know.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    boolean insertPost(Post post);

    List<Post> getSelfPosts(Map<String, Object> map);

    int updatePost(Post post);

    List<Post> getCollectPost(int userId, int startIndex);

    int getCollectPostNum(int userId);

    int getSelfPostNum(Map<String, Object> map);

    List<String> getBlockByPost(int postId);

    boolean addBelong(int postId, int blockId);

    boolean delBelong(int postId, int blockId);

    boolean delAllBelongByPost(int postId);

    List<Integer> getBlockIdByPost(int postId);

    Post getPostById(int id);

    int addScan(int id);

    int addCollect(int userId, int postId);

    int delCollect(int userId, int postId);

    Post getCollectStatus(int userId, int postId);

    int addLike(int postId);

    int delLike(int postId);

    int getCollectNumByPost(int postId);

    List<Post> getPostByBlock(int blockId, int userId);

    int delPostById(int postId);

    List<Post> getPostsByHot();

    List<Post> getPostsByBlockHot(int blockId);

    List<Post> searchPostByHot(String text);

    List<Post> searchPostByTime(String text,int userId);

    List<Post> getReviewPosts();

    int updatePostByAdmin(Post post);

    List<Post> searchPostByAdmin(String text, int viewStatus, int postStatus);

    int setReview(int postId);
}
