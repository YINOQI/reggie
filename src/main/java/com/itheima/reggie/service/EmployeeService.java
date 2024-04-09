package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.EmployeeDto;
import com.itheima.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    R<EmployeeDto> login(Employee employee);

    R<String> logout(HttpServletRequest request);

    R<Page<Employee>> employeePage(int page, int pageSize, String name);

    R<String> saveEmployee(Employee employee);

}
