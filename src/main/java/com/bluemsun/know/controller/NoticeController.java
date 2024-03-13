package com.bluemsun.know.controller;

import com.bluemsun.know.service.NoticeService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @RequestMapping("/numbers")
    public Result getNoticeNum(HttpServletRequest request){
        return noticeService.getNoticeNum(request);
    }

    @RequestMapping("/get/{noticeType}")
    public Result getNoticeByType(HttpServletRequest request, @PathVariable int noticeType){
        return noticeService.getNoticeByType(request,noticeType);
    }

    @RequestMapping("/del/{noticeId}")
    public Result delNotice(HttpServletRequest request,@PathVariable int noticeId){
        return noticeService.delNotice(request,noticeId);
    }
}
