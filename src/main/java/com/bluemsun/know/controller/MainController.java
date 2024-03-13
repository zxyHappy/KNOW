package com.bluemsun.know.controller;

import com.bluemsun.know.service.MailService;
import com.bluemsun.know.service.MainService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/main")
public class MainController {

    @Autowired
    MainService mainService;

    @RequestMapping("/show")
    public Result show(HttpServletRequest request){
        return mainService.getMain(request);
    }

    @RequestMapping("/post/{blockId}")
    public Result getPosts(HttpServletRequest request, @PathVariable int blockId){
        return mainService.getMainPostsByBlock(request,blockId);
    }

    @RequestMapping("/navigation")
    public Result getNavigation(HttpServletRequest request){
        return mainService.getNavigation(request);
    }
}
