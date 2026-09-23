package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    // 批量插入套餐菜品关系数据
    void insertBatch(List<SetmealDish> setmealDishes);

    // 根据菜品id查询对应的套餐id
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    // 根据套餐id查询套餐菜品关系
    List<SetmealDish> getBySetmealId(Long setmealId);

    // 根据套餐id批量删除套餐菜品关系
    void deleteBySetmealIds(List<Long> setmealIds);
}
