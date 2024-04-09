package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    /**
     * 新增套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        return setmealService.saveWithDish(setmealDto);
    }

    /**
     * 获取套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id){
        return setmealService.getSetmealById(id);
    }

    @PutMapping
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        return setmealService.updateWithDish(setmealDto);
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        return setmealService.getSetmealPage(page,pageSize,name);
    }

    @PostMapping("status/0")
    public R<String> banSetmeal(@RequestParam List<Long> ids){
        return setmealService.banSetmeal(ids);
    }

    @PostMapping("status/1")
    public R<String> startSetmeal(@RequestParam List<Long> ids){
        return setmealService.startSetmeal(ids);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        return setmealService.deleteWithDish(ids);
    }

    /**
     * 获取列表数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        return setmealService.listSetmeal(setmeal);
    }
}
