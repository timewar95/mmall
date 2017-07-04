package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ztian on 2017/6/27.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping)
    {
        if(userId==null||shipping==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        //设置shipping的userid属性
        shipping.setUserId(userId);
        int resultConut = shippingMapper.insert(shipping);
        if(resultConut>0){
            HashMap<String,Object> map = Maps.newHashMap();
            map.put("id",shipping.getId());
            return ServerResponse.createBySuccess("新增地址成功",map);
        }
        return ServerResponse.createByErrorMessage("新增地址失败");
    }

    public ServerResponse delete(Integer shippingId, Integer userId)
    {
        if(userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        int resultConut = shippingMapper.deleteByUserid(shippingId,userId);
        if(resultConut>0){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer userId,Shipping shipping)
    {
        if(userId==null||shipping==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        //根据用户名和shippingid更新shipping表
        shipping.setUserId(userId);
        int resultConut = shippingMapper.updateByShipping(shipping);
        if(resultConut>0){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse selectOne(Integer shippingId, Integer userId)
    {
        if(userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByShippingIdAndUserid(shippingId, userId);
        if(shipping!=null){
            return ServerResponse.createBySuccess(shipping);
        }
        return ServerResponse.createByErrorMessage("查看详细地址失败");
    }

    public ServerResponse list(Integer userId,Integer pageNum, Integer pageSize)
    {
        if(userId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectShippingListByUserid(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
