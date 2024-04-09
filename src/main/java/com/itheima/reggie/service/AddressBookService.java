package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.Category;

import java.util.List;


public interface AddressBookService extends IService<AddressBook> {

    R<AddressBook> setDefault(AddressBook addressBook);

    R<AddressBook> get(Long id);

    R<AddressBook> getDefault();

    R<List<AddressBook>> listAddress();

    R<AddressBook> saveAddress(AddressBook addressBook);
}
