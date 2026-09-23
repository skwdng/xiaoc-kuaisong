package com.sky.service;

import java.util.List;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

public interface ShoppingCartService {

  void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

  List<ShoppingCart> list();

  void subShoppingCart(ShoppingCartDTO shoppingCartDTO);

  void cleanShoppingCart();

}
