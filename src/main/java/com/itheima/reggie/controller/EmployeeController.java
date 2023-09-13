package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


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
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.获取数据库中用户数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到或者密码错误则返回信息
        if(emp == null){
            return R.error("用户不存在");
        }

        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //4.如果员工状态为禁用，返回员工状态禁用结果
        if(emp.getStatus() == 0){
            return R.error("该员工已被禁用");
        }

        //5.登录成功，将员工ID存入session并返回登陆成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long id = (Long)request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //构造分页构造器
        Page page1 = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(page1,wrapper);


        return R.success(page1);
    }

    /**
     * 更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        employee.setUpdateUser((long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success("信息更新成功");
    }

    /**
     * 获取员工id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        if (employee != null){
            return R.success(employee);
        }

        return R.error("没有该员工信息");
    }
}
