package com.onlymaker.leo.util;

import com.onlymaker.leo.data.Entry;
import com.onlymaker.leo.data.EntryRepository;
import com.onlymaker.leo.data.OrderInfo;
import com.onlymaker.leo.data.OrderInfoRepository;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by jibo on 2016/10/21.
 */
@Component
public class ExcelParser {
    public static final Logger logger = LoggerFactory.getLogger(ExcelParser.class);
    @Autowired
    EntryRepository entryRepository;
    @Autowired
    OrderInfoRepository orderInfoRepository;
    @Value("${logging.path}")
    String path;

    // asin, store
    public int parseEntry(String file) {
        int error = 1;
        try {
            Iterator<Row> i = iterator(file);
            if(i != null) {
                i.forEachRemaining(row -> {
                    Cell asin = row.getCell(0);
                    Cell store = row.getCell(1);
                    if(asin == null) {
                        logger.error("找不到产品型号：{} line {}", file, row.getRowNum());
                    } else if(store == null) {
                        logger.error("找不到渠道：{} line {}", file, row.getRowNum());
                    } else {
                        asin.setCellType(CellType.STRING);
                        store.setCellType(CellType.STRING);
                        String m = asin.getStringCellValue();
                        String s = store.getStringCellValue();
                        if(!StringUtils.isEmpty(m)
                                && entryRepository.findFirstByAsin(m) == null
                                && AmazonStore.STORES.containsKey(s)
                        ) {
                            entryRepository.save(new Entry(m, s));
                        }
                    }
                });
            }
            error = 0;
        } catch (IOException e) {
            logger.error("excel parse error: {}", e.getMessage());
        } finally {
            return error;
        }
    }

    private Iterator<Row> iterator(String name) throws IOException {
        Iterator<Row> i = null;
        File file = new File(name);
        if(name.endsWith(".xls")) {
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            HSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
            i = sheet.iterator();
        } else if(name.endsWith(".xlsx")) {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
            i = sheet.iterator();
        }
        return i;
    }

    public String exportOrderInfo() throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("orderId");
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue("customerId");
        cell.setCellStyle(style);
        cell = row.createCell(2);
        cell.setCellValue("asin");
        cell.setCellStyle(style);

        Iterator<OrderInfo> i = orderInfoRepository.findAll().iterator();
        while (i.hasNext()) {
            OrderInfo o = i.next();
            row = sheet.createRow(row.getRowNum() + 1);
            cell = row.createCell(0);
            cell.setCellValue(o.getOrderId());
            cell = row.createCell(1);
            cell.setCellValue(o.getCustomerId());
            cell = row.createCell(2);
            cell.setCellValue(o.getAsin());
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String excel = path + File.separator + df.format(new Date()) + ".xls";
        FileOutputStream outputStream = new FileOutputStream(excel);
        wb.write(outputStream);
        outputStream.close();
        return excel;
    }
}
