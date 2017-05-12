package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Created by ztian on 2017/5/12.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount=userMapper.checekUserName(username);
        if(resultCount == 0)
        {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }


        //// TODO: 2017/5/12 密码md5加密
        String md5password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5password);
        if(user==null)
        {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        int resultcount=userMapper.checekUserName(user.getUsername());
        if(resultcount>0)
        {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }

        resultcount=userMapper.checekEmail(user.getEmail());
        if(resultcount>0)
        {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }

        //设置用户角色
        user.setRole(Const.ROLE.ROLE_CUSTOMER);
        //用户密码进行md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //向数据库插入用户
        resultcount=userMapper.insert(user);
        if(resultcount==0)
        {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }
}
