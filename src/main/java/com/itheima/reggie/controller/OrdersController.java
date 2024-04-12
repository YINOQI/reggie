package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        return ordersService.submit(orders);
    }

    /**
     * 获取用户订单列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getUserPage(int page,int pageSize){
        return ordersService.getUserPage(page,pageSize);
    }

    @GetMapping("/page")
    public R<Page<Orders>> getOrderPage(int page,int pageSize){
        return ordersService.getOrderPage(page,pageSize);
    }

    /**
     * 获取订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> status(@RequestBody Orders orders){
        return ordersService.updateStatus(orders);
    }
}
