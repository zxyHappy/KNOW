package com.bluemsun.know.interceptor;

import com.bluemsun.know.util.JWTUtil;
import com.bluemsun.know.util.Result;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@ResponseBody
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || "".equals(token)) {
            if(request.getRequestURI().contains("/post/show") || request.getRequestURI().contains("/comment/show") || request.getRequestURI().contains("/main/show") || request.getRequestURI().contains("/search/main") || request.getRequestURI().contains("/main/post") || request.getRequestURI().contains("/main/navigation") ||  request.getRequestURI().contains("/block/get") || request.getRequestURI().contains("/user/show")){
                request.setAttribute("id",0);
                return true;
            }
            falseResult(response);
            return false;
        }
        int i = jwtUtil.verify(token);
        if (i == -1 || i == 0){
//            request.getRequestDispatcher("/user/login").forward(request,response);
            falseResult(response);
            return false;
        }
        if(request.getRequestURI().contains("/admin") && i != 25){
           falseResult(response);
           return false;
        }
        request.setAttribute("id",i);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    public void falseResult(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Gson gson = new Gson();
        String json = gson.toJson(Result.error(300,"token invalid","token invalid"));
        response.getWriter().println(json);
        return;
    }
}
