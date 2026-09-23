package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

//菜品管理
@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @Operation(summary = "新增菜品")
   public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理菜品缓存，实际 Redis key 是 dish + categoryId，如 dish16
        cleanCache("dish*");
       return Result.success();
   }
   @GetMapping("/page")
   @Operation(summary = "菜品分页查询")
   public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO ){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
   }
   @DeleteMapping
   @Operation(summary = "菜品批量删除")
   public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除:{}",ids);
       dishService.deleteBatch(ids);
 
       //清除所有菜品缓存，实际 Redis key 前缀为 dish
       cleanCache("dish*");
    return Result.success();
    }
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @Operation(summary = "修改菜品")
    public Result<String> update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //清除所有菜品缓存，Redis key 形如 dish16
        cleanCache("dish*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @Operation(summary = "启用/停售菜品")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id){
        log.info("启用/停售菜品：status={}, id={}", status, id);
        dishService.startOrStop(status,id);
        //清除所有菜品缓存，Redis key 形如 dish16
        cleanCache("dish*");
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }
 
    private void cleanCache(String pattern){
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
 
    }
}
