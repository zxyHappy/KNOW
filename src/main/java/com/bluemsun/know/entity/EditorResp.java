package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class EditorResp {
    private int errno;
    private Map<String,Object> data;

    public EditorResp() {
    }

    public EditorResp(int errno, Map<String, Object> data) {
        this.errno = errno;
        this.data = data;
    }

    public static EditorResp success(String url, String title){
        Map<String, Object> map = new HashMap<>();
        map.put("src",url);
        map.put("title",title);
        return new EditorResp(0,map);
    }

    public static EditorResp error(String message){
        Map<String,Object> map = new HashMap<>();
        map.put("src",message);
        map.put("title","出错");
        return new EditorResp(1,map);
    }
}
