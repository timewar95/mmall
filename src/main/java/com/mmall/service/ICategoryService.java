package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by ztian on 2017/6/5.
 */
public interface ICategoryService {
    public ServerResponse<String> addCategory(String categoryName, Integer parentId);
    public ServerResponse<String> updateCategory(String categoryName,Integer categoryId);
    public ServerResponse<List<Category>> getParallelChildrenCategory(Integer parentId);
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);
}
