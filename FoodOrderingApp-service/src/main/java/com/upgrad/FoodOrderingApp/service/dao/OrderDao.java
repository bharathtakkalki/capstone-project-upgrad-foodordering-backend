package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {


    @PersistenceContext
    private EntityManager entityManager;


    //To get list of OrdersEntity by the restaurant if no result then null is returned
    public List<OrdersEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity){
        try{
            List<OrdersEntity> ordersEntities = entityManager.createNamedQuery("getOrdersByRestaurant",OrdersEntity.class).setParameter("restaurant",restaurantEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre){
            return null;
        }
    }
}
