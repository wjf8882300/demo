package com.tongu.search.controller.Import;

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
@RestController("importFootballController")
@RequestMapping("/import/football")
public class FootballController {

    @Autowired
    FootballService batchService;

    @GetMapping("/country")
    public String country() throws Exception {
        batchService.importExcel("ft_t_country", "name");
        return "success";
    }

    @GetMapping("/city")
    public String city() throws Exception {
        batchService.importExcel("ft_t_city", "name");
        return "success";
    }

    @GetMapping("/team")
    public String team() throws Exception {
        batchService.importExcel("ft_t_team", "name");
        return "success";
    }

    @GetMapping("/player")
    public String player() throws Exception {
        batchService.importExcel("ft_t_player", "name");
        return "success";
    }

    @GetMapping("/teacher")
    public String teacher() throws Exception {
        batchService.importExcel("ft_t_teacher", "name");
        return "success";
    }

    @GetMapping("/venue")
    public String venue() throws Exception {
        batchService.importExcel("ft_t_venue", "name");
        return "success";
    }

    @GetMapping("/event")
    public String event() throws Exception {
        batchService.importExcel("ft_t_event", "name");
        return "success";
    }

    @GetMapping("/honor")
    public String honor() throws Exception {
        batchService.importExcel("ft_t_honor", "name");
        return "success";
    }

    @GetMapping("/stage")
    public String stage() throws Exception {
        batchService.importExcel("ft_t_football_stage", "name");
        return "success";
    }
}
