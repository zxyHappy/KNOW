package com.bluemsun.know.controller;


import com.bluemsun.know.entity.User;
import com.bluemsun.know.service.MailService;
import com.bluemsun.know.service.UserService;
import com.bluemsun.know.util.Result;
import org.apache.catalina.startup.HostRuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
@ResponseBody
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;

    @RequestMapping("/sendMail")
    public Result sendEmail(@RequestBody Map<String,String> map){
        return mailService.sendMimeMail(map.get("email"));
    }


    @RequestMapping(value="/register")
    public Result register(@RequestBody User user){
        try{
            String msg = userService.register(user);
            if ("邮箱已被注册，请重新输入".equals(msg) || "验证码错误".equals(msg) || "填写有误".equals(msg)){
                return Result.error(300,msg,null);
            }
            return Result.success(msg, null);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @RequestMapping(value = "/login")
    public Result login(@RequestBody User user){
        try{
            String token = userService.userLogin(user);
            if ("请输入密码".equals(token)) {
                return Result.error(300, token, null);
            } else if ("登录失败".equals(token) || "登录失败,账号或密码有误".equals(token)) {
                return Result.error(250, "登录失败", token);
            } else if("无此用户".equals(token)){
                return Result.error(300,token,null);
            }else if("zxy".equals(user.getUserName())){
                return Result.error(111,"管理员登录",token);
            } else {
                return Result.success("登录成功", token);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    @RequestMapping("/addPhoto")
    public Result addPhoto(MultipartFile photo){
        String url = userService.addPhoto(photo);
        return Result.success("图片上传成功",url);
    }

    @RequestMapping("/update")
    public Result updateUser(@RequestBody User user, HttpServletRequest request){
        return userService.updateUser(user,request);
    }

    @RequestMapping("/showSelf")
    public Result showSelf(HttpServletRequest request){
        return userService.showUser(request);
    }

    @RequestMapping("/getFans")
    public Result getFans(HttpServletRequest request){
        return userService.getFans(request);
    }

    @RequestMapping("/updateFollow/{followedUserId}/{postId}")
    public Result updateFollow(HttpServletRequest request, @PathVariable int followedUserId, @PathVariable int postId){
        return userService.updateFollow(request,followedUserId,postId);
    }

    @RequestMapping("/getSelfFollow")
    public Result getSelfFollow(HttpServletRequest request){
        int userId = (int) request.getAttribute("id");
        return userService.getFollows(userId);
    }

    @RequestMapping("/updateBlack/{ignoredUserId}")
    public Result updateBlack(HttpServletRequest request,@PathVariable int ignoredUserId){
        return userService.updateBlack(request,ignoredUserId);
    }

    @RequestMapping("/showBlack")
    public Result showBlack(HttpServletRequest request){
        return userService.showBlack(request);
    }

    @PostMapping("/updatePwd")
    public Result updatePwd(HttpServletRequest request, @RequestBody Map<String, String> map){
        return userService.updatePwd(request,map);
    }

    @RequestMapping("/show/{userId}")
    public Result showUserMessage(HttpServletRequest request, @PathVariable int userId){
        return userService.showUserMessage(request,userId);
    }

    @RequestMapping("/fans/{userId}")
    public Result showFansMessage(HttpServletRequest request,@PathVariable int userId){
        return userService.getFansMessage(request,userId);
    }

    @RequestMapping("/follow/{userId}")
    public Result showFollowedMessage(HttpServletRequest request, @PathVariable int userId){
        return userService.getFollowsMessage(request,userId);
    }
}
