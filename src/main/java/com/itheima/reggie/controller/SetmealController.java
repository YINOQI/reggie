package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);

        return R.success("新增信息成功");
    }

    /**
     * 获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        if(setmeal == null){
            return R.error("获取套餐信息失败");
        }

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);

        Category category = categoryService.getById(setmeal.getCategoryId());

        setmealDto.setCategoryName(category.getName());

        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改信息成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null,Setmeal::getName,name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Category categoryName = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(categoryName.getName());

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);

        return R.success(setmealDtoPage);
    }

    @PostMapping("status/0")
    public R<String> update(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);


        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
        list.stream().map((item) -> {
            item.setStatus(0);
            return item;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(list);

        return R.success("更新状态成功");
    }

    @PostMapping("status/1")
    public R<String> updateStatus(@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);


        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
        list.stream().map((item) -> {
            item.setStatus(1);
            return item;
        }).collect(Collectors.toList());

        setmealService.updateBatchById(list);

        return R.success("更新状态成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        setmealService.deleteWithDish(ids);

        return R.success("删除套餐成功");
    }

    /**
     * 获取列表数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(list);
    }
}
