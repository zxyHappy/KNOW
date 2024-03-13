package com.bluemsun.know.mapper;

import com.bluemsun.know.entity.SearchKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyMapper {

    int addKey(SearchKey searchKey);

    List<SearchKey> getKeys();

    SearchKey getKeyByText(String keyText);

    int updateKey(int id);

}
