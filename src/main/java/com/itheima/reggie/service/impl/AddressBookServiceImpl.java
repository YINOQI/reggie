package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.utils.BaseContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 全局异常处理器
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    public R<AddressBook> setDefault(AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                .set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        this.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        this.updateById(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R<AddressBook> get(Long id) {
        AddressBook addressBook = this.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("该地址不存在");
        }
    }

    @Override
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                .eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = this.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有设置默认地址");
        } else {
            return R.success(addressBook);
        }

    }

    @Override
    public R<List<AddressBook>> listAddress() {
        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != BaseContext.getCurrentId(), AddressBook::getUserId, BaseContext.getCurrentId())
                .orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(this.list(queryWrapper));
    }

    @Override
    public R<AddressBook> saveAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        this.save(addressBook);
        return R.success(addressBook);
    }
}
