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
}
