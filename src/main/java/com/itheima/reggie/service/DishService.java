package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    R<String> saveWithFlavor(DishDto dishDto);

    R<DishDto> getByIdWithFlavor(Long id);

    R<String> updateWithFlavor(DishDto dishDto);

    R<Page<DishDto>> getDishPage(int page, int pageSize, String name);

    R<List<DishDto>> listDish(Dish dish);
}
