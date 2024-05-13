package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Voucher;
import com.itheima.reggie.entity.VoucherOrder;
import com.itheima.reggie.mapper.VoucherMapper;
import com.itheima.reggie.mapper.VoucherOrderMapper;
import com.itheima.reggie.service.VoucherService;
import com.itheima.reggie.utils.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {
    @Autowired
    private VoucherOrderMapper voucherOrderMapper;

    @Autowired
    private RedisIdWorker redisIDWorker;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public R<String> seckill(Long id, HttpServletRequest request) {
//        Long userId = BaseContext.getCurrentId();

        Voucher voucher = this.getById(id);
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return R.error("活动尚未开始");
        }
        if (voucher.getExpireTime().isBefore(LocalDateTime.now())) {
            return R.error("活动已结束");
        }
        if (voucher.getAmount() <= 0) {
            return R.error("优惠券已抢光");
        }

        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherId(id);
        voucherOrder.setUserId(1L);
        voucherOrder.setStatus(0);
        voucherOrder.setCreateTime(LocalDateTime.now());
        voucherOrder.setExpire_time(voucher.getExpireTime());

        LambdaUpdateWrapper<Voucher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Voucher::getAmount, voucher.getAmount() - 1)
                .eq(Voucher::getId,voucher.getId());
        this.update(wrapper);

        voucherOrderMapper.insert(voucherOrder);

        return R.success("领券成功");
    }

    @Override
    public R<Page<Voucher>> getVoucherList(int page, int pageSize) {
        Page<Voucher> voucherPage = new Page<>(page,pageSize);
        Page<Voucher> pageList = this.page(voucherPage);
        return R.success(pageList);
    }

    @Override
    public R<List<Voucher>> getUserVoucer() {
        LambdaQueryWrapper<Voucher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(Voucher::getAmount,0).eq(Voucher::getStatus,1);
        return R.success(this.list(queryWrapper));
    }

    @Override
    public R<String> updateStatus(Long id, Integer status) {
        LambdaUpdateWrapper<Voucher> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Voucher::getId,id).set(Voucher::getStatus,status);
        this.update(wrapper);
        return R.success("更新状态成功");
    }
}
