package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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
    public boolean remove(Long id) {
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

        return true;
    }

    @Override
    public R<Page<Category>> getCategoryPage(int page, int pageSize) {
        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        this.page(categoryPage, queryWrapper);

        return R.success(categoryPage);
    }

    @Override
    public R<List<Category>> listCategory(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType() != null, Category::getType, category.getType())
                .orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = this.list(queryWrapper);

        return R.success(list);
    }
}
