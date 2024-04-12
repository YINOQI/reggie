package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public R<String> saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        //为每个菜品添加套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
        return R.success("新增信息成功");
    }

    @Override
    @Transactional
    public R<String> deleteWithDish(List<Long> ids) {
        //构造条件
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.in(Setmeal::getId,ids);
        setmealWrapper.eq(Setmeal::getStatus,1);

        //判断是否有套餐还在售卖
        int count = this.count(setmealWrapper);
        if(count > 0){
            //若有套餐还在售卖，则抛出业务异常
            throw new CustomException("当前有套餐售卖中，不能删除");
        }

        //删除套表中数据
        this.removeByIds(ids);

        //删除关系表数据
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishWrapper);

        return R.success("删除套餐成功");
    }

    @Override
    public R<String>  updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        //为每个菜品添加套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        setmealDishService.saveBatch(setmealDishes);

        return R.success("修改信息成功");
    }

    @Override
    public R<SetmealDto> getSetmealById(Long id) {
        Setmeal setmeal = this.getById(id);
        if(setmeal == null){
            return R.error("获取套餐信息失败");
        }

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);

        Category category = categoryMapper.selectById(setmeal.getCategoryId());

        setmealDto.setCategoryName(category.getName());

        return R.success(setmealDto);
    }

    @Override
    public R<Page<SetmealDto>> getSetmealPage(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null,Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        this.page(setmealPage,setmealLambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Category categoryName = categoryMapper.selectById(item.getCategoryId());
            setmealDto.setCategoryName(categoryName.getName());

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    @Override
    public R<String> banSetmeal(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);


        List<Setmeal> list = this.list(setmealLambdaQueryWrapper);
        list.stream().map((item) -> {
            item.setStatus(0);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(list);

        return R.success("更新状态成功");
    }

    @Override
    public R<String> startSetmeal(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);

        List<Setmeal> list = this.list(setmealLambdaQueryWrapper);
        list.stream().map((item) -> {
            item.setStatus(1);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(list);

        return R.success("更新状态成功");
    }

    @Override
    public R<List<Setmeal>> listSetmeal(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = this.list(setmealLambdaQueryWrapper);

        return R.success(list);
    }
}
