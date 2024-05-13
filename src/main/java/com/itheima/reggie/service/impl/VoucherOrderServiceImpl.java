package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.VoucherOrder;
import com.itheima.reggie.mapper.VoucherOrderMapper;
import com.itheima.reggie.service.VoucherOrderService;
import com.itheima.reggie.utils.BaseContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements VoucherOrderService {
    @Override
    public R<List<VoucherOrder>> getVoucherOrder() {
//        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<VoucherOrder> voucherOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        voucherOrderLambdaQueryWrapper.eq(VoucherOrder::getUserId,1L)
                .isNull(VoucherOrder::getUseTime)
                .gt(VoucherOrder::getExpire_time, LocalDateTime.now());
        return R.success(this.list(voucherOrderLambdaQueryWrapper));
    }
}
