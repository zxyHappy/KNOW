package com.bluemsun.know.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class Page<T> {

    private int currentPage;
    private int pageSize;
    private int totalRecord;
    List<T> list;
    private int totalPage;
    private int startIndex;

    public Page() {
    }

    public void setPage(int currentPage, int pageSize, int totalRecord){
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;

        if(totalRecord % pageSize == 0){
            this.totalPage = totalRecord/pageSize;
        }else{
            this.totalPage = totalRecord/pageSize+1;
        }
        this.startIndex = (currentPage-1)*pageSize;
    }


}
