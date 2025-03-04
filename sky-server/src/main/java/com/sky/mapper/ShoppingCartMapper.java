package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 添加购物车
     *
     * @param shoppingCart
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id添加商品数量
     *
     * @param Cart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart Cart);

    /**
     * 插入购物车
     *
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id, number, amount, image, name, create_time, dish_flavor) values (#{userId}, #{dishId}, #{setmealId}, #{number}, #{amount}, #{image}, #{name}, #{createTime}, #{dishFlavor})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     *
     * @return
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void cleanByuserId(Long userId);

    /**
     * 删除购物车
     *
     * @param cart
     */
    void delete(ShoppingCart cart);

    /**
     * 批量插入购物车
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
