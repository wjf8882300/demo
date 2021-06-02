package com.tongu.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * 财务管理系统
 * @author ：wangjf
 * @date ：2020/3/30 13:17
 * @version: v1.0.0
 */
@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = { "com.tongu.search", "com.alibaba.fastjson.support.spring" })
public class DemoApplication
{
    public static void main( String[] args )
    {
    	SpringApplication.run(DemoApplication.class, args);
    }
}

