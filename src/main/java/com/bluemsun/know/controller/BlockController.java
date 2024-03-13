package com.bluemsun.know.controller;

import com.bluemsun.know.entity.Block;
import com.bluemsun.know.service.BlockService;
import com.bluemsun.know.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/block")
public class BlockController {

    @Autowired
    BlockService blockService;

    @RequestMapping("/add")
    public Result addBlock(@RequestBody Block block){
        return blockService.addBlock(block);
    }

    @RequestMapping("/delete/{id}")
    public Result deleteBlock(@PathVariable int id){
        return blockService.deleteBlock(id);
    }

    @RequestMapping("/get")
    public Result getAllBlock(){
        return blockService.getAllBlock();
    }
}
