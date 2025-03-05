package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkSpaceService {

    /**
     * 查询营业数据
     *
     * @return
     */
    BusinessDataVO businessData();

    /**
     * 查询订单管理数据
     *
     * @return
     */
    OrderOverViewVO ordersOverview();

    /**
     * 查询菜品管理数据
     *
     * @return
     */
    DishOverViewVO dishesOverview();

    /**
     * 查询套餐管理数据
     *
     * @return
     */
    SetmealOverViewVO setmealsOverview();
}
