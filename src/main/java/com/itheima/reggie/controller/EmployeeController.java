package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.EmployeeDto;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * 员工信息操作
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<EmployeeDto> login(@RequestBody Employee employee) {
        return employeeService.login(employee);
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        return employeeService.logout(request);
    }

    /**
     * 添加员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {

        return employeeService.saveEmployee(employee);
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        return employeeService.employeePage(page, pageSize, name);
    }

    /**
     * 更新员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

//        employee.setUpdateUser((long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("信息更新成功");
    }

    /**
     * 获取员工id
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);

        if (employee != null) {
            return R.success(employee);
        }

        return R.error("没有该员工信息");
    }
}
