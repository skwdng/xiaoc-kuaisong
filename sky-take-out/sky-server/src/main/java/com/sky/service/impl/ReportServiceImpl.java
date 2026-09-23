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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天日期
        List<LocalDate> dateList = getDateList(begin, end);

        //查询每天的营业额（状态为已完成的订单金额合计）
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map map = new HashMap();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
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
     * 统计指定时间区间内的用户数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天日期
        List<LocalDate> dateList = getDateList(begin, end);

        //存放每天的新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每天的总用户数量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);
            //总用户数量
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);
            //新增用户数量
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天日期
        List<LocalDate> dateList = getDateList(begin, end);

        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询每天的订单总数
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            //计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计指定时间区间内的销量排名前10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderDetailMapper.getSalesTop10(beginTime, endTime);
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);

        //查询概览数据
        Double turnover = getTurnover(beginTime, endTime);
        Integer totalOrderCount = getOrderCount(beginTime, endTime, null);
        Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        Double unitPrice = validOrderCount == 0 ? 0.0 : turnover / validOrderCount;
        Map userMap = new HashMap();
        userMap.put("begin", beginTime);
        userMap.put("end", endTime);
        Integer newUsers = userMapper.countByMap(userMap);

        //2. 通过POI将数据写入到Excel文件中
        XSSFWorkbook excel = new XSSFWorkbook();
        XSSFSheet sheet = excel.createSheet("运营数据报表");

        //概览数据
        XSSFRow titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("时间区间：" + dateBegin + " 至 " + dateEnd);
        XSSFRow overviewTitle = sheet.createRow(1);
        overviewTitle.createCell(0).setCellValue("营业额");
        overviewTitle.createCell(1).setCellValue("有效订单");
        overviewTitle.createCell(2).setCellValue("订单完成率");
        overviewTitle.createCell(3).setCellValue("平均客单价");
        overviewTitle.createCell(4).setCellValue("新增用户数");
        XSSFRow overviewData = sheet.createRow(2);
        overviewData.createCell(0).setCellValue(turnover);
        overviewData.createCell(1).setCellValue(validOrderCount);
        overviewData.createCell(2).setCellValue(orderCompletionRate);
        overviewData.createCell(3).setCellValue(unitPrice);
        overviewData.createCell(4).setCellValue(newUsers);

        //明细数据
        XSSFRow detailTitle = sheet.createRow(4);
        detailTitle.createCell(0).setCellValue("日期");
        detailTitle.createCell(1).setCellValue("营业额");
        detailTitle.createCell(2).setCellValue("有效订单");
        detailTitle.createCell(3).setCellValue("订单完成率");
        detailTitle.createCell(4).setCellValue("平均客单价");
        detailTitle.createCell(5).setCellValue("新增用户数");

        List<LocalDate> dateList = getDateList(dateBegin, dateEnd);
        int rowNum = 5;
        for (LocalDate date : dateList) {
            LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            Double dayTurnover = getTurnover(dayBegin, dayEnd);
            Integer dayTotalOrder = getOrderCount(dayBegin, dayEnd, null);
            Integer dayValidOrder = getOrderCount(dayBegin, dayEnd, Orders.COMPLETED);
            Double dayCompletionRate = dayTotalOrder == 0 ? 0.0 : dayValidOrder.doubleValue() / dayTotalOrder;
            Double dayUnitPrice = dayValidOrder == 0 ? 0.0 : dayTurnover / dayValidOrder;
            Map dayUserMap = new HashMap();
            dayUserMap.put("begin", dayBegin);
            dayUserMap.put("end", dayEnd);
            Integer dayNewUsers = userMapper.countByMap(dayUserMap);

            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(date.toString());
            row.createCell(1).setCellValue(dayTurnover);
            row.createCell(2).setCellValue(dayValidOrder);
            row.createCell(3).setCellValue(dayCompletionRate);
            row.createCell(4).setCellValue(dayUnitPrice);
            row.createCell(5).setCellValue(dayNewUsers);
        }

        //3. 通过输出流将Excel文件下载到客户端浏览器
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=business_data.xlsx");
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.flush();
            out.close();
            excel.close();
        } catch (IOException e) {
            log.error("导出运营数据报表失败", e);
        }
    }

    /**
     * 获取从begin到end范围内的每天日期集合
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 根据条件统计营业额
     * @param begin
     * @param end
     * @return
     */
    private Double getTurnover(LocalDateTime begin, LocalDateTime end) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);
        return turnover == null ? 0.0 : turnover;
    }
}
