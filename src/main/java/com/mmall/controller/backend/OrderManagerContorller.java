package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ztian on 2017/6/30.
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManagerContorller {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;

    @ResponseBody
    @RequestMapping("/detail.do")
    public ServerResponse orderDetail(HttpSession session,Long orderNo){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iOrderService.manageOrderDetail(orderNo);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权进行操作");
    }

    @ResponseBody
    @RequestMapping("/list.do")
    public ServerResponse orderList(HttpSession session,
                                    @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize",defaultValue="10") Integer pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iOrderService.manageOrderList(pageNum,pageSize);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权进行操作");
    }

    @ResponseBody
    @RequestMapping("/search.do")
    public ServerResponse orderList(HttpSession session,@Param("searchNo") Long searchNo,
                                    @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize",defaultValue="10") Integer pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iOrderService.manageOrderSearch(pageNum,pageSize,searchNo);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权进行操作");
    }

    @ResponseBody
    @RequestMapping("/send_goods.do")
    public ServerResponse sendGoods(HttpSession session,@Param("orderNo") Long orderNo){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iOrderService.manageSendGoods(orderNo);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权进行操作");
    }
}
