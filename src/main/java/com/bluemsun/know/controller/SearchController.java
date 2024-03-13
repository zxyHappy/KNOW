package com.bluemsun.know.controller;


import com.bluemsun.know.service.SearchService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = {"/getkey"})
    public Result getKeys(@RequestBody Map<String,Object> map){
        String text = (String) map.get("text");
         return searchService.getkey(text);
    }

    @RequestMapping("/main")
    public Result getSearch(HttpServletRequest request, @RequestBody Map<String,Object> map){
        return searchService.getSearch(request,map);
    }
}
