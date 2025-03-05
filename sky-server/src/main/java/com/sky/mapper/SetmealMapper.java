package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFull;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countSetmealByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFull(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param id
     * @return
     */
    void deleteById(Long id);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    Setmeal getById(Long id);

    /**
     * 修改套餐
     * @param setmeal
     */
    @AutoFull(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 根据分类id查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品列表
     * @param setmealId
     * @return
     */
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);

    /**
     * 查询起售或停售的套餐数量
     * @param status
     * @return
     */
    @Select("select count(id) from setmeal where status = #{status}")
    Integer countOnOrOffSale(Integer status);
}
