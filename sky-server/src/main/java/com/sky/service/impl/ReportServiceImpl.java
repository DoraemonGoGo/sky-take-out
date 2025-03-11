package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
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

    /**
     * 导出报表
     *
     * @param response
     */
    public void export(HttpServletResponse response) {
        // 计算日期
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // 查询概览数据
        BusinessDataVO businessDataVO = workSpaceService.businessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        // 通过PIO将数据写到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 向模板中写入数据
            XSSFWorkbook excel = new XSSFWorkbook(in);

            XSSFSheet sheet = excel.getSheet("Sheet1");
            // 居中显示
            Font font = excel.createFont();
            font.setFontName("Times New Roman");
            font.setFontHeightInPoints((short)15);
            CellStyle cellStyle = excel.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setFont(font);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            sheet.getRow(1).getCell(1).setCellStyle(cellStyle);
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + " - " + dateEnd);

            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());

            List<LocalDate> dateList = getDateList(dateBegin, dateEnd);
            for (int i = 0; i < dateList.size(); i++) {
                LocalDate date = dateList.get(i);
                LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

                BusinessDataVO businessData = workSpaceService.businessData(begin, end);
                sheet.getRow(7 + i).getCell(1).setCellValue(date.toString());
                sheet.getRow(7 + i).getCell(2).setCellValue(businessData.getTurnover());
                sheet.getRow(7 + i).getCell(3).setCellValue(businessData.getValidOrderCount());
                sheet.getRow(7 + i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
                sheet.getRow(7 + i).getCell(5).setCellValue(businessData.getUnitPrice());
                sheet.getRow(7 + i).getCell(6).setCellValue(businessData.getNewUsers());
            }
            // 通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            // 关闭资源
            out.close();
            excel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
