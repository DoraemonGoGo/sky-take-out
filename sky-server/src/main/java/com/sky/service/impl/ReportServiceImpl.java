package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public ReportServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public TurnoverReportVO getTurnoverStatistics(LocalDate beginTime, LocalDate endTime) {
        // 计算日期
        List<LocalDate> dateList = getDateList(beginTime, endTime);

        // 根据每天时间计算营业额
        List<Double> turnoverList = new ArrayList<>();
        Map map = new HashMap();
        for (LocalDate date : dateList) {
            // 查询data日期对应的营业额：已完成订单金额合计
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            map.put("begin", begin);
            map.put("end", end);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getDateList(begin, end);

        // 根据每天时间计算用户总数量
        List<Integer> totalUserList = new ArrayList<>();
        Integer totlaUserCount0 = 0;
        Map map = new HashMap();

        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询data日期对应的用户数量
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 根据每天时间计算新用户数量
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer newUserCount = userMapper.countUser(map);
            newUserCount = newUserCount == null ? 0 : newUserCount;
            newUserList.add(newUserCount);

            if (date == dateList.get(0)) {
                map.put("begin", null);
                totlaUserCount0 = userMapper.countUser(map);
                totlaUserCount0 = totlaUserCount0 == null ? 0 : totlaUserCount0;
                totalUserList.add(totlaUserCount0);
            }
            else {
                Integer totlaUserCount = totlaUserCount0 + newUserCount;
                totlaUserCount = totlaUserCount == null ? 0 : totlaUserCount;
                totalUserList.add(totlaUserCount);
                totlaUserCount0 = totlaUserCount;
            }
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdertatistics(LocalDate begin, LocalDate end) {
        // 计算日期
        List<LocalDate> dateList = getDateList(begin, end);

        // 根据每天时间计算订单总数
        List<Integer> totalOrderList = new ArrayList<>();
        // 根据每天时间计算完成订单数量
        List<Integer> completedOrderList = new ArrayList<>();

        for (LocalDate date : dateList) {
            Integer totalOrderCount = countOrder(date, date, null);
            Integer completedOrderCount = countOrder(date, date, Orders.COMPLETED);

            completedOrderList.add(completedOrderCount);
            totalOrderList.add(totalOrderCount);
        }

        // 计算订单总数和完成订单数量
        Integer totalOrder = totalOrderList.stream().reduce(Integer::sum).get();
        Integer completedOrder = completedOrderList.stream().reduce(Integer::sum).get();

        // 计算订单完成率
        Double completedRate = totalOrder == 0 ? 0.0 : completedOrder * 1.0 / totalOrder;

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .validOrderCountList(StringUtils.join(completedOrderList, ","))
                .totalOrderCount(totalOrder)
                .validOrderCount(completedOrder)
                .orderCompletionRate(completedRate)
                .build();
    }

    /**
     * 根据条件统计订单数量
     *
     * @param begin
     * @param end
     * @param status
     *
     * @return
     */
    public Integer countOrder(LocalDate begin, LocalDate end, Integer status) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", status);
        Integer count = orderMapper.countByMap(map);
        return count == null ? 0 : count;
    }

    /**
     * 销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSaleTop10(LocalDate begin, LocalDate end) {
        // 计算日期
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesList = orderMapper.getSalesTop10(beginTime, endTime);
        String names = goodsSalesList.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));
        String numbers = goodsSalesList.stream().map(GoodsSalesDTO::getNumber).map(String::valueOf).collect(Collectors.joining(","));

        return SalesTop10ReportVO.builder()
                .nameList(names)
                .numberList(numbers)
                .build();
    }

    /**
     * 计算日期
     *
     * @param begin
     * @param end
     * @return
     */
    public List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }
}
