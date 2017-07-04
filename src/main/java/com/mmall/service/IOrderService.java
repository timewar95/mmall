package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * Created by ztian on 2017/6/28.
 */
public interface IOrderService {
    public ServerResponse pay(Integer userId, Long orderNo, String path);
    public ServerResponse alipayCallback(Map<String,String> params);
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);
    public ServerResponse createOrder(Integer userId,Integer shippingId);
    public ServerResponse cancelOrder(Integer userId,Long orderNo);
    public ServerResponse getCartProductVo(Integer userId);
    public ServerResponse orderDetail(Integer userId,Long orderNo);
    public ServerResponse orderList(int pageNum,int pageSize,Integer userId);

    //backend
    public ServerResponse manageOrderDetail(Long orderNo);
    public ServerResponse manageOrderList(int pageNum,int pageSize);
    public ServerResponse manageOrderSearch(int pageNum,int pageSize,Long searchNo);
    public ServerResponse manageSendGoods(Long orderNo);
}
