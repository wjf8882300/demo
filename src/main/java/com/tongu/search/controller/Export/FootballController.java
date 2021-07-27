package com.tongu.search.controller.Export;

import com.tongu.search.service.FootballService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：wangjf
 * @date ：2021/3/12 10:44
 * @description：demo
 * @version: v1.1.0
 */
@RestController("exportFootballController")
@RequestMapping("/export/football")
public class FootballController {

    @Autowired
    FootballService batchService;

    @GetMapping("/country")
    public String country() throws Exception {
        batchService.export("select id, zh_name sourceValue from com_t_country where zh_name is not null order by id", "ft_t_country", "name");
        return "success";
    }

    @GetMapping("/city")
    public String city() throws Exception {
        batchService.export("select id, zh_name sourceValue from com_t_city where zh_name is not null order by id", "ft_t_city", "name");
        return "success";
    }

    @GetMapping("/team")
    public String team() throws Exception {
        batchService.export("select id, zh_name sourceValue from ft_t_team where zh_name is not null order by id", "ft_t_team", "name");
        return "success";
    }

    @GetMapping("/player")
    public String player() throws Exception {
        batchService.export("select id, zh_name sourceValue from ft_t_player where zh_name is not null order by id", "ft_t_player", "name");
        return "success";
    }

    @GetMapping("/teacher")
    public String teacher() throws Exception {
        batchService.export("select id, zh_name sourceValue from ft_t_teacher where zh_name is not null order by id", "ft_t_teacher", "name");
        return "success";
    }

    @GetMapping("/venue")
    public String venue() throws Exception {
        batchService.export("select id, zh_name sourceValue from ft_t_venue where zh_name is not null order by id", "ft_t_venue", "name");
        return "success";
    }

    @GetMapping("/event")
    public String event() throws Exception {
        batchService.export("select id, zh_name sourceValue from ft_t_event where zh_name is not null order by id", "ft_t_event", "name");
        return "success";
    }
}
