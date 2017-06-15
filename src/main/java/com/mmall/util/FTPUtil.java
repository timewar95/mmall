package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ztian on 2017/6/15.
 */
public class FTPUtil {
    private static String ftpIp=PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass=PropertiesUtil.getProperty("ftp.pass");
    private static Logger logger= LoggerFactory.getLogger(FTPUtil.class);


    public static boolean upload(List<File> fileList) throws IOException {
        //ftp服务器地址 连接ftp服务器用户名 连接ftp服务器密码 ftp服务器端口
        FTPUtil ftpUtil=new FTPUtil(ftpIp,ftpUser,ftpPass,21);
        logger.info("开始连接Ftp服务器上传文件");
        boolean result=ftpUtil.upload(fileList,"img");
        logger.info("上传文件到Ftp服务器完成,上传结果{}",result);
        return result;
    }

    //remotePath 上传到Ftp服务器哪个文件夹下
    public boolean upload(List<File> fileList,String remotePath) throws IOException {
        FileInputStream fis=null;
        //是否上传成功
        boolean isSuccess=false;
        if(connectServer(this.ip,this.user,this.userPass,this.port))
        {
            try {
                //设置上传目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置上传缓存大小
                ftpClient.setBufferSize(1024);
                //设置上传文件格式
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //设置上传文件控制编码
                ftpClient.setControlEncoding("UTF-8");
                //ftp服务器服务为被动连接模式
                ftpClient.enterLocalPassiveMode();

                //开始上传文件到ftp服务器
                for(File fileItem:fileList) {
                    fis=new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                isSuccess=true;
            } catch (IOException e) {
                logger.error("文件上传到Ftp服务器失败");
            }finally{
                fis.close();
                ftpClient.disconnect();
            }
        }
        return isSuccess;
    }

    //登录Ftp服务器
    public boolean connectServer(String ip,String user,String userPass,int port){
        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess=ftpClient.login(user,userPass);
        } catch (IOException e) {
            logger.error("登录Ftp服务器失败",e);
        }
        return isSuccess;
    }

    public FTPUtil(String ip, String user, String userPass, int port) {
        this.ip = ip;
        this.user = user;
        this.userPass = userPass;
        this.port = port;
    }
    private String ip;
    private String user;
    private String userPass;
    private int port;
    private FTPClient ftpClient;
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
