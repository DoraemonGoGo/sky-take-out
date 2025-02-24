package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐
     * @param dishIds
     * @return
     */
    public List<Long> getSetmealBtDishId(List<Long> dishIds);
}
