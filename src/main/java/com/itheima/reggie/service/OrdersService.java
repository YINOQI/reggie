package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.Orders;


public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    R<String> submit(Orders orders);

    R<Page<OrdersDto>> getUserPage(int page, int pageSize);

    R<Page<Orders>> getOrderPage(int page, int pageSize);

    R<String> updateStatus(Orders orders);

}
