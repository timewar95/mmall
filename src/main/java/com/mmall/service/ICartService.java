package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

/**
 * Created by ztian on 2017/6/20.
 */
public interface ICartService {
    public ServerResponse add(Integer userId, Integer produdctId, Integer count);
    public ServerResponse<CartVo> update(Integer userId, Integer produdctId, Integer count);
    public ServerResponse<CartVo> deleteByIds(Integer userId,String productIds);
    public ServerResponse<CartVo> list(Integer userId);
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);
    public ServerResponse<Integer> selectCartProductCount(Integer userId);

}
