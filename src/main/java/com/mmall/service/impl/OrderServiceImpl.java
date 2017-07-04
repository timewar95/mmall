package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.ICartService;
import com.mmall.service.IOrderService;
import com.mmall.service.IProductService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import com.sun.deploy.panel.ITreeNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by ztian on 2017/6/28.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService{
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    private static final Logger logger= LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse pay(Integer userId,Long orderNo,String path) {
        if (userId == null || orderNo == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(), ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }

        //根据用户id和orderNo查出order对象
        Order order = orderMapper.getByUseridAndOrderNo(userId, orderNo);
        if(order==null) {
            return ServerResponse.createByErrorMessage("没有要付款的该订单");
        }

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo =orderNo.toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall扫码支付,订单号").append(orderNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount =order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单号").
                append(orderNo).append("购买商品共").append(order.getPayment()).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        //oderItem转成GoodDetail
        List<OrderItem> orderItemList = orderItemMapper.getListByUserIdAndOrderNo(userId, orderNo);
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        for(OrderItem item:orderItemList){
            //orderItem下商品的单价的单位由元转换成分
            long price= BigDecimalUtil.mul(item.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue();
            GoodsDetail good = GoodsDetail.newInstance(item.getOrderNo().toString(),item.getProductName(),price,item.getQuantity());
            goodsDetailList.add(good);
        }

        /*
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);*/


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        //创建交易服务
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        //根据请求参数bulider获得请求结果
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                //判断upload文件夹是否存在
                File file = new File(path);
                if (!file.exists()) {
                    file.setWritable(true);
                    file.mkdirs();
                }
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                //快速打印response信息
                dumpResponse(response);
                // 需要修改为运行机器上的路径
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());
                String qrFileName=String.format("qr-%s.png", response.getOutTradeNo());
                logger.info("filePath:" + qrPath);
                //上传二维码图片到qrpath路径下
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                //上传二维码图片到ftp服务器上
                File targetFile =new File(path,qrFileName);
                try {
                    FTPUtil.upload(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码到图片服务器失败",e);
                    return ServerResponse.createByErrorMessage("付款二维码获取失败，请重新获取");
                }
                String qrUrl=new StringBuilder().append(PropertiesUtil.getProperty("ftp.server.http.prefix")).append(qrFileName).toString();
                Map map= Maps.newHashMap();
                map.put("orderNo",orderNo);
                map.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(map);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    public ServerResponse alipayCallback(Map<String,String> params){
        //商城订单号
        Long orderNo=Long.parseLong(params.get("out_trade_no"));
        //支付宝交易状态
        String trade_status=params.get("trade_status");
        //支付宝交易号
        String trade_no=params.get("trade_no");
        //支付宝交易完成时间
        String gmt_payment=params.get("gmt_payment");

        Order order=orderMapper.getByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("非mmall商城的订单");
        }
        //判断订单状态 并且要避免支付宝重复请求接口改变订单状态
        //当订单状态大于20则认为支付成功 支付包调用接口时直接返回success
        if(order.getStatus()>= Const.OrderStatusEnum.PAY.getCode()){
            return ServerResponse.createBySuccess();
        }
        //订单状态小于20 则订单未付款 检查支付宝交易状态来确定是否更改订单交易状态
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(trade_status)){
            //更新订单付款时间 订单状态
            order.setStatus(Const.OrderStatusEnum.PAY.getCode());
            order.setPaymentTime(DateTimeUtil.strToDate(gmt_payment));
            try{
                orderMapper.updateByPrimaryKeySelective(order);
            }catch (Exception e){
                logger.error("更新订单错误",e);
            }
        }
        //插入payinfo信息
        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformStatus(trade_status);
        payInfo.setPlatformNumber(trade_no);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo){
        //参数错误
        if(userId==null||orderNo==null){
            return ServerResponse.createByError();
        }
        Order order = orderMapper.getByUseridAndOrderNo(userId, orderNo);
        //查询不到订单
        if(order==null){
            return ServerResponse.createByError();
        }
        //订单已付款 返回success
        if(order.getStatus()>=Const.OrderStatusEnum.PAY.getCode()){
            return ServerResponse.createBySuccess();
        }
        //订单未付款
        return ServerResponse.createByError();
    }

    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }



    public ServerResponse createOrder(Integer userId,Integer shippingId){
        if(userId==null||shippingId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        //根据userId获得被选中的cartList
        List<Cart> cartList = cartMapper.selectCheckedListByUserId(userId);
        //根据cartList获得OrderItemList
        ServerResponse response = this.getOrderItemList(userId,cartList);
        //若得到失败状态的response 直接返回这个ServerResponse
        if(!response.isSuccess()){
            return response;
        }
        List<OrderItem> orderItemList=(List<OrderItem>)response.getData();
        //根据orderItemList获得订单总价钱
        BigDecimal orderTotalPrice=this.getOrderTotalPrice(orderItemList);
        //根据userId,shippingId,orderTotalPrice创建新的订单
        //返回新建的订单项
        Order order = this.assembleOrder(userId, shippingId, orderTotalPrice);
        if(order==null){
            return ServerResponse.createByErrorMessage("数据库新建订单失败，请联系管理员");
        }
        //向数据库插入order成功后 向数据库插入orderItem
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入多条orderItem数据
        orderItemMapper.batchInsert(orderItemList);
        //插入完成后 减少商品库存
        this.reduceProductStock(orderItemList);
        //插入完成后 清空购物车
        this.clearCart(cartList);
        //获取OrderVo对象
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse cancelOrder(Integer userId,Long orderNo){
        if(userId==null||orderNo==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Order order = orderMapper.getByUseridAndOrderNo(userId, orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("没有找到用户订单,不能取消");
        }
        if(order.getStatus()==Const.OrderStatusEnum.CANCELED.getCode()){
            return ServerResponse.createByErrorMessage("用户订单已取消,请不要重复取消");
        }
        if(order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("用户订单已付款,不能取消");
        }
        //若上述if都通过 则更新订单状态为已取消
        Order updateOrder=new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(resultCount>0){
            return ServerResponse.createBySuccess("取消订单成功");
        }
        return ServerResponse.createByErrorMessage("状态异常，取消订单失败");
    }
    public ServerResponse getCartProductVo(Integer userId){
        List<Cart> cartList = cartMapper.selectListByUserId(userId);
        ServerResponse<List<OrderItem>> resposne = this.getOrderItemList(userId, cartList);
        if(!resposne.isSuccess()){
            return resposne;
        }
        //resposne.isSuccess()为true 则能获取orderItemList
        //组装OrderProductVo
        List<OrderItem> orderItemList=resposne.getData();
        OrderProductVo orderProductVo=new OrderProductVo();
        //设置imageHose
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        //设置totalPirce
        BigDecimal totalPrice=this.getOrderTotalPrice(orderItemList);
        orderProductVo.setProductTotalPrice(totalPrice);
        //设置OrderItemVoList
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVo orderItemVo = this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        return ServerResponse.createBySuccess(orderProductVo);
    }

    public ServerResponse orderDetail(Integer userId,Long orderNo){
        if(userId==null||orderNo==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Order order = orderMapper.getByUseridAndOrderNo(userId, orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("用户不存在该订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.getListByUserIdAndOrderNo(userId, orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse orderList(int pageNum,int pageSize,Integer userId){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getByListByUserid(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, userId);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //管理员查看订单详细 不需要userId
    public ServerResponse manageOrderDetail(Long orderNo){
        if(orderNo==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Order order = orderMapper.getByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("不存在该订单");
        }
        List<OrderItem> orderItemList = orderItemMapper.getListByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse manageOrderList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getAllList();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse manageOrderSearch(int pageNum,int pageSize,Long searchNo){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.getListBySerchNo(searchNo);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo pageInfo=new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public ServerResponse manageSendGoods(Long orderNo){
        Order order = orderMapper.getByOrderNo(orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("要发货的订单不存在");
        }
        if(order.getStatus()==Const.OrderStatusEnum.PAY.getCode()){
            order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
            order.setSendTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccess("订单发货成功");
        }
        return ServerResponse.createByErrorMessage("订单状态不符合发货要求,请检查订单状态");


    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList=Lists.newArrayList();
        //orderList为空的话直接返回
        if(CollectionUtils.isEmpty(orderList)){
            return orderVoList;
        }
        for(Order order:orderList)
        {
            //管理员不需要传userId也能获取orderItemList
            if(userId==null){
                List<OrderItem> orderItemList = orderItemMapper.getListByUserId(userId);
                OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
                orderVoList.add(orderVo);
            }else{
                List<OrderItem> orderItemList = orderItemMapper.getListByUserIdAndOrderNo(userId, order.getOrderNo());
                OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
                orderVoList.add(orderVo);
            }
        }
        return orderVoList;
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){
        OrderVo orderVo=new OrderVo();
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PayTypeEnum.codeOfDesc(order.getPaymentType()));
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOfDesc(order.getStatus()));
        orderVo.setPostage(order.getPostage());
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        //设置orderItemVoList
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemVo orderItemVo = this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);

        //设置shippingVo
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping!=null){
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(this.assembleShippingVo(shipping));
        }
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo=new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo=new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal orderTotalPirce=new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            orderTotalPirce=BigDecimalUtil.add(orderTotalPirce.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return orderTotalPirce;
    }

    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }
    //清空购物车
    private void clearCart(List<Cart> cartList){
         String ids="";
         for(int i=0;i<cartList.size();i++){
             ids+=(i==cartList.size()-1?cartList.get(i).getId():cartList.get(i).getId()+",");
         }
        cartMapper.deleteByIds(ids);
    }
    private ServerResponse<List<OrderItem>> getOrderItemList(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList=Lists.newArrayList();
        //根据userId获得被选中的cartList
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空,不能创建订单");
        }
        for (Cart cartItem:cartList){
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(Const.ProductStatusEnum.ON_SALE.getStatus()!=product.getStatus()){
                return ServerResponse.createByErrorMessage("下单的商品中有已下架的商品,不能创建订单");
            }
            if(cartItem.getQuantity()>product.getStock()){
                return ServerResponse.createByErrorMessage("上单的商品超过商品库存,不能创建订单");
            }
            OrderItem orderItem=new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity().doubleValue()));
            //把orderItem加入orderList
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
        Order order=new Order();
        order.setOrderNo(this.generatorOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(orderTotalPrice);
        order.setPaymentType(Const.PayTypeEnum.PAY_ONLINE.getCode());
        //订单邮费 刚开始设置为0
        order.setPostage(0);
        //刚开始为未付款
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        int resultCount = orderMapper.insert(order);
        //插入成功 return Order 否则返回null
        if(resultCount>0){
            return order;
        }
        return null;
    }

    private long generatorOrderNo(){
        return System.currentTimeMillis()+new Random().nextInt(100);
    }
}

