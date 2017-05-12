package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by ztian on 2017/5/12.
 */
@Controller
@RequestMapping("/user/")
public class UserControl {

    @Autowired
    private IUserService iUserService;
    /**
     *   @Auchor ztian
     *   @Date 2017/5/12 18:05
     *   @Comment 登录功能
     */
    @RequestMapping("login.do")
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session)
    {

        ServerResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess())
        {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
}
