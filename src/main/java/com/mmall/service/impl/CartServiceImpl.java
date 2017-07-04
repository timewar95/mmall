package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.ICategoryService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ztian on 2017/6/20.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService{

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    public ServerResponse add(Integer userId,Integer produdctId,Integer count)
    {
        if(userId==null||produdctId==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }

        Cart cart=cartMapper.selectByUserIdAndProductId(userId,produdctId);
        //判断购车表中是否存在该项
        if(cart==null) {
            //在购物车表中不存在该项 新建该项 保存在购物车表中
            Cart cartItem=new Cart();
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(produdctId);
            cartItem.setUserId(userId);
            cartItem.setQuantity(count);
            cartMapper.insert(cartItem);
        }else{
            //已存在该项 更改购车车表中该项购买数量
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId,Integer produdctId,Integer count){
        if(userId==null||produdctId==null||count==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, produdctId);
        if(cart!=null){
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    public ServerResponse<CartVo> deleteByIds(Integer userId,String productIds){
        if(userId==null||productIds==null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),ResponseCode.ILLEAGAL_ARGUMENT.getDesc());
        }
        List<String> productIdsList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isNotEmpty(productIdsList)) {
            cartMapper.deleteByUseridAndProudctIds(userId,productIdsList);
        }
        //返回购物车表中的最新数据
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    //
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.selectOrUnSelect(userId,productId,checked);
        return this.list(userId);
    }

    public ServerResponse<Integer> selectCartProductCount(Integer userId){
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<CartProductVo> cartProductVoList= Lists.newArrayList();
        List<Cart> cartList=cartMapper.selectListByUserId(userId);
        BigDecimal cartTotalPrice=new BigDecimal("0");
        for(Cart cartItem:cartList)
        {
            CartProductVo cartProductVo=new CartProductVo();
            cartProductVo.setId(cartItem.getId());
            cartProductVo.setUserId(userId);
            cartProductVo.setProductId(cartItem.getProductId());

            Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            if(product!=null){
                cartProductVo.setProductMainImage(product.getMainImage());
                cartProductVo.setProductName(product.getName());
                cartProductVo.setProductSubtitle(product.getSubtitle());
                cartProductVo.setProductStatus(product.getStatus());
                cartProductVo.setProductPrice(product.getPrice());
                cartProductVo.setProductStock(product.getStock());
                //判断商品表中的库存是否满足购物车中的需求
                int buyLimitCount=0;
                if(product.getStock()>cartItem.getQuantity()) {
                    //商品表中的库存满足购物车中的需求
                    buyLimitCount=cartItem.getQuantity();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                }else{
                    buyLimitCount=product.getStock();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    //更新购物车表中商品数量 最大只能为商品库存
                    cartItem.setQuantity(product.getStock());
                    cartMapper.updateByPrimaryKeySelective(cartItem);
                }
                //设置cartProductVo的quantity和totalPrice
                cartProductVo.setQuantity(buyLimitCount);
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity().doubleValue()));
                cartProductVo.setProductChecked(cartItem.getChecked());
            }
            //只计算被选中商品的总价
            if(cartProductVo.getProductChecked()==Const.Cart.CHECKED) {
                cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }
        //遍历玩CartList设置CartVo
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.selectAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    //用于判断CartVo 的AllCheckd项 根据userId在购物车表中查不出有Status为0的项时 返回true
    private boolean selectAllCheckedStatus(Integer userId)
    {
        if(userId==null)
        {
            return false;
        }
        return cartMapper.selectAllCheckedStatus(userId)==0;
    }
}
