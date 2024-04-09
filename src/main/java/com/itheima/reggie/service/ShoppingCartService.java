package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> add(ShoppingCart shoppingCart);

    R<List<ShoppingCart>> listShoppingCart();

    R<String> clear();
}
