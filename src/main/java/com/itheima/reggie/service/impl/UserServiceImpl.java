package com.itheima.reggie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.UserDto;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.itheima.reggie.utils.RedisConstants.*;
import static com.itheima.reggie.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public R<String> sendMsg(User user) {
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {

            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);

//            SMSUtils.sendMessage("瑞吉外卖",phone,code);

//            session.setAttribute(phone, code);
            stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + user.getPhone(),code,LOGIN_CODE_TTL, TimeUnit.MINUTES);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    @Override
    public R<String> login(Map map) {
        //获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //获取session中的验证码
//        Object attribute = session.getAttribute(phone);
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if (cacheCode != null && cacheCode.equals(code)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);

            User user = this.getOne(wrapper);

            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
                user.setStatus(1);
                this.save(user);
            }

            String token = UUID.randomUUID(true).toString();
//            session.setAttribute("user",user.getId());
            UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
            Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

            stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
            stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
            return R.success(token);
        }

        return R.error("验证码错误");
    }

    @Override
    public R<String> loginout(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return R.success("退出成功");
    }
}
