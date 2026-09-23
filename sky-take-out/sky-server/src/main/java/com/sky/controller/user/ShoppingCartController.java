package com.sky.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/shoppingCart")
@Tag(name = "C端购物车相关接口")
@Slf4j
public class ShoppingCartController {

  @Autowired
  private ShoppingCartService shoppingCartService;
  @PostMapping("/add")
  @Operation(summary = "添加购物车")
  public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
    log.info("添加购物车,商品信息为：{}", shoppingCartDTO);
    shoppingCartService.addShoppingCart(shoppingCartDTO);
    return Result.success();
  }
  @GetMapping("/list")
  @Operation(summary = "查询购物车列表")
  public Result<List<ShoppingCart>> list(){
    log.info("查询购物车列表");
    List<ShoppingCart> list = shoppingCartService.list();
    return Result.success(list);
  }
  @PostMapping("/sub")
  @Operation(summary = "删除购物车中一个商品")
  public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO){
    log.info("删除购物车中一个商品:{}", shoppingCartDTO);
    shoppingCartService.subShoppingCart(shoppingCartDTO);
    return Result.success();
  }
  @DeleteMapping("/clean")
  @Operation(summary = "清空购物车")
  public Result clean(){
    log.info("清空购物车");
    shoppingCartService.cleanShoppingCart();
    return Result.success();
  }
}
