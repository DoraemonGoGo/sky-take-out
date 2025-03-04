package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
/**
 * 管理端订单接口
 */
public class OrderController {

    @Autowired
    private OrderService orderService;

    /*
     * 搜索订单
     * @param ordersPageQueryDTO
     *
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("搜索订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("搜索订单");
        PageResult page = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(page);
    }

    /*
     * 统计各状态订单数量
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("统计各状态订单数量")
    public Result<OrderStatisticsVO> statistics() {
        log.info("统计各状态订单数量");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /*
     * 查询订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long orderId) {
        log.info("查询订单详情");
        return Result.success(orderService.detail(orderId));
    }

    /*
     * 接单
     *
     * @param orderId
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单");
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /*
     * 拒单
     *
     * @param orderId
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        log.info("拒单");
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    /*
    * 取消订单
    *
    * @param ordersCancelDTO
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        log.info("取消订单");
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    /*
     * 派送订单
     *
     * @param orderId
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable("id") Long orderId) {
        log.info("派送订单");
        orderService.delivery(orderId);
        return Result.success();
    }

    /*
     * 完成订单
     *
     * @param orderId
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long orderId) {
        log.info("完成订单");
        orderService.complete(orderId);
        return Result.success();
    }
}
