package com.bluemsun.know.mapper;

import com.bluemsun.know.entity.FirstComment;
import com.bluemsun.know.entity.SecondComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    int getFirstCommentNum(int postId);

    int addFirstComment(FirstComment comment);

    List<FirstComment> getFirstCommentByPost(int postId);

    List<FirstComment> getFirstHotCommentByPost(int postId);

    int delFirstComment(int commentId);

    FirstComment getCommentById(int commentId);

    int addSecondComment(SecondComment comment);

    SecondComment getSecondCommentById(int id);

    int delSecondComment(int id);

    int delFirstLike(int id);

    int addFirstLike(int id);

    int addSecondLike(int id);

    int delSecondLike(int id);

    List<SecondComment> getSecondCommentByFirstId(int firstId);

}
