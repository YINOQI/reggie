package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
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

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> getUserPage(int page,int pageSize){
        return ordersService.getUserPage(page,pageSize);
    }

    @GetMapping("/page")
    public R<Page<Orders>> getOrderPage(int page,int pageSize){
        return ordersService.getOrderPage(page,pageSize);
    }

    @PutMapping
    public R<String> status(@RequestBody Orders orders){
        return ordersService.updateStatus(orders);
    }
}
