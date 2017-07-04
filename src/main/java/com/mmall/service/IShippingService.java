package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

/**
 * Created by ztian on 2017/6/27.
 */
public interface IShippingService {
    public ServerResponse add(Integer userId, Shipping shipping);
    public ServerResponse delete(Integer shippingId, Integer userId);
    public ServerResponse update(Integer userId,Shipping shipping);
    public ServerResponse selectOne(Integer shippingId, Integer userId);
    public ServerResponse list(Integer userId,Integer pageNum, Integer pageSize);
}
