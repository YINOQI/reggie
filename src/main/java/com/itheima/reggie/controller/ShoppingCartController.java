package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.add(shoppingCart);
    }

    /**
     * 获取购物车内商品的集合
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart(){

        return R.success(shoppingCartService.listShoppingCart());
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        return shoppingCartService.clear();
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map<String,String> params){
        Long id = Long.parseLong(params.get("dishId"));
        if (id == null) {
            return R.error("参数错误");
        }
        shoppingCartService.removeDish(id);
        return R.success("清除商品成功");
    }
}
