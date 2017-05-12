package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by ztian on 2017/5/12.
 */
public interface IUserService {
    public ServerResponse<User> login(String username, String password);
    public ServerResponse<String> register(User user);
}
