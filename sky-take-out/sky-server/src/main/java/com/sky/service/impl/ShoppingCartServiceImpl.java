package com.sky.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
//判断当前加入到购物车中的商品是否存在
ShoppingCart shoppingCart = new ShoppingCart();
BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
Long userId = BaseContext.getCurrentId();
shoppingCart.setUserId(userId);
shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
shoppingCart.setDishId(shoppingCartDTO.getDishId());
shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());
List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
//如果已经存在了，只需要将数量加一
if (list != null && list.size()>0){
  ShoppingCart cart = list.get(0);
  cart.setNumber(cart.getNumber() + 1);
  shoppingCartMapper.updateNumberById(cart);
}else{
//如果不存在，需要插入一条购物车数据
Long dishId = shoppingCartDTO.getDishId();
if (dishId != null){
  Dish dish = dishMapper.getById(dishId);
  shoppingCart.setName(dish.getName());
  shoppingCart.setImage(dish.getImage());
  shoppingCart.setAmount(dish.getPrice());
  shoppingCart.setNumber(1);
  shoppingCart.setCreateTime(LocalDateTime.now());
}else{
Long setmealId = shoppingCartDTO.getSetmealId();
Setmeal setmeal = setmealMapper.getById(setmealId);
shoppingCart.setName(setmeal.getName());
shoppingCart.setImage(setmeal.getImage());
shoppingCart.setAmount(setmeal.getPrice());

}
shoppingCart.setNumber(1);
shoppingCart.setCreateTime(LocalDateTime.now());
shoppingCartMapper.insert(shoppingCart);
}
    }

    @Override
    public List<ShoppingCart> list() {
//查询当前用户的购物车数据
ShoppingCart shoppingCart = new ShoppingCart();
shoppingCart.setUserId(BaseContext.getCurrentId());
List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
return list;
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
//删除购物车中一个商品
ShoppingCart shoppingCart = new ShoppingCart();
BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
shoppingCart.setUserId(BaseContext.getCurrentId());
List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
if (list != null && list.size() > 0){
  ShoppingCart cart = list.get(0);
  if (cart.getNumber() > 1){
//数量大于一，数量减一
    cart.setNumber(cart.getNumber() - 1);
    shoppingCartMapper.updateNumberById(cart);
  }else{
//数量等于一，直接删除该条记录
    shoppingCartMapper.deleteById(cart.getId());
  }
}
    }

    @Override
    public void cleanShoppingCart() {
//清空当前用户的购物车数据
shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
