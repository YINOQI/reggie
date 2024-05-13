package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Voucher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface VoucherService extends IService<Voucher> {
    R<String> seckill(Long id, HttpServletRequest request);

    R<Page<Voucher>> getVoucherList(int page, int pageSize);

    R<List<Voucher>> getUserVoucer();

    R<String> updateStatus(Long id, Integer status);
}
