package com.zosh.service;

import com.zosh.model.Category;

import java.util.List;

public interface CategoryService {
    public Category createCategory(String name, Long userId) throws Exception;

    public List<Category> findCategoryByRestaurant(Long id) throws Exception;

    public Category findCategoryById(Long id) throws  Exception;



}
