package com.mmall.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by ztian on 2017/6/7.
 */
public class PropertiesUtil {
    private static Logger logger= LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties props;
    static {
        String fileName="mmall.properties";
        props=new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            logger.error("mmall.properties配置文件初始化错误",e);
        }
    }
    public static String getProperty(String key){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            return null;
        }
        return value;

    }
    public static String getProperty(String key,String defaultValue){
        String value = props.getProperty(key.trim());
        if(StringUtils.isBlank(value)){
            value=defaultValue;
        }
        return value;
    }

    public static void main(String[] args) {
        String value = PropertiesUtil.getProperty("ftp.server.ip");
        System.out.println(value);

    }
}
