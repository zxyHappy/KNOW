package com.bluemsun.know.config;


import com.bluemsun.know.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Value("${file.upload.url}")
    private String uploadPath;

    @Value("${file.path}")
    private String showPath;

    @Autowired
    LoginInterceptor loginInterceptor;

    // 静态资源展示
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // PS：注意文件路径最后的斜杠（文件分隔符），如果缺少了，就不能正确的映射到相应的目录
        registry.addResourceHandler(showPath).addResourceLocations("file:///" + uploadPath+"/");
//        super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截处理操作的匹配路径
        //放开静态拦截
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") //拦截所有路径
                .excludePathPatterns("/user/login", "/user/register","/user/sendMail") //排除路径
                .excludePathPatterns("/show/**")
                .excludePathPatterns("/search/getkey/**");

    }

}
