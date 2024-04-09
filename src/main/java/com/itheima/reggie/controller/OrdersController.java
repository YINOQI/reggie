package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("提交订单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> orderPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

        ordersService.page(orderPage);
        BeanUtils.copyProperties(orderPage,ordersDtoPage,"records");

        List<Orders> records = orderPage.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item) ->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getId,item.getNumber());
            List<OrderDetail> orderDetail = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetail);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    @GetMapping("/page")
    public R<Page> orderPage(int page,int pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<Orders> orederPage = ordersService.page(ordersPage);

        return R.success(orederPage);
    }

    @PutMapping
    public R<String> status(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.eq(Orders::getId,orders.getId()).set(Orders::getStatus,orders.getStatus());
        ordersService.update(ordersLambdaUpdateWrapper);
        return R.success("修改状态成功");
    }
}
