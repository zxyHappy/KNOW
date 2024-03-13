package com.bluemsun.know.service;

import com.bluemsun.know.entity.Page;
import com.bluemsun.know.entity.User;
import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service
public class UserService{

    @Value("${file.upload.url}")
    private String uploadPath;

    @Value("${file.show.url}")
    private String showUrl;

    @Autowired
    UserMapper userMapper;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    Page<User> page;

    @Autowired
    NoticeService noticeService;

    public String register(User user) {

        if(user.getNickName() == null || user.getNickName().contains(" ")){
            return "填写有误";
        }

        if(userMapper.getUserByEmail(user.getEmail()) != null){
            return "邮箱已被注册，请重新输入";
        }

        Jedis jedis = RedisUtil.getJedis();
        assert jedis != null;
        String codeTrue = jedis.get(user.getEmail()+"_code");
        if(user.getCode() == null || !user.getCode().equals(codeTrue)){
            return "验证码错误";
        }
        user.setPassword(MD5Utils.MD5(user.getPassword()));

        StringBuilder userName = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int var = new Random().nextInt(10);
            userName.append(var);
        }
        while(userMapper.getUserByUserName(userName.toString()) != null) {
            userName = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int var = new Random().nextInt(10);
                userName.append(var);
            }
        }
        user.setUserName(userName.toString());
        boolean code = userMapper.insertUser(user);
        System.out.println(code);
        return "注册成功";
    }



    public String userLogin(User user) {
        String password = MD5Utils.MD5(user.getPassword());
        if(password == null){
            return "请输入密码";
        }
        if(user.getUserName() != null){
//            User userReal = userMapper.getUserByEmail(user.getEmail());
            User userReal = null;
            if (userMapper.getUserByEmail(user.getUserName()) != null){
                userReal = userMapper.getUserByEmail(user.getUserName());
            } else if(userMapper.getUserByUserName(user.getUserName()) != null) {
                userReal = userMapper.getUserByUserName(user.getUserName());
            } else {
                return "无此用户";
            }
            if(password.equals(userReal.getPassword())){
                if(userReal.getBanStatus() == 1){
                    return "该账号已被封禁";
                }
                return jwtUtil.createToken(String.valueOf(userReal.getId()),userReal.getUserName());
            }
//        } else if(user.getUserName() != null){
//            User userReal = userMapper.getUserByUserName(user.getUserName());
//            if(password.equals(userReal.getPassword())){
//                return jwtUtil.createToken(String.valueOf(userReal.getId()),userReal.getUserName());
//            }
        }else {
            return "登录失败";
        }
        return "登录失败,账号或密码有误";
    }

    public String addPhoto(MultipartFile photo){
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
            return e.getMessage();
        }
        return showUrl + '/' + saveName + suffix;
    }

    public Result updateUser(User user, HttpServletRequest request){
        try{
            int id = (int) request.getAttribute("id");
            if(userMapper.getUserByEmail(user.getEmail()) != null){
                if(!userMapper.getUserById(id).getEmail().equals(user.getEmail())){
                    return Result.error(300, "邮箱已被占用", null);
                }
            }
            user.setId(id);
            user.setPassword(MD5Utils.MD5(user.getPassword()));
            int i = userMapper.updateUser(user);
            if (i == 1) {
                return Result.success("修改成功", null);
            } else {
                return Result.error(250, "修改失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result showUser(HttpServletRequest request){
        try{
            int id = (int) request.getAttribute("id");
            User user = userMapper.getUserById(id);
            int fansNum = userMapper.getFansNum(id);
            int followNum = userMapper.getFollowNum(id);
            user.setFansNum(fansNum);
            user.setFollowNum(followNum);
            return Result.success("获取用户信息成功", user);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getFans(HttpServletRequest request){
        try {
            int userId = (int) request.getAttribute("id");
            int fansNum = userMapper.getFansNum(userId);
            page.setPage(1, 10000, fansNum);
            List<User> list = userMapper.getFans(userId, page.getStartIndex());
            for(User u:list){
                if(userMapper.getFollowStatus(u.getId(),userId) != null){
                    u.setFollowedStatus(1);
                }else {
                    u.setFollowedStatus(0);
                }
            }
            page.setList(list);
            return Result.success("获取粉丝成功", page);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Result updateFollow(HttpServletRequest request, int followedUserId, int postId){
        try {
            int userId = (int) request.getAttribute("id");
            User user = userMapper.getFollowStatus(followedUserId, userId);
            if (user == null){
                int i = userMapper.addFollow(followedUserId, userId);
                if (i == 1) {
                    noticeService.addFollowNotice(followedUserId,userId,postId);
                    return Result.success("关注成功", null);
                } else {
                    return Result.error(300, "关注失败", null);
                }
            }else {
                int i = userMapper.deleteFollow(followedUserId,userId);
                if(i == 1){
                    return Result.success("取关成功",null);
                }else {
                    return Result.error(300,"取关失败",null);
                }
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getFollows(int userId){
        try{
            int count = userMapper.getFollowNum(userId);
            page.setPage(1, 10000, count);
            List<User> list = userMapper.getFollow(userId, page.getStartIndex());
            for(User u:list){
                u.setFollowedStatus(1);
            }
            page.setList(list);
            return Result.success("获取成功", page);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result updateBlack(HttpServletRequest request, int ignoredUserId){
        try{
            int userId = (int) request.getAttribute("id");
            if(userId == ignoredUserId){
                return Result.error(300,"无法拉黑自己",null);
            }
            int count = userMapper.getBlackStatus(userId, ignoredUserId);
            if (count == 0) {
                int i = userMapper.addBlack(userId, ignoredUserId);
                if (i == 1) {
                    return Result.success("拉黑成功", null);
                } else {
                    return Result.error(300, "拉黑失败", null);
                }
            } else {
                int i = userMapper.deleteBlack(userId, ignoredUserId);
                if (i == 1) {
                    return Result.success("取消成功", null);
                } else {
                    return Result.error(300, "取消失败", null);
                }
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result showBlack(HttpServletRequest request){
        try{
            int userId = (int) request.getAttribute("id");
            int count = userMapper.getBlackNum(userId);
            page.setPage(1, 10000, count);
            List<User> list = userMapper.getBlackPage(userId, page.getStartIndex());
            for (User u : list) {
                u.setBlackedStatus(1);
            }
            page.setList(list);
            return Result.success("获取成功", page);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result updatePwd(HttpServletRequest request,Map<String, String> map){
        try {
            String pwd1 = map.get("pwd");
//            String pwd2 = map.get("pwd2");
            pwd1 = MD5Utils.MD5(pwd1);
//            pwd2 = MD5Utils.MD5(pwd2);
            int userId = (int) request.getAttribute("id");
            User user = userMapper.getUserById(userId);
            if (pwd1 != null && pwd1.equals(user.getPassword())) {
//                user.setPassword(pwd2);
//                userMapper.updateUser(user);
                return Result.success("校验成功", null);
            } else {
                return Result.error(300, "校验失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result showUserMessage(HttpServletRequest request, int userId){
        try{
            int selfId = (int) request.getAttribute("id");
            User user = userMapper.getUserById(userId);
            user.setFansNum(userMapper.getFansNum(userId));
            user.setFollowNum(userMapper.getFollowNum(userId));
            if (userMapper.getFollowStatus(userId, selfId) != null) {
                user.setFollowedStatus(1);
            }
            if(userMapper.getBlackStatus(selfId,userId) == 1){
                user.setBlackedStatus(1);
            }
            return Result.success("获取用户信息成功", user);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getFansMessage(HttpServletRequest request, int userId){
        int selfId = (int) request.getAttribute("id");
        int fansNum = userMapper.getFansNum(userId);
        page.setPage(1, 10000, fansNum);
        List<User> list = userMapper.getFans(userId, page.getStartIndex());
        for(User u:list){
            if(userMapper.getFollowStatus(u.getId(),selfId) != null){
                u.setFollowedStatus(1);
            }else {
                u.setFollowedStatus(0);
            }
        }
        page.setList(list);
        return Result.success("获取粉丝成功", page);
    }


    public Result getFollowsMessage(HttpServletRequest request, int userId){
        int selfId = (int) request.getAttribute("id");
        int count = userMapper.getFollowNum(userId);
        page.setPage(1, 10000, count);
        List<User> list = userMapper.getFollow(userId, page.getStartIndex());
        for(User u:list){
            if(userMapper.getFollowStatus(u.getId(),selfId) != null){
                u.setFollowedStatus(1);
            }
        }
        page.setList(list);
        return Result.success("获取成功", page);
    }

    public Result updateBanUser(int userId){
        try {
            int banStatus = userMapper.getBanStatus(userId);
            if (banStatus == 1) {
                userMapper.cancelBan(userId);
                return Result.success("解封成功", null);
            } else {
                userMapper.banUser(userId);
                return Result.success("封禁成功", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getUserByType(int userType){
        try{
            List<User> users = userMapper.getUserByType(userType);
            if(userType == 0){
                for(User u:users){
                    u.setBanStatus(0);
                }
            }else {
                for(User u:users){
                    u.setBanStatus(1);
                }
            }
            return Result.success("获取用户成功",users);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }


    public Result searchUserByAdmin(String userName){
        try{
            List<User> users = userMapper.searchUserByAdmin(userName);
            for(User u:users){
                u.setBanStatus(userMapper.getBanStatus(u.getId()));
            }
            return Result.success("搜索成功",users);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getAllUserByAdmin(){
        try{
            List<User> users = userMapper.getAllUser();
            for (User user : users) {
                user.setBanStatus(userMapper.getBanStatus(user.getId()));
            }
            return Result.success("获取全部用户成功", users);
        }catch (Exception e){
            return Result.error(250, e.getMessage(),null);
        }
    }


}
