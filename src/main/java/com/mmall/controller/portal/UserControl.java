package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session)
    {

        ServerResponse<User> response=iUserService.login(username,password);
        //登录成功返回的serverResponse的isSuccess为true 此时设置用户登录信息到session域中
        if(response.isSuccess())
        {
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:17
     *   @Comment 用户登出功能
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session)
    {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }
    
    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:17
     *   @Comment 用户注册功能
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user)
    {

        return iUserService.register(user);
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:18
     *   @Comment 用户数据校验功能 username email在数据库中不存在时校验成功
     */
    @RequestMapping(value = "check_valid.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkVaild(String str,String type)
    {
        return iUserService.checkVaild(str,type);
    }


    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:18
     *   @Comment 用户个人信息获取
     */
    @RequestMapping(value = "get_user_info.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session)
    {
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser!=null)
        {
            return ServerResponse.createBySuccess(currentUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录,请先登录");
    }
    
    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:27
     *   @Comment 用户忘记密码问题获取
     */
    @RequestMapping(value = "forget_get_question.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username)
    {
        return iUserService.selectQuestion(username);
    }
    
    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 20:56
     *   @Comment 检测用户忘记密码答案
     */
    @RequestMapping(value = "forget_check_answer.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    
    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 21:42
     *   @Comment 忘记问题下的重置用户密码
     */
    @RequestMapping(value = "forget_reset_password.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetPasswordReset(username,passwordNew,forgetToken);
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/3 21:42
     *   @Comment 登录状态重置用户密码
     */
    @RequestMapping(value = "reset_password.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录,请先进行登录");
        }
        return iUserService.resetPassword(currentUser,passwordOld,passwordNew);
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/4 21:44
     *   @Comment 用户更新个人信息功能
     */
    @RequestMapping(value = "update_information.do",method =RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null)
        {
            return ServerResponse.createByErrorMessage("用户未登录,请先进行登录");
        }

        //更加登录用户设置要更新的用户Id
        user.setId(currentUser.getId());
        ServerResponse<User> response=iUserService.updateInformation(user);

        //个人信息更新成功的话 issuccess返回true
        if(response.isSuccess())
        {
            //更新sesssion中的CurentUser信息
            currentUser.setPhone(response.getData().getPhone());
            currentUser.setEmail(response.getData().getEmail());
            currentUser.setQuestion(response.getData().getQuestion());
            currentUser.setAnswer(response.getData().getAnswer());
            session.setAttribute(Const.CURRENT_USER,currentUser);
        }
        return response;
    }

}
