package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    R<String> saveWithDish(SetmealDto setmealDto);
    R<String> deleteWithDish(List<Long> ids);

    R<String>  updateWithDish(SetmealDto setmealDto);

    R<SetmealDto> getSetmealById(Long id);

    R<Page<SetmealDto>> getSetmealPage(int page, int pageSize, String name);

    R<String> banSetmeal(List<Long> ids);

    R<String> startSetmeal(List<Long> ids);

    R<List<Setmeal>> listSetmeal(Setmeal setmeal);
}
