package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Tag(name = "C端用户相关接口")
@Slf4j

public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout(){
        log.info("微信用户退出登录");
        //jwt为无状态令牌，退出登录由前端清除token即可
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录:{}",userLoginDTO.getCode());
        //微信登录
        User user = userService.wxLogin(userLoginDTO);
        //为微信用户生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }

    /**
     * 查询当前登录用户信息
     * @return
     */
    @GetMapping("/getById")
    @Operation(summary = "查询当前登录用户信息")
    public Result<User> getById() {
        log.info("查询当前登录用户信息：{}", BaseContext.getCurrentId());
        User user = userService.getById(BaseContext.getCurrentId());
        return Result.success(user);
    }

    /**
     * 修改用户信息（头像、昵称等）
     * @param user
     * @return
     */
    @PutMapping
    @Operation(summary = "修改用户信息")
    public Result update(@RequestBody User user) {
        log.info("修改用户信息：{}", user);
        user.setId(BaseContext.getCurrentId());
        userService.updateById(user);
        return Result.success();
    }
}
