package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import com.sun.corba.se.spi.activation.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztian on 2017/6/7.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product!=null)
        {
            //设置subImages的第一张图为主图
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImages = product.getSubImages().split(",");
                if(subImages.length>0){
                    product.setMainImage(subImages[0]);
                }
            }


            if(product.getId()!=null)
            {
                //更新操作
                int updateCount = productMapper.updateByPrimaryKeySelective(product);
                if(updateCount>0){
                    return ServerResponse.createBySuccess("商品更新成功");
                }
                    return ServerResponse.createByErrorMessage("商品更新失败");
            }else{
                //新增操作
                int insertCount = productMapper.insert(product);
                if(insertCount>0){
                    return ServerResponse.createBySuccess("商品添加成功");
                }
                return ServerResponse.createByErrorMessage("商品添加失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新商品参数不正确");
    }


    public ServerResponse setSaleStatus(Integer productId,Integer status)
    {
        if(productId==null||status==null) {
            return ServerResponse.createByErrorMessage("更新商品状态参数错误");
        }
        Product updateProduct=new Product();
        updateProduct.setId(productId);
        updateProduct.setStatus(status);
        int updateCount = productMapper.updateByPrimaryKeySelective(updateProduct);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("更新商品状态成功");
        }
        return ServerResponse.createByErrorMessage("更新商品状态失败");
    }

    public ServerResponse manageDetailProduct(Integer productId)
    {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("找不到要查看的商品,商品已删除或下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    //product对象转换成ProductDetailVo对象方法
    public ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo vo=new ProductDetailVo();
        vo.setPrice(product.getPrice());
        vo.setId(product.getId());
        vo.setStock(product.getStock());
        vo.setName(product.getName());
        vo.setStatus(product.getStatus());
        vo.setCategoryId(product.getCategoryId());
        vo.setDetail(product.getDetail());
        vo.setSubtitle(product.getSubtitle());
        vo.setSubImages(product.getSubImages());
        vo.setMainImage(product.getMainImage());

        //设置imgHost 为ftp服务器Url地址前缀
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mmall.com/"));
        //使用日期转换器 转换Date类型的Create_Date到String类型
        vo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        vo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        //设置Category的ParentId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null) {
            vo.setParentCategoryId(0);
        }else{
            vo.setParentCategoryId(category.getParentId());
        }
        return vo;
    }

    public ServerResponse getProductList(Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productVoList= Lists.newArrayList();

        for(Product productItem:productList)
        {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productVoList.add(productListVo);
        }
        PageInfo pageReusult=new PageInfo(productList);
        pageReusult.setList(productVoList);
        return ServerResponse.createBySuccess(pageReusult);
    }

    //product对象转换成ProductListVo对象的方法
    public ProductListVo assembleProductListVo(Product product){
        ProductListVo vo=new ProductListVo();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setStatus(product.getStatus());
        vo.setSubtitle(product.getSubtitle());
        vo.setMainImage(product.getMainImage());
        vo.setCategoryId(product.getCategoryId());
        vo.setPrice(product.getPrice());
        vo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.mmall.com/"));
        return vo;
    }

    public ServerResponse getProductListBySearch(String productName,Integer productId,Integer pageNum,Integer pageSize)
    {
        //设置分页的pageNum和pageSize 第一条查询的sql语句会进行分页
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName=new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> productList = productMapper.selectProductListByNameAndId(productName, productId);
        ArrayList<ProductListVo> productListVoList =Lists.newArrayList();

        for(Product productItem:productList)
        {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult=new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse getDetailProduct(Integer productId)
    {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("找不到要查看的商品,商品已删除或下架");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getStatus())
        {
            return ServerResponse.createByErrorMessage("找不到要查看的商品,商品已删除或下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse getListBySearchAndCategoryId(int pageNum,int pageSize,Integer categoryId,String searchWord,String orderBy)
    {
        //按商品类别或搜索关键字搜索 若两个都为空，则参数错误
        if(categoryId==null&&searchWord==null)
        {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }

        List categoryIds=Lists.newArrayList();
        if(categoryId!=null)
        {
            //类别查询为空 并且关键字也为空,返回一个空的数组
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&StringUtils.isBlank(searchWord))
            {
                PageHelper.startPage(pageNum,pageSize);
                List productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //获得类别和所有子类别，放入categoryIds中
            categoryIds=iCategoryService.getCategoryAndChildrenById(category.getId()).getData();
        }

        if(StringUtils.isNotBlank(searchWord)){
            searchWord=new StringBuilder().append("%").append(searchWord).append("%").toString();
        }

        //动态排序处理
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)) {
            if(Const.ProductListOrderBy.PRICE_DESC_ASC.contains(orderBy)) {
                //PageHelper排序设置LIst
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList= productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(searchWord) ? null : searchWord, categoryIds.size() == 0 ? null : categoryIds);
        List productListVoList=Lists.newArrayList();
        //遍历producyList集合，把product转成productListVo
        for(Product product:productList)
        {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo =new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return  ServerResponse.createBySuccess(pageInfo);
    }
}
