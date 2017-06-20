package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ztian on 2017/6/20.
 */
@Controller
@RequestMapping(value = "/product/")
public class ProductControl {
    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "detail.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse detail(Integer productId){
        return iProductService.getDetailProduct(productId);
    }

    @RequestMapping(value = "list.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "pageNum",defaultValue ="1")int pageNum,
                               @RequestParam(value = "10",defaultValue = "10")int pageSize,
                               @RequestParam(value = "categoryId",required = false)Integer categoryId,
                               @RequestParam(value = "searchWord",required = false)String searchWord,
                               @RequestParam(value = "orderBy",defaultValue = "price_desc")String orderBy){
        return iProductService.getListBySearchAndCategoryId(pageNum,pageSize,categoryId,searchWord,orderBy);
    }

}
