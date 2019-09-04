package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

@Service
public class RestaurantServices {

    @Autowired
    RestaurantDao restaurantDao;

    public List<RestaurantEntity> restaurantsByRating(){
        List<RestaurantEntity> restaurantEntities = restaurantDao.restaurantsByRating();
        return restaurantEntities;
    }



}
