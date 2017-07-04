package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ztian on 2017/6/27.
 */
@Controller
@RequestMapping("/shipping")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;

    @ResponseBody
    @RequestMapping(value = "/add.do",method = RequestMethod.POST)
    public ServerResponse add(HttpSession session, Shipping shipping){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            //添加地址的方法
            return iShippingService.add(user.getId(),shipping);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @ResponseBody
    @RequestMapping(value = "/delete.do",method =RequestMethod.POST)
    public ServerResponse delete(HttpSession session,Integer shippingId){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            //删除地址的方法
             return iShippingService.delete(shippingId,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @ResponseBody
    @RequestMapping(value = "/update.do",method = RequestMethod.POST)
    public ServerResponse update(HttpSession session,Shipping shipping){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            //更新地址的方法
            return iShippingService.update(user.getId(),shipping);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //查看地址详情
    @ResponseBody
    @RequestMapping(value = "/select.do",method = RequestMethod.POST)
    public ServerResponse select(HttpSession session,Integer shippingId){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            //更新地址的方法
            return iShippingService.selectOne(shippingId,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //查看地址列表
    @ResponseBody
    @RequestMapping(value = "/list.do",method = RequestMethod.POST)
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum",defaultValue ="1") Integer pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null) {
            //获得地址列表地址的方法
            return iShippingService.list(user.getId(),pageNum,pageSize);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
}
