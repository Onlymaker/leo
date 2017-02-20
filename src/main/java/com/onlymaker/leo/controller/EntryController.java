package com.onlymaker.leo.controller;

import com.onlymaker.leo.data.Entry;
import com.onlymaker.leo.data.EntryRepository;
import com.onlymaker.leo.data.LogRepository;
import com.onlymaker.leo.util.AmazonStore;
import com.onlymaker.leo.util.ExcelParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jibo on 2016/10/19.
 */
@Controller
public class EntryController {
    private static final Logger logger = LoggerFactory.getLogger(EntryController.class);
    private static final int PAGE_SIZE = 25;
    private static final int UPLOAD_INIT = 0;
    private static final int UPLOAD_SUCCESS = 1;
    private static final int UPLOAD_FAILURE = 2;
    private static final int COUNT_TYPE_ALL = 0;
    private static final int COUNT_TYPE_UPDATE = 1;
    private static final int COUNT_TYPE_FILTER = 2;

    @Autowired
    EntryRepository entryRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    ExcelParser excelParser;
    @Value("${logging.path}")
    String path;

    @RequestMapping({"/", "/index"})
    public String index(
            @RequestParam(required = false) Integer pageNo,
            Model model
    ) {
        if(pageNo == null || pageNo == 0) pageNo = 1;
        Page<Entry> page = entryRepository.findAll(new PageRequest(pageNo - 1, PAGE_SIZE, Sort.Direction.DESC, "updateTime"));
        model.addAttribute("title", "Index");
        model.addAttribute("current", "index");
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pages", getPageCount(COUNT_TYPE_ALL));
        model.addAttribute("entries", page.getContent());
        model.addAttribute("stores", AmazonStore.STORES.keySet());
        return "index";
    }

    @RequestMapping({"asin", "/asin/{asinStr}"})
    public String asin(
            @PathVariable(required = false) String asinStr,
            @RequestParam(required = false) Long id,
            Model model
    ) {
        if(StringUtils.isEmpty(asinStr)) return "redirect:/index";
        else if(asinStr.startsWith("detail") && id != null) return "redirect:/detail?id=" + id;
        List<Entry> entries = new ArrayList<>();
        Entry entry = entryRepository.findFirstByAsin(asinStr);
        entries.add(entry);
        model.addAttribute("title", "Asin");
        model.addAttribute("current", "asin");
        model.addAttribute("asin", asinStr);
        model.addAttribute("pageNo", 1);
        model.addAttribute("pages", 1);
        model.addAttribute("entries", entries);
        return "index";
    }

    @RequestMapping({"store", "/store/{filter}"})
    public String filter(
            @PathVariable(required = false) String filter,
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Long id,
            Model model
    ) {
        if(StringUtils.isEmpty(filter)) return "redirect:/index";
        else if(!AmazonStore.STORES.containsKey(filter)) {
            if(filter.startsWith("detail") && id != null)
                return "redirect:/detail?id=" + id;
            return "redirect:/" + filter;
        }
        if(pageNo == null || pageNo == 0) pageNo = 1;
        Page<Entry> page = entryRepository.findByStore(filter, new PageRequest(pageNo - 1, PAGE_SIZE, Sort.Direction.DESC, "updateTime"));
        model.addAttribute("title", "Index");
        model.addAttribute("current", filter);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pages", getPageCount(COUNT_TYPE_FILTER, filter));
        model.addAttribute("entries", page.getContent());
        model.addAttribute("stores", new String[] {filter});
        return "index";
    }

    @RequestMapping("/update")
    public String update(
            @RequestParam(required = false) Integer pageNo,
            Model model
    ) {
        if(pageNo == null || pageNo == 0) pageNo = 1;
        Page<Entry> page = entryRepository.findByStatus(1, new PageRequest(pageNo - 1, PAGE_SIZE, Sort.Direction.DESC, "updateTime"));
        model.addAttribute("title", "Update");
        model.addAttribute("current", "update");
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("pages", getPageCount(COUNT_TYPE_UPDATE));
        model.addAttribute("entries", page.getContent());
        return "index";
    }

    @RequestMapping("/detail")
    public String detail(HttpServletRequest request, Model model) {
        Long id = Long.parseLong(request.getParameter("id"));
        Entry entry = entryRepository.findOne(id);
        if(request.getMethod().toLowerCase().equals("post")) {
            entry.setStatus(0);
            entryRepository.save(entry);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        model.addAttribute("title", "Detail");
        model.addAttribute("id", id);
        model.addAttribute("status", entry.getStatus());
        model.addAttribute("entries", logRepository.findAllByEntryAndCreateTimeAfterOrderByIdDesc(entry, new Timestamp(calendar.getTimeInMillis())));
        return "detail";
    }

    @RequestMapping("/create")
    public String create(HttpServletRequest request, Model model) {
        if(request.getMethod().toLowerCase().equals("post")) {
            String asin = request.getParameter("asin").trim();
            if(entryRepository.findFirstByAsin(asin) == null) {
                entryRepository.save(new Entry(asin, request.getParameter("store").trim().toUpperCase()));
            }
            return "redirect:/index";
        }
        model.addAttribute("title", "Upload");
        model.addAttribute("stores", AmazonStore.STORES.keySet());
        return "create";
    }

    @GetMapping("/upload")
    public String upload(Model model) {
        model.addAttribute("title", "Upload");
        model.addAttribute("status", UPLOAD_INIT);
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String upload(@RequestParam(required = false) MultipartFile file, Model model) {
        int status = UPLOAD_FAILURE;
        if (!file.isEmpty()) {
            String name = path + File.separator + file.getOriginalFilename();
            try {
                logger.info("save upload file: {}", name);
                file.transferTo(new File(name));
                if(excelParser.parseEntry(name) == 0) {
                    status = UPLOAD_SUCCESS;
                }
            } catch (IOException e) {
                logger.error("upload error: {}", e.getMessage());
            }
        }
        model.addAttribute("title", "Upload");
        model.addAttribute("status", status);
        return "upload";
    }

    private int getPageCount(int type, String ... args) {
        long count;
        switch (type) {
            case COUNT_TYPE_ALL:
                count = entryRepository.count();
                break;
            case COUNT_TYPE_UPDATE:
                count = entryRepository.countByStatus(1);
                break;
            case COUNT_TYPE_FILTER:
                count = entryRepository.countByStore(args[0]);
                break;
            default:
                count = PAGE_SIZE;
        }
        return (int) Math.ceil(count / (double) PAGE_SIZE);
    }
}
