package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by ztian on 2017/5/12.
 */
public interface IUserService {
    public ServerResponse<User> login(String username, String password);
    public ServerResponse<String> register(User user);
    public ServerResponse<String> checkVaild(String str,String type);
    public ServerResponse<String> selectQuestion(String username);
    public ServerResponse<String> checkAnswer(String username,String question,String answer);
    public ServerResponse<String> forgetPasswordReset(String username,String passwordNew,String forgetToken);
    public ServerResponse<String> resetPassword(User user,String passwordOld,String passwordNew);
    public ServerResponse<User> updateInformation(User user);
    public ServerResponse<User> getInformation(Integer uid);
    public ServerResponse<String> checkAdmin(User user);
}
