package com.itheima.reggie.mapper;

import com.itheima.reggie.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ScheduledMapper {

    Voucher select(@Param("id") Long id);
    void statusUpdate();
}
