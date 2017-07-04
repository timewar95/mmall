package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserid(@Param("id") Integer id,@Param("userId") Integer userId);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    Shipping selectByShippingIdAndUserid(@Param("shippingId") Integer shippingId,@Param("userId") Integer userId);

    List<Shipping> selectShippingListByUserid(@Param("userId") Integer userId);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int updateByShipping(Shipping record);
}