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
import java.util.stream.Collectors;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements VoucherOrderService {
    @Override
    public R<List<VoucherOrder>> getVoucherOrder() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<VoucherOrder> voucherOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        voucherOrderLambdaQueryWrapper.eq(VoucherOrder::getUserId,userId)
                .isNull(VoucherOrder::getUseTime)
                .gt(VoucherOrder::getExpire_time, LocalDateTime.now());
        return R.success(this.list(voucherOrderLambdaQueryWrapper));
    }

    @Override
    public List<Long> getVoucherIds() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<VoucherOrder> voucherOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        voucherOrderLambdaQueryWrapper.eq(VoucherOrder::getUserId,userId)
                .isNull(VoucherOrder::getUseTime)
                .gt(VoucherOrder::getExpire_time, LocalDateTime.now());
        return super.list(voucherOrderLambdaQueryWrapper).stream().map(VoucherOrder::getVoucherId).collect(Collectors.toList());
    }
}
