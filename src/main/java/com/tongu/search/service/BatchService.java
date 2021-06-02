package com.tongu.search.service;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.tongu.search.model.QueryVO;
import com.tongu.search.util.GoogleTranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BatchService {

    @Autowired
    @Qualifier("sourceJdbcTemplate")
    protected NamedParameterJdbcTemplate sourceJdbcTemplate;

    @Autowired
    @Qualifier("localeJdbcTemplate")
    protected NamedParameterJdbcTemplate localeJdbcTemplate;

    @Autowired
    private GoogleTranslateUtil googleTranslateUtil;

    private String tableName;
    private String columnName;

    /**
     * 每次处理条数
     */
    private final Integer PAGE_SIZE = 500;

    public void update(List<QueryVO> list) {
        List<String> sourceList = list.stream().map(QueryVO::getSourceValue).collect(Collectors.toList());
        String source = Joiner.on("\r\n").skipNulls().join(sourceList);
        if(StringUtils.isBlank(source)) {
            return;
        }
        String dest = googleTranslateUtil.translateText(source, "en", "vi");
        if(StringUtils.isBlank(dest)) {
            throw new RuntimeException(MessageFormat.format("翻译失败，返回的翻译文为空！source:{0}", source));
        }
        List<String> destList = Splitter.on("\r\n").trimResults().omitEmptyStrings().splitToList(dest);
        if(sourceList.size() != destList.size()) {
            throw new RuntimeException(MessageFormat.format("翻译失败，原文与翻译文不一致！source:{0}, dest:{1}", source, dest));
        }
        for(int i = 0; i < list.size(); i ++) {
            list.get(i).setDestValue(destList.get(i));
        }

        StringBuilder sql = new StringBuilder()
                .append(String.format(" insert into %s(id, %s) ", tableName, columnName))
                .append(" values(:id, :destValue) ")
                .append(" on conflict(id) do ")
                .append(String.format(" update set %s = :destValue ", columnName));

        batchUpdateBeans(sql.toString(), list);
    }

    public void execute(String sql, String tableName, String columnName) {
        this.tableName = tableName;
        this.columnName = columnName;
        NamedParameterJdbcTemplate jdbcTemplate = sourceJdbcTemplate;
        String countSql = MessageFormat.format("select count(1) from ({0}) a", sql);
        Integer total = jdbcTemplate.queryForObject(countSql, Maps.newHashMap(), Integer.class);
        int size = total / PAGE_SIZE;
        int i = 0;
        for(; i < size; i ++) {
            executeOnce(sql, i*PAGE_SIZE, PAGE_SIZE, jdbcTemplate);
        }
        if(total % PAGE_SIZE != 0) {
            executeOnce(sql,i*PAGE_SIZE, PAGE_SIZE, jdbcTemplate);
        }
    }

    private void executeOnce(String sql, int offset, int pageSize, NamedParameterJdbcTemplate jdbcTemplate) {
        String querySql = MessageFormat.format("{0} limit {2} offset {1}", sql, String.valueOf(offset), String.valueOf(pageSize));
        List<QueryVO> list = jdbcTemplate.query(querySql, Maps.newHashMap(), new BeanPropertyRowMapper<QueryVO>(QueryVO.class));
        log.info("开始处理: {}", querySql);
        update(list);
        log.info("结束处理: {}", querySql);
    }

    public int batchUpdateBeans(String insertSql, List<? extends Object> beans) {
        int[] results = null;
        try {
            SqlParameterSource[] params = SqlParameterSourceUtils
                    .createBatch(beans.toArray());
            results = localeJdbcTemplate.batchUpdate(insertSql,
                    params);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return results.length;
    }
}
