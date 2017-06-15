package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

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
        //要判断的type字段非空(空格不算字符)才进行判断
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type))
        {
            //开始校验
            if(Const.USERNAME.equals(type)) {
                int resultcount=userMapper.checekUserName(str);
                if(resultcount>0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
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

    public ServerResponse<String> selectQuestion(String username)
    {
        ServerResponse<String> validResponse=this.checkVaild(username,Const.USERNAME);
        //校验成功 即用户在数据库中不存在
        if(validResponse.isSuccess())
        {
            return ServerResponse.createByErrorMessage("不存在该用户");
        }
        String question=userMapper.selectQuestionByUserName(username);
        return ServerResponse.createBySuccess(question);
    }


    public ServerResponse<String> checkAnswer(String username,String question,String answer)
    {
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0)
        {
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("用户问题答案不正确");
    }

    public ServerResponse<String> forgetPasswordReset(String username,String passwordNew,String forgetToken)
    {
        //检测forgettoken是否为空 只包含空格也是为空
        if(StringUtils.isBlank(forgetToken)) {
            return  ServerResponse.createByErrorMessage("参数错误,需要传递token");
        }
        //判断用户是否存在
        ServerResponse validResponse=this.checkVaild(username,Const.USERNAME);
        if(validResponse.isSuccess()) {
            //通过校验 则用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        //判断toiken是否失效或已过期
        String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }

        //判断用户传入的token和本地缓存的token是否一致
        if(StringUtils.equals(token,forgetToken)) {
            String MD5Password=MD5Util.MD5EncodeUtf8(passwordNew);
           int resultCout=userMapper.updatePasswordByUsername(username,MD5Password);
            if(resultCout>0)
            {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return  ServerResponse.createByErrorMessage("Token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码错误");
    }

    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew){
        String MD5Password=MD5Util.MD5EncodeUtf8(passwordOld);
        int resultCount=userMapper.checkPassword(MD5Password,user.getId());
        if(resultCount==0)
        {
            return ServerResponse.createByErrorMessage("用户旧密码错误");
        }
        ///旧密码正确
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateResult=userMapper.updateByPrimaryKeySelective(user);
        if(updateResult>0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败,");
    }

    public ServerResponse<User> updateInformation(User user)
    {
        //根据更新的uid和email 确保更新的后email也在数据库中唯一
        int resultCount=userMapper.checkEmailByUid(user.getEmail(),user.getId());
        if(resultCount>0)
        {
            return ServerResponse.createByErrorMessage("要更新的email已存在,请更改要更新的邮件后再尝试");
        }

        //username不能被更新,新建User对象
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setUpdateTime(new Date());
        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0)
        {
            return ServerResponse.createBySuccess("个人信息更新成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("个人信息更新失败");
    }

    public ServerResponse<User> getInformation(Integer uid)
    {
        User user=userMapper.selectByPrimaryKey(uid);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        //设置用户的密码为空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    //backend
    public ServerResponse<String> checkAdmin(User user)
    {
        if(user.getRole().intValue()==Const.ROLE.ROLE_ADMIN)
        {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
