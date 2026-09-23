package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    //订单状态：2待接单 4派送中 5已完成 6已取消
    private static final Integer TO_BE_CONFIRMED = 2;
    private static final Integer DELIVERY_IN_PROGRESS = 4;
    private static final Integer COMPLETED = 5;
    private static final Integer CANCELLED = 6;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    public BusinessDataVO businessData() {
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now();

        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", COMPLETED);

        //今日营业额（已完成订单金额合计）
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        //今日有效订单数（已完成）
        Integer validOrderCount = orderMapper.countByMap(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;

        //今日总订单数
        map.remove("status");
        Integer orderCount = orderMapper.countByMap(map);
        orderCount = orderCount == null ? 0 : orderCount;

        //订单完成率、平均客单价
        Double orderCompletionRate = 0.0;
        Double unitPrice = 0.0;
        if (orderCount != 0 && validOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / orderCount;
            unitPrice = turnover / validOrderCount;
        }

        //今日新增用户数
        Integer newUsers = userMapper.countByMap(map);
        newUsers = newUsers == null ? 0 : newUsers;

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO overviewOrders() {
        //待接单
        Integer waitingOrders = orderMapper.countByMap(buildStatusMap(TO_BE_CONFIRMED));
        //派送中
        Integer deliveredOrders = orderMapper.countByMap(buildStatusMap(DELIVERY_IN_PROGRESS));
        //已完成
        Integer completedOrders = orderMapper.countByMap(buildStatusMap(COMPLETED));
        //已取消
        Integer cancelledOrders = orderMapper.countByMap(buildStatusMap(CANCELLED));
        //全部订单
        Integer allOrders = orderMapper.countByMap(new HashMap<>());

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览：起售数、停售数
     * @return
     */
    public DishOverViewVO overviewDishes() {
        Integer sold = dishMapper.countByMap(buildStatusMap(StatusConstant.ENABLE));
        Integer discontinued = dishMapper.countByMap(buildStatusMap(StatusConstant.DISABLE));

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览：起售数、停售数
     * @return
     */
    public SetmealOverViewVO overviewSetmeals() {
        Integer sold = setmealMapper.countByMap(buildStatusMap(StatusConstant.ENABLE));
        Integer discontinued = setmealMapper.countByMap(buildStatusMap(StatusConstant.DISABLE));

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    private Map<String, Object> buildStatusMap(Integer status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        return map;
    }
}
