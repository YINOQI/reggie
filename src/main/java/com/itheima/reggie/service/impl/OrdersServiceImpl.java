package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.*;
import com.itheima.reggie.service.*;
import com.itheima.reggie.utils.BaseContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private VoucherOrderService voucherOrderService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public R<String> submit(Orders orders) {
        //获得当前用户id
        long currentId = BaseContext.getCurrentId();

        List<ShoppingCart> shoppingCartList = shoppingCartService.listShoppingCart();

//        //查询当前用户的购物车数据
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShoppingCart::getUserId, currentId);
//        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(queryWrapper);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空");
        }

        //查询用户数据
        User user = userMapper.selectById(currentId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息为空");
        }

        VoucherOrder voucherOrder = null;

        // 判断是否使用优惠券
        if (orders.getVoucherId() != null) {
            LambdaQueryWrapper<VoucherOrder> voucherOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
            voucherOrderLambdaQueryWrapper.eq(VoucherOrder::getUserId,currentId).eq(VoucherOrder::getVoucherId,orders.getVoucherId());

            // 判断优惠券是否过期
            voucherOrder = voucherOrderService.getOne(voucherOrderLambdaQueryWrapper);
            if (voucherOrder.getExpire_time() != null && voucherOrder.getExpire_time().isBefore(LocalDateTime.now())) {
                throw new CustomException("该优惠券已过期");
            }
            // 判断优惠券是否已被使用
            if (voucherOrder.getUseTime() != null) {
                throw new CustomException("该优惠券已使用过");
            }

            voucherOrder.setUseTime(LocalDateTime.now());
        }

        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //保存订单信息
        this.save(orders);

        //保存订单详细信息
        orderDetailService.saveBatch(orderDetails);

        //扣减优惠卷
        if (voucherOrder != null) {
            voucherOrderService.updateById(voucherOrder);
        }
//        LambdaUpdateWrapper<VoucherOrder> voucherOrderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        voucherOrderLambdaUpdateWrapper.eq(orders.getVoucherId() != null, VoucherOrder::getId, orders.getVoucherId())
//                .eq(VoucherOrder::getUserId,currentId)
//                .set(VoucherOrder::getUseTime, LocalDateTime.now());
//        voucherOrderService.update(voucherOrderLambdaUpdateWrapper);

        //清空购物车数据
        shoppingCartService.clear();

        return R.success("提交订单成功");
    }

    @Override
    public R<Page<OrdersDto>> getUserPage(int page, int pageSize) {
        //获得当前用户id
        long currentId = BaseContext.getCurrentId();

        Page<Orders> orderPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

        this.page(orderPage, new LambdaQueryWrapper<Orders>().orderByDesc(Orders::getOrderTime).eq(Orders::getUserId,currentId));
        BeanUtils.copyProperties(orderPage, ordersDtoPage, "records");

        List<Orders> records = orderPage.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            List<OrderDetail> orderDetail = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getId, item.getNumber()));
            ordersDto.setOrderDetails(orderDetail);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    @Override
    public R<Page<Orders>> getOrderPage(int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<Orders> orederPage = this.page(ordersPage);

        return R.success(orederPage);
    }

    @Override
    public R<String> updateStatus(Orders orders) {
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.eq(Orders::getId, orders.getId()).set(Orders::getStatus, orders.getStatus());
        this.update(ordersLambdaUpdateWrapper);
        return R.success("修改状态成功");
    }
}
