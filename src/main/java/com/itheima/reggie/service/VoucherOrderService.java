package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.VoucherOrder;

import java.util.List;

public interface VoucherOrderService extends IService<VoucherOrder> {
    R<List<VoucherOrder>> getVoucherOrder();
    List<Long> getVoucherIds();
}
