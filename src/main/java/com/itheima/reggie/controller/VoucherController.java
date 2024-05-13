package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Voucher;
import com.itheima.reggie.entity.VoucherOrder;
import com.itheima.reggie.service.VoucherOrderService;
import com.itheima.reggie.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherOrderService voucherOrderService;

    @GetMapping
    public R<List<Voucher>> getUserVoucher(){
        return voucherService.getUserVoucer();
    }

    @GetMapping("/voucherList")
    public R<Page<Voucher>> getVoucherList(int page,int pageSize){
        return voucherService.getVoucherList(page,pageSize);
    }

    @PostMapping("/{id}")
    public R<String> seckill(@PathVariable("id") Long id, HttpServletRequest request){
        return voucherService.seckill(id,request);
    }

    @GetMapping("/voucher-order")
    public R<List<VoucherOrder>> getVoucherOrder(){
        return voucherOrderService.getVoucherOrder();
    }

    @PutMapping("/addVoucher")
    public R<String> addVoucher(@RequestBody Voucher voucher){
        voucher.setRules("全场通用");
        voucherService.save(voucher);
        return R.success("添加成功");
    }

    @DeleteMapping
    public R<String> deleteVoucher(@RequestParam List<Long> ids){
        voucherService.removeByIds(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status")
    public R<String> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return voucherService.updateStatus(id,status);
    }

}
