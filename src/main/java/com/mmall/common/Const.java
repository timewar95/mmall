package com.mmall.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by ztian on 2017/5/12.
 */
public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String EMAIL="eamil";
    public static final String USERNAME="username";
    public interface ROLE{
        int ROLE_CUSTOMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_DESC_ASC= Sets.newHashSet("price_desc", "price_asc");
    }
    public interface Cart{
        int CHECKED=1;//购车车项被选中
        int UN_CHECKED=0;//购物车项未被选中
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"商品正常上架");
        private int status;
        private String value;
        ProductStatusEnum(int status, String value) {
            this.status = status;
            this.value = value;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public enum OrderStatusEnum{
        CANCELED(0,"取消付款"),
        NO_PAY(10,"未付款"),
        PAY(20,"已付款"),
        SHIPPED(30,"已发货"),
        ORDER_SUCCESS(40,"交易成功"),
        ORDER_CLOSE(50,"交易关闭");
        private int code;
        private String desc;

        OrderStatusEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }
        public void setDesc(String desc) {
            this.desc = desc;
        }
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }

        public static String codeOfDesc(int code){
            for(OrderStatusEnum orderStatusEnum:values()){
                if(orderStatusEnum.getCode()==code){
                    return orderStatusEnum.getDesc();
                }
            }
            throw new RuntimeException("没有找到订单状态码对应的描述");
        }
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");
        private int code;
        private String desc;
        PayPlatformEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }
        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
    }


    public enum PayTypeEnum{
        PAY_ONLINE(1,"在线支付");
        private int code;
        private String desc;
        PayTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }
        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }

        public static String codeOfDesc(int code){
            for(PayTypeEnum payTypeEnum:values()){
                if(code==payTypeEnum.getCode()){
                    return payTypeEnum.getDesc();
                }
            }
            throw new RuntimeException("没有找到支付类型状态码对应的描述");
        }
    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS="TRADE_SUCCESS";
        String RESPONSE_SUCCESS="success";
        String RESPONSE_FAILED="failed";
    }
}
