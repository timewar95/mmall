package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ztian on 2017/6/20.
 */
@Controller
@RequestMapping("/cart/")
public class CartControl {

    @Autowired
    private ICartService iCartService;

    //新增购物车项
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session,Integer productId,Integer count)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.add(user.getId(),productId,count);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //更新购物车项
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session,Integer productId,Integer count)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.update(user.getId(),productId,count);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //删除购物车项的方法
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse deleteByIds(HttpSession session,String productIds)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.deleteByIds(user.getId(),productIds);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //显示购物车列表的方法
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.list(user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //购物车项全选方法
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.selectOrUnSelect(user.getId(),null, Const.Cart.CHECKED);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //购物车项全不选方法
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.selectOrUnSelect(user.getId(),null, Const.Cart.UN_CHECKED);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //购物车项单选方法
    @RequestMapping("select_one.do")
    @ResponseBody
    public ServerResponse selectOne(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //购物车项单项不选方法
    @RequestMapping("un_select_one.do")
    @ResponseBody
    public ServerResponse unSelectOne(HttpSession session,Integer productId)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }


    //查询购物车中购物车项的总数量 并且不按类别来计算
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartPoructCount(HttpSession session)
    {
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null)
        {
            //业务开始，调用Service层的方法
            return iCartService.selectCartProductCount(user.getId());
        }
        //用户未登录 数量返回0
        return ServerResponse.createBySuccess(0);
    }

}
