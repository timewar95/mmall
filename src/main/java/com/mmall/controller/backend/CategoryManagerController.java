package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by ztian on 2017/6/5.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryservice;

    @RequestMapping(value = "/add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCatergory(HttpSession session, String categoryName, @RequestParam(value ="parentId",defaultValue = "0") int parentId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要先进行登录操作");
        }
        //判断用户是否为管理员
        if(iUserService.checkAdmin(user).isSuccess())
        {
            return iCategoryservice.addCategory(categoryName,parentId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行添加品类操作");
    }

    @RequestMapping(value = "/update_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateCategory(HttpSession session, String categoryName,int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要先进行登录操作");
        }
        //判断用户是否为管理员
        if(iUserService.checkAdmin(user).isSuccess())
        {
            return iCategoryservice.updateCategory(categoryName,categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/6 22:59
     *   @Comment 获得下一级所有平级的子节点
     */
    @RequestMapping(value = "/get_children_of_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getParallelChildrenCategory(HttpSession session, @RequestParam(value = "categoryId" ,defaultValue = "0") int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要先进行登录操作");
        }
        //判断用户是否为管理员
        if(iUserService.checkAdmin(user).isSuccess())
        {
            return iCategoryservice.getParallelChildrenCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }
    
    /**
     *   @Auchor ztian
     *   @Date 2017/6/6 23:00
     *   @Comment 根据categoryId获取这个子节点及这个子节点下的所有子节点
     */
    @RequestMapping(value = "/get_category_and_children.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndChildren(HttpSession session, @RequestParam(value = "categoryId" ,defaultValue = "0") int categoryId)
    {
        //判断用户是否已登录
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,需要先进行登录操作");
        }
        //判断用户是否为管理员
        if(iUserService.checkAdmin(user).isSuccess())
        {
            return iCategoryservice.getCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限进行品类更新操作");
    }


}
