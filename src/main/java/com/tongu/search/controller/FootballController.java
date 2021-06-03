package com.tongu.search.controller;

import com.tongu.search.service.BatchService;
import com.tongu.search.service.FootballService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：wangjf
 * @date ：2021/3/12 10:44
 * @description：demo
 * @version: v1.1.0
 */
@RestController
@RequestMapping("/football")
public class FootballController {

    @Autowired
    FootballService batchService;

    @GetMapping("/country")
    public String country() throws Exception {
        batchService.execute("select id, en_name sourceValue from com_t_country where en_name is not null order by id", "ft_t_country", "name");
        return "success";
    }

    @GetMapping("/city")
    public String city() throws Exception {
        batchService.execute("select id, en_name sourceValue from com_t_city where en_name is not null order by id", "ft_t_city", "name");
        return "success";
    }

    @GetMapping("/team")
    public String team() throws Exception {
        batchService.execute("select id, en_name sourceValue from ft_t_team where en_name is not null order by id", "ft_t_team", "name");
        return "success";
    }

    @GetMapping("/player")
    public String player() throws Exception {
        batchService.execute("select id, en_name sourceValue from ft_t_player where en_name is not null order by id", "ft_t_player", "name");
        return "success";
    }

    @GetMapping("/teacher")
    public String teacher() throws Exception {
        batchService.execute("select id, en_name sourceValue from ft_t_teacher where en_name is not null order by id", "ft_t_teacher", "name");
        return "success";
    }

    @GetMapping("/venue")
    public String venue() throws Exception {
        batchService.execute("select id, en_name sourceValue from ft_t_venue where en_name is not null order by id", "ft_t_venue", "name");
        return "success";
    }

    @GetMapping("/event")
    public String event() throws Exception {
        batchService.execute("select id, en_name sourceValue from ft_t_event where en_name is not null order by id", "ft_t_event", "name");
        return "success";
    }
}
