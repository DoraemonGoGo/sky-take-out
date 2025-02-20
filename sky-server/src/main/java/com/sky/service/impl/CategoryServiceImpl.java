package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类管理业务层
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 新增分类
     *
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        Category category = new Category();
        // 将categoryDTO中的属性复制到category中
        BeanUtils.copyProperties(categoryDTO, category);

        // 分类状态默认为禁用
        category.setStatus(StatusConstant.DISABLE);

        // 设置创建时间、更新时间、创建人、更新人
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.insert(category);
    }

    /**
     * 分页查询分类
     *
     * @param categoryPageQueryDTO
     * @return
     */
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类：{}", categoryPageQueryDTO);
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        // 查询分类列表
        Page<Category> categoryPage = categoryMapper.pageQuery(categoryPageQueryDTO);
        // 封装分页结果
        return new PageResult(categoryPage.getTotal(), categoryPage.getResult());
    }


    /**
     * 根据id删除分类
     *
     * @param id
     */
    public void deleteById(Long id) {
        log.info("删除分类：{}", id);
        // 查询当前分类是否关联了菜品，如果关联了菜品则抛出业务异常
        Integer count = dishMapper.countDishByCategoryId(id);
        if (count > 0) {
            // 当前分类被菜品关联，不允许删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        // 查询当前分类是否关联了套餐，如果关联了套餐则抛出业务异常
        count = setmealMapper.countSetmealByCategoryId(id);
        if (count > 0) {
            // 当前分类被套餐关联，不允许删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        // 删除分类
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     *
     * @param categoryDTO
     */
    public void update(CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        // 设置修改时间和修改人
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser(BaseContext.getCurrentId());

        categoryMapper.update(category);
    }

    /**
     * 启用禁用分类状态
     *
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
        log.info("启用禁用分类状态：{}, {}", status, id);
        Category category = Category.builder()
                .id(id)
                .status(status)
//                .updateTime(LocalDateTime.now())
//                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类列表
     *
     * @param type
     * @return
     */
    public List<Category> list(Integer type) {
        log.info("查询分类列表：{}", type);
        return categoryMapper.list(type);
    }
}
