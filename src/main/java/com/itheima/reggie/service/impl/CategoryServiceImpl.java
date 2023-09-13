package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 全局异常处理器
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * 删除菜品，如果其已关联菜品，则提示无法删除
     * @param id
     */
    @Override
    public void remove(Long id) {
        //添加查询条件，根据id进行查询
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        //查询当前分类是否关联菜品，若已关联，则抛出异常，提示无法删除
        if (dishService.count(dishQueryWrapper) > 0) {
            throw new CustomException("当前分类下关联了菜品，无法删除");
        }

        //添加查询条件，根据id进行查询
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        //查询当前分类是否关联菜品，若已关联，则抛出异常，提示无法删除
        if (setmealService.count(setmealQueryWrapper) > 0) {
            throw new CustomException("当前分类下关联了套餐，无法删除");
        }

        super.removeById(id);
    }
}
