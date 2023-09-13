package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestControllerAdvice.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 添加异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info(exception.getMessage());

        if (exception.getMessage().contains("Duplicate entry")){
            String[] s = exception.getMessage().split(" ");
            return R.error(s[2]+"已存在");
        }

        return R.error("未知错误");
    }


    /**
     * 业务异常处理
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.info(exception.getMessage());

        return R.error(exception.getMessage());
    }
}
