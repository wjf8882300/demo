package com.tongu.search.controller;

import com.tongu.search.util.GoogleTranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：wangjf
 * @date ：2021/6/11 11:10
 * @description：demo
 * @version: v1.1.0
 */
@Slf4j
@RestController
@RequestMapping("/translate")
public class TranslateController {

    @Autowired
    private GoogleTranslateUtil googleTranslateUtil;

    @GetMapping("/test")
    public String test() throws Exception {
        log.info(googleTranslateUtil.translateTextSdk("这是一个测试", "zh", "en"));
        return "success";
    }
}
