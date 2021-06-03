package com.tongu.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ：wangjf
 * @date ：2021/6/3 13:14
 * @description：demo
 * @version: v1.1.0
 */
@Service
public class FootballService extends BatchService{

    @Autowired
    @Qualifier("footballJdbcTemplate")
    protected NamedParameterJdbcTemplate footballJdbcTemplate;

    public FootballService() {
        this.sourceJdbcTemplate = footballJdbcTemplate;
    }
}
