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
        //使用校验功能校验用户名和邮箱是否在数据库中已存在，参数一为检测的字段的字符，参数二要检测的字段
        ServerResponse<String> checkResponse=checkVaild(user.getUsername(),Const.USERNAME);
        //校验不成功，即用户名在数据库中已存在，直接返回ServerResponse
        if(!checkResponse.isSuccess())
        {
            return checkResponse;
        }
        checkResponse=checkVaild(user.getEmail(),Const.EMAIL);
        if(!checkResponse.isSuccess())
        {
            return checkResponse;
        }


        //设置用户角色
        user.setRole(Const.ROLE.ROLE_CUSTOMER);
        //用户密码进行md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        //向数据库插入用户
        int resultcount=userMapper.insert(user);
        if(resultcount==0)
        {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    //参数一:要判断的字符数据  参数二:要判断的字段
    public ServerResponse<String> checkVaild(String str, String type){
        //要判断的字段非空才进行判断
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type))
        {
            //开始校验
            if(Const.EMAIL.equals(type)) {
                int resultcount=userMapper.checekUserName(str);
                if(resultcount>0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }

            if(Const.USERNAME.equals(type)){
                int resultcount=userMapper.checekEmail(str);
                if(resultcount>0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }

        return ServerResponse.createBySuccessMessage("校验成功");
    }
}
