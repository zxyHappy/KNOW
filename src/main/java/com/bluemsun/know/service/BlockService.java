package com.bluemsun.know.service;

import com.bluemsun.know.entity.Block;
import com.bluemsun.know.entity.Label;
import com.bluemsun.know.mapper.BlockMapper;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockService {

    @Autowired
    BlockMapper blockMapper;

    public Result addBlock(Block block){
        try{
            boolean i = blockMapper.addBlock(block);
            if (i) {
                return Result.success("添加成功", null);
            } else {
                return Result.error(300, "添加失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result deleteBlock(int id){
        try{
            boolean i = blockMapper.deleteBlock(id);
            if (i) {
                return Result.success("删除成功", null);
            } else {
                return Result.error(300, "删除失败", null);
            }
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }

    public Result getAllBlock(){
        try{
            List<Block> list = blockMapper.getAllBlock();
            List<Label> labels = new ArrayList<>();
            for(Block b : list){
                labels.add(new Label(b));
            }
            return Result.success("获取标签成功", labels);
        }catch (Exception e){
            return Result.error(250,e.getMessage(),null);
        }
    }
}
