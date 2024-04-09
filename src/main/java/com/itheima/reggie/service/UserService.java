package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService extends IService<User> {
    R<String> sendMsg(User user);

    R<String> login(Map map);

    R<String> loginout(HttpServletRequest request);
}
