package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {

    /**
     * 营业额统计
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate beginTime, LocalDate endTime);
}
