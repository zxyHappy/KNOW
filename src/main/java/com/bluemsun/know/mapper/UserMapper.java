package com.bluemsun.know.mapper;


import com.bluemsun.know.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    List<User> getAllUser();

    boolean insertUser(User user);

    User getUserByUserName(String userName);

    User getUserByEmail(String email);

    User getUserById(int id);

    int updateUser(User user);

    int addFollow(int followedUserId, int userId);

    int deleteFollow(int followedUserId, int userId);

    User getFollowStatus(int followedUserId, int userId);

    int getFollowNum(int userId);

    List<User> getFans(int userId, int startIndex);

    int getFansNum(int userId);

    List<User> getFollow(int userId, int startIndex);

    List<User> getBlackPage(int userId, int startIndex);

    int getBlackNum(int uerId);

    int addBlack(int userId, int ignoredUserId);

    int deleteBlack(int userId, int ignoredUserId);

    int getBlackStatus(int userId, int ignoredUserId);

    int addLike(int likedId, int userId, int likeType);

    int delLike(int likedId, int userId, int likeType);

    User getLikeStatus(int likedId, int userId, int likeType);

    List<User> searchUser(String text, int userId);

    int banUser(int userId);

    int cancelBan(int userId);

    int getBanStatus(int userId);

    /**
     *
     * @param typeId 0未封禁，1封禁
     */
    List<User> getUserByType(int typeId);

    List<User> searchUserByAdmin(String userName);

}
