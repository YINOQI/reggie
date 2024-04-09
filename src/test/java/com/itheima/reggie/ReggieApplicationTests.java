package com.itheima.reggie;

import com.itheima.reggie.common.R;
import com.itheima.reggie.controller.ShoppingCartController;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import com.itheima.reggie.utils.BaseContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ReggieApplicationTests {
    @InjectMocks
    private ShoppingCartController shoppingCartController;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Before
    public void init(){
        //模拟当前用户已登录
        BaseContext.setCurrentId(1L);
    }

    @Test
    public void testAddExistingShoppingCart() {
        // TC-001: 购物车中已存在相同的菜品，更新数量

        // 模拟当前用户已登录

        // 创建模拟的购物车记录
        ShoppingCart existingShoppingCart = new ShoppingCart();
        existingShoppingCart.setUserId(1L);
        existingShoppingCart.setDishId(1L);
        existingShoppingCart.setNumber(2);
        existingShoppingCart.setAmount(new BigDecimal(200));

        // 模拟购物车中已存在相同的记录
        when(shoppingCartService.getOne(any())).thenReturn(existingShoppingCart);

        // 调用被测试的方法
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals(200, result.getData().getAmount().intValue());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(1)).getOne(any());
        verify(shoppingCartService, times(1)).updateById(any());
        verify(shoppingCartService, times(0)).save(any());
    }

    @Test
    public void testAddExistingSetmeal() {
        // TC-002: 购物车中已存在相同的套餐，更新数量

        // 模拟当前用户已登录
        long currentId = BaseContext.getCurrentId();

        // 创建模拟的购物车记录
        ShoppingCart existingShoppingCart = new ShoppingCart();
        existingShoppingCart.setUserId(currentId);
        existingShoppingCart.setSetmealId(1L);
        existingShoppingCart.setNumber(3);
        existingShoppingCart.setAmount(new BigDecimal(300));

        // 模拟购物车中已存在相同的记录
        when(shoppingCartService.getOne(any())).thenReturn(existingShoppingCart);

        // 调用被测试的方法
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals(300, result.getData().getAmount().intValue());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(1)).getOne(any());
        verify(shoppingCartService, times(1)).updateById(any());
        verify(shoppingCartService, times(0)).save(any());
    }


    @Test
    public void testAddNewShoppingCart() {
        // TC-003: 购物车中不存在相同的菜品和套餐，新增记录

        // 模拟当前用户已登录
        long currentId = BaseContext.getCurrentId();

        // 模拟购物车中不存在相同记录
        when(shoppingCartService.getOne(any())).thenReturn(null);

        // 模拟保存购物车记录成功
        when(shoppingCartService.save(any())).thenReturn(true);

        // 调用被测试的方法
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals(1, result.getData().getNumber().intValue());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(1)).getOne(any());
        verify(shoppingCartService, times(0)).updateById(any());
        verify(shoppingCartService, times(1)).save(any());
    }

    @Test
    public void testAddEmptyDishId() {
        // TC-004: 购物车中不存在相同的菜品和套餐，但dishId为空

        // 模拟当前用户已登录
        long currentId = BaseContext.getCurrentId();

        // 模拟购物车中不存在相同记录
        when(shoppingCartService.getOne(any())).thenReturn(null);

        // 模拟保存购物车记录成功
        when(shoppingCartService.save(any())).thenReturn(true);

        // 调用被测试的方法，这里 ShoppingCart 的 dishId 设置为空
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals(1, result.getData().getNumber().intValue());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(1)).getOne(any());
        verify(shoppingCartService, times(0)).updateById(any());
        verify(shoppingCartService, times(1)).save(any());
    }

    @Test
    public void testAddEmptySetmealId() {
        // TC-005: 购物车中不存在相同的菜品和套餐，但setmealId为空

        // 模拟当前用户已登录
        long currentId = BaseContext.getCurrentId();

        // 模拟购物车中不存在相同记录
        when(shoppingCartService.getOne(any())).thenReturn(null);

        // 模拟保存购物车记录成功
        when(shoppingCartService.save(any())).thenReturn(true);

        // 调用被测试的方法，这里 ShoppingCart 的 setmealId 设置为空
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals(1, result.getData().getNumber().intValue());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(1)).getOne(any());
        verify(shoppingCartService, times(0)).updateById(any());
        verify(shoppingCartService, times(1)).save(any());
    }

    @Test
    public void testAddUserNotLoggedIn() {
        // TC-006: 当前用户未登录

        // 模拟当前用户未登录
        // 调用被测试的方法
        R<ShoppingCart> result = shoppingCartController.add(new ShoppingCart());

        // 验证结果
        assertEquals("用户未登录", result.getMsg());

        // 验证是否按预期调用了相关服务
        verify(shoppingCartService, times(0)).getOne(any());
        verify(shoppingCartService, times(0)).updateById(any());
        verify(shoppingCartService, times(0)).save(any());
    }
}
