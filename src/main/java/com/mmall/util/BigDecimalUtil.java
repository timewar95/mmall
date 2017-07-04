package com.mmall.util;

import java.math.BigDecimal;

/**
 * Created by ztian on 2017/6/20.
 */
public class BigDecimalUtil {
    public static BigDecimal add(Double value1,Double value2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.add(b2);
    }
    public static BigDecimal sub(Double value1,Double value2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.subtract(b2);
    }
    public static BigDecimal mul(Double value1,Double value2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        return b1.multiply(b2);
    }
    public static BigDecimal div(Double value1,Double value2)
    {
        BigDecimal b1=new BigDecimal(Double.toString(value1));
        BigDecimal b2=new BigDecimal(Double.toString(value2));
        //除不尽的情况用四舍五入的方法保留两位小数
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);
    }
}
