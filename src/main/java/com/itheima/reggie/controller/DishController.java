package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜品信息操作
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        return dishService.saveWithFlavor(dishDto);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        return dishService.getDishPage(page,pageSize,name);
    }

    /**
     * 获取菜品以及菜品口味
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        return dishService.getByIdWithFlavor(id);
    }


    /**
     * 更新菜品信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody DishDto dishDto) {
        return dishService.updateWithFlavor(dishDto);
    }
    

    /**
     * 获取菜品列表
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> getList(Dish dish) {
        return dishService.listDish(dish);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        dishService.remove(ids);
        return R.success("");
    }
}


