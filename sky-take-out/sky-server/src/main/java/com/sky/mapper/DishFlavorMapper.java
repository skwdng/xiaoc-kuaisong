package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    //批量插入口味数据
    void insertBatch(List<DishFlavor> flavors);
/**
 * 根据菜品ID删除对应的菜品口味数据
 * @param dishId 菜品的ID，用于确定要删除哪些口味数据
 */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")

    void deleteByDishId(Long dishId);  // 方法名表示通过菜品ID删除，参数为菜品ID
    //根据菜品id集合批量删除关联的口味数据

    void deleteByDishIds(List<Long> dishIds);
    //根据菜品id查询对应口味数据
    @Select("select * from dish_flavor where dish_id = #{dishId}")

    List<DishFlavor> getByDishId(Long dishId);
}
