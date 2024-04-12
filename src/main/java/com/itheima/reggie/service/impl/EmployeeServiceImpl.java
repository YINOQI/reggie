package com.itheima.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.EmployeeDto;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.itheima.reggie.utils.RedisConstants.LOGIN_USER_KEY;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R<EmployeeDto> login(Employee employee) {
        //1.将页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.获取数据库中用户数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = this.getOne(queryWrapper);

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

        String token = UUID.randomUUID(true).toString();


        EmployeeDto employeeDto = BeanUtil.copyProperties(emp, EmployeeDto.class);
        employeeDto.setToken(token);

        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(employeeDto,new HashMap<>()
                ,CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));



        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token,stringObjectMap);

        return R.success(employeeDto);
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return R.success("退出成功");
    }

    @Override
    public R<Page<Employee>> getEmployeePage(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Employee> employeePage = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        return R.success(this.page(employeePage,wrapper));
    }

    @Override
    public R<String> saveEmployee(Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        this.save(employee);

        return R.success("新增员工成功");
    }
}
