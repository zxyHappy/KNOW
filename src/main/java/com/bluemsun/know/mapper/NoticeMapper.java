package com.bluemsun.know.mapper;

import com.bluemsun.know.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {

    int addNotice(Notice notice);

    int addRelation(int noticeId, int userId);

    int setRead(int noticeType, int userId);

    int delNotice(int noticeId, int userId);

    int getNoticeNoReadNum(int userId, int noticeType);

    List<Notice> getNoticeByType(int userId, int noticeType);
}
