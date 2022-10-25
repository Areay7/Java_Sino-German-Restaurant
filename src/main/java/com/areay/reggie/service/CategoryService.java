package com.areay.reggie.service;

import com.areay.reggie.entity.Category;
import com.areay.reggie.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
