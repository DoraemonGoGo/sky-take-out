package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询营业数据
     *
     * @return
     */
    public BusinessDataVO businessData(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        // 查询订单数量
        Integer orderCount = orderMapper.countByMap(map);
        orderCount = orderCount == null ? 0 : orderCount;

        map.put("status", Orders.COMPLETED);
        // 查询营业额
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0.0 : turnover;

        // 查询有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;

        // 查询平均客单价
        Double unitPrice = validOrderCount == 0 ? 0.0 : turnover * 1.0 / validOrderCount;
        Double completionRate = orderCount == 0 ? 0.0 : validOrderCount * 1.0 / orderCount;

        // 新增用户数
        Integer newUsers = userMapper.countUser(map);
        newUsers = newUsers == null ? 0 : newUsers;

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(completionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单概览
     *
     * @return
     */
    public OrderOverViewVO ordersOverview() {
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        // 待接单数
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmed = orderMapper.countByMap(map);
        // 待派送数
        map.put("status", Orders.CONFIRMED);
        Integer confirmed = orderMapper.countByMap(map);
        // 已完成数
        map.put("status", Orders.COMPLETED);
        Integer completed = orderMapper.countByMap(map);
        // 已取消数
        map.put("status", Orders.CANCELLED);
        Integer cancelled = orderMapper.countByMap(map);
        // 订单总数
        map.put("status", null);
        Integer total = orderMapper.countByMap(map);
        return OrderOverViewVO.builder()
                .waitingOrders(toBeConfirmed)
                .deliveredOrders(confirmed)
                .completedOrders(completed)
                .cancelledOrders(cancelled)
                .allOrders(total)
                .build();
    }

    /**
     * 查询菜品概览
     *
     * @return
     */
    public DishOverViewVO dishesOverview() {
        // 已起售数量
        Integer onSale = dishMapper.countOnOrOffSale(StatusConstant.ENABLE);
        // 已停售数量
        Integer offSale = dishMapper.countOnOrOffSale(StatusConstant.DISABLE);
        return DishOverViewVO.builder()
                .sold(onSale)
                .discontinued(offSale)
                .build();
    }

    /**
     * 查询套餐概览
     *
     * @return
     */
    public SetmealOverViewVO setmealsOverview() {
        // 已起售数量
        Integer onSale = setmealMapper.countOnOrOffSale(StatusConstant.ENABLE);
        // 已停售数量
        Integer offSale = setmealMapper.countOnOrOffSale(StatusConstant.DISABLE);
        return SetmealOverViewVO.builder()
                .sold(onSale)
                .discontinued(offSale)
                .build();
    }
}
