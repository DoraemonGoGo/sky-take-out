package com.sky.mapper;

import com.sky.annotation.AutoFull;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countDishByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFull(value = OperationType.INSERT)
    void insert(Dish dish);
}
