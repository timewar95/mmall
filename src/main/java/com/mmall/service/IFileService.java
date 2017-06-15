package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ztian on 2017/6/15.
 */
public interface IFileService {
    public String upload(MultipartFile file, String path);
}
