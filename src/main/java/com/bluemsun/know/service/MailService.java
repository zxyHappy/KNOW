package com.bluemsun.know.service;

import com.bluemsun.know.mapper.UserMapper;
import com.bluemsun.know.util.RedisUtil;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;//一定要用@Autowired


    //application.properties中已配置的值
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 给前端输入的邮箱，发送验证码
     * @param email
     * @return
     */
    public Result sendMimeMail(String email) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setSubject("验证码邮件");//主题
            //生成随机数
            String code = randomCode();

            Jedis jedis = RedisUtil.getJedis();
            assert jedis != null;
            jedis.set(email+"_code",code);
            jedis.expire(email+"_code",60*60*2);

            mailMessage.setText("您收到的验证码是："+code);//内容

            mailMessage.setTo(email);//发给谁

            mailMessage.setFrom(from);//自己的邮箱

            mailSender.send(mailMessage);//发送

            RedisUtil.closeJedis(jedis);
            return  Result.success("发送成功",null);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(300,"发送失败",null);
        }
    }

    /**
     * 随机生成6位数的验证码
     * @return String code
     */
    public String randomCode(){
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }


}
