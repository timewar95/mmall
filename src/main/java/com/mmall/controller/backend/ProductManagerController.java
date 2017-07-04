package com.mmall.controller.backend;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ztian on 2017/6/7.
 */
@RequestMapping("/manage/product/")
@Controller
public class ProductManagerController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    /**
     *   @Auchor ztian
     *   @Date 2017/6/7 13:01
     *   @Comment 后台新增或更新商品
     */
    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product)
    {
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iProductService.saveOrUpdateProduct(product);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权添加商品");
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/7 13:01
     *   @Comment 后台改变商品状态
     */
    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleState(HttpSession session,Integer productId,Integer status)
    {
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iProductService.setSaleStatus(productId,status);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权添加商品");
    }

    /**
     *   @Auchor ztian
     *   @Date 2017/6/7 13:14
     *   @Comment 查看商品详情
     */
    @RequestMapping(value = "detail.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetailProduct(HttpSession session,Integer productId)
    {
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //验证管理员，业务代码放这里
            return iProductService.manageDetailProduct(productId);
        }
        //非管理员
        return ServerResponse.createByErrorMessage("用户非管理员,无权添加商品");
    }

    @RequestMapping(value = "list.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue="10") Integer pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先进行登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //业务代码
            return iProductService.getProductList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限操作");
    }

    @RequestMapping(value = "search.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getProductListBySearch(HttpSession session,String productName,Integer productId ,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue="10") Integer pageSize){
        User user= (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先进行登录");
        }
        if(iUserService.checkAdmin(user).isSuccess()) {
            //业务代码
            return iProductService.getProductListBySearch(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限操作");
    }


    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpSession session){
        User currUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currUser==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请先进行登录");
        }
        if(iUserService.checkAdmin(currUser).isSuccess()) {
            //业务代码
            String path=request.getSession().getServletContext().getRealPath("upload");
            String uri=iFileService.upload(file,path);
            if(uri!=null) {
                String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+uri;
                Map fileMap= Maps.newHashMap();
                fileMap.put("uri",uri);
                fileMap.put("url",url);
                return ServerResponse.createBySuccess(fileMap);
            }

            return ServerResponse.createByErrorMessage("上传文件失败");
        }
        return ServerResponse.createByErrorMessage("用户非管理员,无权限操作");
    }

    //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
    //        {
    //            "success": true/false,
    //                "msg": "error message", # optional
    //            "file_path": "[real file path]"
    //        }
    //            response.addHeader("Access-Control-Allow-Headers","X-File-Name");

    @RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpSession session, HttpServletResponse response){
        User currUser = (User) session.getAttribute(Const.CURRENT_USER);
        Map map=Maps.newHashMap();

        //用户未登录
        if(currUser==null)
        {
            map.put("success","false");
            map.put("msg","用户未登录,请先登录");
            return map;
        }
        if(iUserService.checkAdmin(currUser).isSuccess()) {
            //业务代码
            String path=request.getSession().getServletContext().getRealPath("upload");
            String uri=iFileService.upload(file,path);
            if(uri!=null) {
                String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+uri;
                map.put("success","true");
                map.put("file_path",url);
                response.addHeader("Access-Control-Allow-Headers","X-File-Name");
                return map;
            }
            //上传文件失败时
            map.put("success","false");
            map.put("msg","上传文件失败");
            return map;
        }
        //用户非管理员无权限时
        map.put("success","false");
        map.put("msg","用户非管理员，无操作权限");
        return map;
    }
}
