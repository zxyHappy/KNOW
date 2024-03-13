package com.bluemsun.know.mapper;

import com.bluemsun.know.entity.Block;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlockMapper {

    boolean addBlock(Block block);

    boolean deleteBlock(int id);

    List<Block> getAllBlock();
}
