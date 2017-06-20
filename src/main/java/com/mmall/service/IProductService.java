package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by ztian on 2017/6/7.
 */
public interface IProductService {
    public ServerResponse saveOrUpdateProduct(Product product);
    public ServerResponse setSaleStatus(Integer productId,Integer status);
    public ServerResponse manageDetailProduct(Integer productId);
    public ServerResponse getProductList(Integer pageNum,Integer pageSize);
    public ServerResponse getProductListBySearch(String productName,Integer productId,Integer pageNum,Integer pageSize);
    public ServerResponse getDetailProduct(Integer productId);
    public ServerResponse getListBySearchAndCategoryId(int pageNum,int pageSize,Integer categoryId,String searchWord,String orderBy);
}
