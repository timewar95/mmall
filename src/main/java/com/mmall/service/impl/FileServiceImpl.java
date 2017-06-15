package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by ztian on 2017/6/15.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{
    //先上传文件到tomcat服务器 返回上传文件的文件名
    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        String  fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName= UUID.randomUUID()+"."+fileExtensionName;

        File dir=new File(path);
        //若上传的文件目录不存在，新建上传文件目录
        if(!dir.exists()){
            dir.mkdirs();
        }
        File targetFile=new File(dir,uploadFileName);
        try {
            //上传文件到tomcat目录
            file.transferTo(targetFile);
            //上传文件到Ftp服务器
            FTPUtil.upload(Lists.<File>newArrayList(targetFile));
            //上传文件到Ftp服务器完成后 删除tomcat目录的上传文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件失败",e);
            return null;
        }
        return targetFile.getName();
    }


}
