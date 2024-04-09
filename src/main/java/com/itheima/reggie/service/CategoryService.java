package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;

import java.util.List;


public interface CategoryService extends IService<Category> {

    boolean remove(Long id);

    R<Page<Category>> getCategoryPage(int page, int pageSize);

    R<List<Category>> listCategory(Category category);

}
