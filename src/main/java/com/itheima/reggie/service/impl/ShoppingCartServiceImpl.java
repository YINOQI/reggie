package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.utils.BaseContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {
        long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        Long dishId = shoppingCart.getDishId();

        shoppingCart.setUserId(currentId);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        if (dishId != null) {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCartOne = this.getOne(shoppingCartLambdaQueryWrapper);
        if (shoppingCartOne != null) {
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
            this.updateById(shoppingCartOne);
        }else{
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);
    }

    @Override
    public R<List<ShoppingCart>> listShoppingCart() {
        long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = this.list(shoppingCartLambdaQueryWrapper);

        return R.success(list);
    }

    @Override
    public R<String> clear() {
        long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,currentId);

        this.remove(shoppingCartLambdaQueryWrapper);

        return R.success("清空购物车成功");
    }
}
