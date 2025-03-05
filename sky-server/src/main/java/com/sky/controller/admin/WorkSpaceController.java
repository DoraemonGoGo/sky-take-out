package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "管理端工作台接口")
@Slf4j
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 查询营业数据
     *
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("查询营业数据")
    public Result<BusinessDataVO> businessData() {
        log.info("查询营业数据");
        BusinessDataVO businessDataVO = workSpaceService.businessData();
        return Result.success(businessDataVO);
    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> ordersOverview() {
        log.info("查询订单管理数据");
        OrderOverViewVO orderOverViewVO = workSpaceService.ordersOverview();
        return Result.success(orderOverViewVO);
    }

    /**
     * 查询菜品管理数据
     *
     * @return
     */
    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品管理数据")
    public Result<DishOverViewVO> dishesOverview() {
        log.info("查询菜品管理数据");
        DishOverViewVO dishOverViewVO = workSpaceService.dishesOverview();
        return Result.success(dishOverViewVO);
    }

    /**
     * 查询套餐管理数据
     *
     * @return
     */
    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐管理数据")
    public Result<SetmealOverViewVO> setmealsOverview() {
        log.info("查询套餐管理数据");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.setmealsOverview();
        return Result.success(setmealOverViewVO);
    }
}
