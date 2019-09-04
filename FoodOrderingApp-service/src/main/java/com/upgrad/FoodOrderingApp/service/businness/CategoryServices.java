package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


@Service
public class CategoryServices {


    @Autowired
    RestaurantCategoryDao restaurantCategoryDao;

    @Autowired
    RestaurantDao restaurantDao;
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid){
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategory());
        });
        return categoryEntities;
    }
}
