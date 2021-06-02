package com.tongu.search.controller;

import com.tongu.search.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：wangjf
 * @date ：2021/3/12 10:44
 * @description：demo
 * @version: v1.1.0
 */
@RestController
public class TestController {

    @Autowired
    BatchService batchService;

    @GetMapping("/test")
    public String test() throws Exception {
        batchService.execute("select id, en_name sourceValue from com_t_country order by id", "ft_t_country", "name");
        return "success";
    }

}
