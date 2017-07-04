package com.mmall.dao;

import com.mmall.pojo.Order;
import com.mysql.jdbc.ConnectionPropertiesImpl;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order getByUseridAndOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);

    List<Order> getByListByUserid(@Param("userId") Integer userId);

    List<Order> getAllList();

    List<Order> getListBySerchNo(@Param("searchNo") Long searchNo);

    Order getByOrderNo(@Param("orderNo")Long orderNo);
}