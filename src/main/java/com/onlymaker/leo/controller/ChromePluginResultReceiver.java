package com.onlymaker.leo.controller;

import com.onlymaker.leo.data.OrderInfo;
import com.onlymaker.leo.data.OrderInfoRepository;
import com.onlymaker.leo.util.ExcelParser;
import com.onlymaker.leo.util.JsonTransfer;
import com.onlymaker.leo.util.Qiniu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//@Controller
public class ChromePluginResultReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ChromePluginResultReceiver.class);
    private static final int PAGE_SIZE = 25;
    @Autowired
    OrderInfoRepository orderInfoRepository;
    @Autowired
    ExcelParser excelParser;
    @Autowired
    Qiniu qiniu;

    @RequestMapping("/order_info")
    public String index(@RequestParam(required = false) Integer pageNo, Model model) {
        if(pageNo == null || pageNo == 0) pageNo = 1;
        Page<OrderInfo> page = orderInfoRepository.findAll(new PageRequest(pageNo - 1, PAGE_SIZE));
        model.addAttribute("title", "Order Info");
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pages", (int) Math.ceil(orderInfoRepository.count() / (double) PAGE_SIZE));
        model.addAttribute("results", page.getContent());
        return "order_info";
    }

    @RequestMapping("/persist")
    @ResponseBody
    public Map<String, String> cache(@RequestParam(required = false) String data) throws IOException {
        if(data != null) {
            logger.debug("persist {}", data);
            List<Map<String, Object>> list = JsonTransfer.getMapper().readValue(data, List.class);
            list.stream().forEach(m -> {
                OrderInfo o = new OrderInfo(String.valueOf(m.get("orderId")), String.valueOf(m.get("customerId")), String.valueOf(m.get("asin")));
                if (orderInfoRepository.countByOrderId(o.getOrderId()) == 0) {
                    orderInfoRepository.save(o);
                    logger.debug("orderId {} persisted successfully", o.getOrderId());
                } else {
                    logger.debug("orderId {} already existed", o.getOrderId());
                }
            });
        }
        return new HashMap<String, String>(){
            {
                put("data", "success");
            }
        };
    }

    @RequestMapping("/order_info/export")
    @ResponseBody
    public String export() {
        String result = null;
        try {
            String file = excelParser.exportOrderInfo();
            result = qiniu.upload(file, UUID.randomUUID() + ".xls");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    @RequestMapping("/order_info/delete")
    @ResponseBody
    public String delete() {
        orderInfoRepository.deleteAll();
        return "success";
    }
}
