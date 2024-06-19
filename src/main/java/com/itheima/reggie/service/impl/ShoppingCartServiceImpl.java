package com.itheima.reggie.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.itheima.reggie.utils.RedisConstants.SHOPPING_CART;


@Service
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingCart) {
        long currentId = BaseContext.getCurrentId();
//        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        Long dishId = shoppingCart.getDishId();

        String key = SHOPPING_CART + currentId;

        shoppingCart.setUserId(currentId);
        shoppingCart.setNumber(null);

        String strShoppingCart = JSONUtil.toJsonStr(shoppingCart);

        Double score = stringRedisTemplate.opsForZSet().score(key, strShoppingCart);
        if (score == null) {
            stringRedisTemplate.opsForZSet().add(key, strShoppingCart, 1);
            stringRedisTemplate.expire(key, 10, TimeUnit.MINUTES);
        } else {
            stringRedisTemplate.opsForZSet().incrementScore(key, strShoppingCart, 1);
        }
        Double number = stringRedisTemplate.opsForZSet().score(key, strShoppingCart);
        shoppingCart.setNumber(number.intValue());

//        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(SHOPPING_CART + currentId + dishId);
//
//        if (!entries.isEmpty()) {
//            ShoppingCart cart = BeanUtil.mapToBean(entries, ShoppingCart.class, false,
//                    CopyOptions.create()
//                            .setIgnoreNullValue(true)
//                            .setFieldValueEditor((name, value) -> {
//                                if (value.equals("null")) {
//                                    return null;
//                                } else {
//                                    return value;
//                                }
//                            }));
//            cart.setNumber(cart.getNumber() + 1);
//            shoppingCart = cart;
//        }

//        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(shoppingCart, new HashMap<>(),
//                CopyOptions.create()
//                        .setIgnoreNullValue(true)
//                        .setFieldValueEditor((name, value) -> {
//                            if (value == null) {
//                                return null;
//                            } else {
//                                return value.toString();
//                            }
//                        }));
//        stringRedisTemplate.opsForHash().putAll(SHOPPING_CART + currentId + dishId, stringObjectMap);


//        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
//        if (dishId != null) {
//            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
//        } else {
//            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
//        }
//        ShoppingCart shoppingCartOne = this.getOne(shoppingCartLambdaQueryWrapper);
//        if (shoppingCartOne != null) {
//            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
//            this.updateById(shoppingCartOne);
//        } else {
//            shoppingCart.setNumber(1);
//            this.save(shoppingCart);
//            shoppingCartOne = shoppingCart;
//        }

        return R.success(shoppingCart);
    }

    @Override
    public List<ShoppingCart> listShoppingCart() {
        long currentId = BaseContext.getCurrentId();

        List<ShoppingCart> list = new ArrayList<>();

        String key = SHOPPING_CART + currentId;

        Long size = stringRedisTemplate.opsForZSet().zCard(key);
        if (size != null) {
            Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, size);
            if(range !=null && !range.isEmpty()){
                range.forEach(value -> {
                    Double score = stringRedisTemplate.opsForZSet().score(key, value);
                    int number = score.intValue();
                    ShoppingCart shoppingCart = JSONUtil.toBean(value, ShoppingCart.class);
                    shoppingCart.setNumber(number);
                    list.add(shoppingCart);
                });
            }
        }else {
            return ListUtil.empty();
        }

//        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
//        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
//
//        List<ShoppingCart> list = this.list(shoppingCartLambdaQueryWrapper);

        return list;
    }

    @Override
    public R<String> clear() {
        long currentId = BaseContext.getCurrentId();
        String key = SHOPPING_CART + currentId;
        stringRedisTemplate.delete(key);

//        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
//
//        this.remove(shoppingCartLambdaQueryWrapper);

        return R.success("清空购物车成功");
    }

    @Override
    public void removeDish(Long dishId) {
        long currentId = BaseContext.getCurrentId();
        String key = SHOPPING_CART + currentId;

//        stringRedisTemplate.opsForZSet().incrementScore(key,)
//        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId).eq(ShoppingCart::getDishId, dishId);

//        this.remove(shoppingCartLambdaQueryWrapper);
    }
}
