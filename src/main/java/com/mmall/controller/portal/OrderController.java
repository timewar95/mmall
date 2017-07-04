package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.IOrderService;
import com.mmall.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.LongLiteral;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ztian on 2017/6/28.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;

    @ResponseBody
    @RequestMapping("/pay.do")
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            String path = request.getServletContext().getRealPath("upload");
            return iOrderService.pay(user.getId(),orderNo,path);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @ResponseBody
    @RequestMapping("/alipay_callback.do")
    public Object alipayCallback(HttpServletRequest request){
        //获得支付宝回到请求参数 并解析成验证支付宝回调信息的格式
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,String> params= Maps.newHashMap();
        for(Iterator iter=parameterMap.keySet().iterator();iter.hasNext();){
            String name =(String) iter.next();
            String[] values = parameterMap.get(name);
            String strValue="";
            for(int i=0;i<values.length;i++){
                strValue+=(i==values.length-1?strValue+values[i]:strValue+values[i]+",");
            }
            params.put(name,strValue);
        }
        logger.info("sign:{},sign_type:{},参数{}",params.get("sign"),params.get("sign_type"),params.toString());
        //初始化支付宝配置文件
        Configs.init("zfbinfo.properties");
        params.remove("sign_type");
        logger.info("sign_type",Configs.getSignType());
        try {
            boolean alipayRsa2Check = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if(!alipayRsa2Check){
                //验证失败,非支付宝调用接口
                return ServerResponse.createByErrorMessage("非法请求,再次非法请求该接口报告网警");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调请求错误",e);
        }
        //验证完支付宝调用信息后 调用此服务 更改订单支付状态 往Payinfo表插入支付宝交易信息
        ServerResponse callback = iOrderService.alipayCallback(params);
        if(callback.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @ResponseBody
    @RequestMapping("/query_order_pay_status.do")
    public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            ServerResponse payStatus = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
            //订单已付款 返回true
            if(payStatus.isSuccess()){
                return ServerResponse.createBySuccess("true");
            }
            return ServerResponse.createBySuccess("false");
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @ResponseBody
    @RequestMapping("/create_order.do")
    public ServerResponse createOrder(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return iOrderService.createOrder(user.getId(),shippingId);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    @ResponseBody
    @RequestMapping("/cancel_order.do")
    public ServerResponse cancelOrder(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return iOrderService.cancelOrder(user.getId(),orderNo);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }

    //获取购物车中选择商品 以OrderProductVo的形式返回
    @ResponseBody
    @RequestMapping("/get_cart_product.do")
    public ServerResponse getCartProduct(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return iOrderService.getCartProductVo(user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
    //查看订单详情 以OrderVo的形式返回
    @ResponseBody
    @RequestMapping("/order_detail.do")
    public ServerResponse orderDetail(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return iOrderService.orderDetail(user.getId(),orderNo);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
    //查看用户订单列表 以List<OrderVo>的形式返回
    @ResponseBody
    @RequestMapping("/order_list.do")
    public ServerResponse orderList(HttpSession session,
                                      @RequestParam(value = "pageNum",defaultValue ="1")Integer pageNum,
                                      @RequestParam(value = "pageSize",defaultValue ="10")Integer pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return iOrderService.orderList(pageNum,pageSize,user.getId());
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
    }
}
