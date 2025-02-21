package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     *
     * @param dishDTO
     */
     @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
         log.info("新增菜品: {}", dishDTO);

         // 新增菜品
         Dish dish = new Dish();
         // 将dishDTO的属性拷贝到dish
         BeanUtils.copyProperties(dishDTO, dish);
         // 插入dish
         dishMapper.insert(dish);

         // 获取到新增菜品的id
         Long dishId = dish.getId();

         List<DishFlavor> flavors = dishDTO.getFlavors();
         if (flavors != null && flavors.size() > 0) {
                // 设置dishId
                for (DishFlavor flavor : flavors) {
                    flavor.setDishId(dishId);
                }
                // 批量插入口味
                dishFlavorMapper.insertBatch(flavors);
         }
    }
}
