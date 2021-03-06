package com.github.chengzhx76.buy.processor;

import com.alibaba.fastjson.JSON;
import com.github.chengzhx76.buy.Buyer;
import com.github.chengzhx76.buy.model.*;
import com.github.chengzhx76.buy.utils.FileUtils;
import com.github.chengzhx76.buy.utils.OperationType;
import com.github.chengzhx76.buy.utils.TicketConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @desc:
 * @author: hp
 * @date: 2018/1/12
 */
public class SimpleProcessor implements Processor {

    private final static Logger LOG = LoggerFactory.getLogger(SimpleProcessor.class);


    @Override
    public void preHandle(Buyer buyer, Request request) {
        OperationType operation = request.getOperation();
        request.setMethod(operation.getMethod());
        if (OperationType.LOG.equals(operation) ||
                OperationType.QUERY.equals(operation)) {
            request.setUrl(operation.getUrl()
                    .replace("{TRAINDATE}", buyer.getStationDate())
                    .replace("{FROMSTATION}", buyer.getFromStationCode())
                    .replace("{TOSTATION}", buyer.getToStationCode()));
        } else {
            request.setUrl(operation.getUrl());
        }

    }

    @Override
    public void afterCompletion(Buyer buyer, Request request, Response response) {
        OperationType operation = response.getOperation();
        if (OperationType.LOG.equals(operation)) {

            ValidateMsg validateMsg = parseObject(response, ValidateMsg.class);
            if (validateMsg.getStatus()) {
                request.setSleepTime(0L);
                request.setOperation(OperationType.QUERY);
            } else {
                throw new RuntimeException("日志接口返回失败");
            }

        } else if (OperationType.QUERY.equals(operation)) {

            Query query = parseObject(response, Query.class);
            if (query == null ||
                    query.getData() == null ||
                    query.getData().getResult().isEmpty()) {
                LOG.warn("没有查到数据重新查询");
                return;
            }

            List<String> allTicket = query.getData().getResult();
            for (String tickets : allTicket) {
                String ticket[] = tickets.split("\\|");
                if ("Y".equals(ticket[11]) && "预订".equals(ticket[1])) {
                    for (String seatCn : buyer.getSetType()) {
                        int seatCode = TicketConstant.getSeat(seatCn);
                        String seat = ticket[seatCode];
                        if (!"".equals(seat) &&
                                !"*".equals(seat) &&
                                !"无".equals(seat) &&
                                buyer.getStationTrains().contains(ticket[3])) {

                            String secretStr = ticket[0];
                            String trainNo = ticket[3];
                            LOG.info("车次：" + trainNo + " 始发车站：" + buyer.getFromStation() + " 终点车站：" +
                                    buyer.getToStation() + " 席别：" + seatCn + "-" + seat + " 安全码：" + secretStr);
                            if (!"无".equals(seat)) {
                                request.setOperation(OperationType.CHECK_USER);
                            }
                        }
                    }
                }
            }

        } else if (OperationType.CHECK_USER.equals(operation)) {

            ValidateMsg validateMsg = parseObject(response, ValidateMsg.class);
            if (validateMsg.getData().getFlag()) {
                System.out.println("----------开始尝试下单-------------");
            } else {
                request.setOperation(OperationType.CAPTCHA_IMG);
            }

        } else if (OperationType.CAPTCHA_IMG.equals(operation)) {

            System.out.println("-----success-img------");
            try {
                FileUtils.writeToLocal(".\\s1.png", response.getContent());

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

            request.setOperation(OperationType.END);
        } else if (OperationType.END.equals(operation)) {
            System.out.println("-----end-----");
            response.destroy();
        } else {
            System.out.println("SimpleProcessor--> ---------");
        }
    }

    private ValidateMsg getValidateMsg(Response response) {
        try {
            return JSON.parseObject(response.getRawText(), ValidateMsg.class);
        } catch (Exception e) {
            LOG.error("解析验证消息出错");
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T parseObject(Response response, Class<T> clazz) {
        try {
            return JSON.parseObject(response.getRawText(), clazz);
        } catch (Exception e) {
            LOG.error("格式化发生错误 Response {}", response.getRawText(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
