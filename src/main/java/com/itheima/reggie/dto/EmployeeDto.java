package com.itheima.reggie.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String token;
}
