package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 查询订单列表
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery(int pageNum, int pageSize, Integer status);

    /**
     * 查询订单详情
     *
     * @param orderId
     * @return
     */
    OrderVO detail(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId
     */
    void userCancelById(Long orderId) throws Exception;

    /**
     * 再来一单
     *
     * @param orderId
     */
    void repetition(Long orderId);

    /**
     * 条件查询订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单统计信息
     *
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 接单
     *
     * @param ordersConfirmDTO
     * @return
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     * @return
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 管理员取消订单
     *
     * @param ordersCancelDTO
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);

    /*
    * 派送订单
    *
    * @param orderId
     */
    void delivery(Long orderId);

    /**
     * 完成订单
     *
     * @param orderId
     */
    void complete(Long orderId);

    /**
     * 催单
     *
     * @param orderId
     */
    void reminder(Long orderId);
}
