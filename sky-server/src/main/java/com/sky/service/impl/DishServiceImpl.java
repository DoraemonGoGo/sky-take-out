package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 分页查询菜品
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
         log.info("分页查询菜品: {}", dishPageQueryDTO);
         PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
            // 查询菜品
         Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
         return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 删除菜品
     *
     * @param ids
     */
    public void deleteBatch(List<Long> ids) {
        log.info("删除菜品: {}", ids);

        // 如果餐品为起售状态，则不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 如果菜品关联套餐，则不能删除
        List<Long> setmeals = setmealDishMapper.getSetmealBtDishId(ids);
        if (setmeals != null && setmeals.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        for (Long id : ids) {
            dishMapper.delete(id);
            // 删除菜品对应的口味
            dishFlavorMapper.deleteByDishIds(id);
        }

    }
}
