package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车：{}", shoppingCartDTO);
        // 判断当前加入到购物车的商品是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 如果存在，则更新数量
        if (list != null && list.size() > 0) {
            ShoppingCart Cart = list.get(0);
            Cart.setNumber(Cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(Cart);
        } else {
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null){
                Dish dish = dishMapper.getById(dishId);

                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());

            }
            else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                shoppingCart.setNumber(1);
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查询购物车
     *
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart Cart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(Cart);
        return list;
    }

    /**
     * 清空购物车
     *
     * @param
     */
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.cleanByuserId(userId);
    }

    /**
     * 删除购物车
     *
     * @param shoppingCartDTO
     */
    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list != null && list.size() > 0) {
            ShoppingCart Cart = list.get(0);
            if (Cart.getNumber() > 1) {
                Cart.setNumber(Cart.getNumber() - 1);
                shoppingCartMapper.updateNumberById(Cart);
            } else {
                shoppingCartMapper.delete(Cart);
            }
        }
    }
}
