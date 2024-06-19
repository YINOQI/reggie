package com.itheima.reggie.dto;

import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Integer number;

    private Long dishId;

    private Long setmealId;
}
