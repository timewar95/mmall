package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by ztian on 2017/6/5.
 */
@Service("iCategoryservice")
public class CategoryServiceImpl implements ICategoryService{
    @Autowired
    private CategoryMapper categoryMapper;

    //日志对象
    private Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);

    public ServerResponse<String> addCategory(String categoryName,Integer parentId)
    {
        if(StringUtils.isBlank(categoryName)||parentId==null)
        {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int insertCount = categoryMapper.insert(category);
        if(insertCount>0)
        {
            return ServerResponse.createBySuccess("商品品类更新成功");
        }
        return ServerResponse.createByErrorMessage("商品品类更新失败");
    }


    public ServerResponse<String> updateCategory(String categoryName,Integer categoryId)
    {
        if(StringUtils.isBlank(categoryName)||categoryId==null)
        {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category updateCategory=new Category();
        updateCategory.setId(categoryId);
        updateCategory.setName(categoryName);

        int updateCount = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if(updateCount>0)
        {
            return ServerResponse.createBySuccessMessage("商品品类更新成功");
        }
        return ServerResponse.createByErrorMessage("商品品类更新错误");
    }

    public ServerResponse<List<Category>> getParallelChildrenCategory(Integer parentId)
    {
        List<Category> categoryList = categoryMapper.getParallelChildrenCategory(parentId);
        if(CollectionUtils.isEmpty(categoryList))
        {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);

    }

    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId)
    {
        List<Integer> categoryList = Lists.newArrayList();
        Set<Category> set= Sets.newHashSet();

        //调用递归函数，把所有节点信息封装到set集合
        findCategoryAndChildren(set, categoryId);
        for(Category categoryItem:set)
        {
            categoryList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //递归算法求出所有子节点的商品品类,放入set集合中
    public Set<Category> findCategoryAndChildren(Set<Category> set,Integer parentId){
        Category faterCategory = categoryMapper.selectByPrimaryKey(parentId);
        if(faterCategory!=null)
        {
            set.add(faterCategory);
        }
        List<Category> childrenCategoryList = categoryMapper.getParallelChildrenCategory(parentId);
        for(Category categoryItem:childrenCategoryList)
        {
            findCategoryAndChildren(set,categoryItem.getId());
        }
        return set;
    }
}
