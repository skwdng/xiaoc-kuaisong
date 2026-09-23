package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
/**
 * 保存带有口味信息的菜品数据
 * 该方法用于将包含口味信息的菜品数据保存到数据库中
 * 
 * @param dishDTO 菜品数据传输对象，包含菜品的基本信息及其口味数据
 */
    public void saveWithFlavor(DishDTO dishDTO);

/**
 * 分页查询菜品信息
 * 
 * @param dishPageQueryDTO 菜品分页查询条件数据传输对象，包含分页参数和查询条件
 * @return PageResult 分页查询结果，包含菜品数据列表和分页信息
 */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

/**
 * 批量删除方法
 * 根据传入的ID列表批量删除对应的数据
 * @param ids 需要删除的数据ID列表，使用Long类型包装器类
 */
    void deleteBatch(List<Long> ids);

/**
 * 根据ID查询菜品信息，并包含口味数据
 * 
 * @param id 菜品的ID
 * @return DishVO 包含菜品详细信息和口味数据的视图对象
 */
    DishVO getByIdWithFlavor(Long id);

/**
 * 根据特定口味更新菜品信息
 * 该方法用于接收一个DishDTO对象，并根据其中的口味信息更新对应的菜品数据
 * 
 * @param dishDTO 包含菜品更新信息的DTO对象，其中应包含口味相关的数据
 */
    void updateWithFlavor(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);

    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
