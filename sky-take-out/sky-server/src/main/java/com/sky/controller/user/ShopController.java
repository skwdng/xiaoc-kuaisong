package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Tag(name = "店铺相关接口")
@Slf4j

public class ShopController {
    public static final String KEY = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/status")
    @Operation(summary = "获取店铺的营业状态")
    public Result<Integer> getStatus(){

        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        //Redis重启/清空后键会丢失，默认营业中，避免status为null时拆箱NPE导致接口500
        if (status == null) {
            status = 1;
        }
        log.info("获取到店铺的营业状态为: {}",status ==1 ? "营业中":"打烊中");
        return Result.success(status);
    }

    @GetMapping("/phone")
    @Operation(summary = "获取商家电话")
    public Result<String> getShopPhone(){
        log.info("获取商家电话");
        return Result.success("15248555588");
    }
}
