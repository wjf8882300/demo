package com.tongu.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author ：wangjf
 * @date ：2021/6/3 13:14
 * @description：demo
 * @version: v1.1.0
 */
@Service
public class BasketballService extends BatchService{

    @Autowired
    @Qualifier("basketballJdbcTemplate")
    protected NamedParameterJdbcTemplate basketballJdbcTemplate;

    @PostConstruct
    void init() {
        this.sourceJdbcTemplate = basketballJdbcTemplate;
    }
}
