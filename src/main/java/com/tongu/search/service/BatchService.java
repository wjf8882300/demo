package com.tongu.search.service;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tongu.search.model.QueryVO;
import com.tongu.search.util.ExcelUtil;
import com.tongu.search.util.GoogleTranslateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class BatchService {

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


    /**
     * 批量更新
     * @param sql
     * @param tableName
     * @param columnName
     */
    public void execute(String sql, String tableName, String columnName) {
        execute(sql, tableName, columnName, list->{
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

            StringBuilder querySql = new StringBuilder()
                    .append(String.format(" insert into %s(id, %s) ", tableName, columnName))
                    .append(" values(:id, :destValue) ")
                    .append(" on conflict(id) do ")
                    .append(String.format(" update set %s = :destValue ", columnName));

            batchUpdateBeans(querySql.toString(), list);
        });
    }

    /**
     * 批量导出
     * @param sql
     * @param tableName
     * @param columnName
     */
    public void export(String sql, String tableName, String columnName) {
        List<QueryVO> allList = Lists.newArrayList();
        execute(sql, tableName, columnName, list->{
            // 查询已经本地库（已经翻译的）
            List<Long> ids = list.stream().map(QueryVO::getId).collect(Collectors.toList());
            Map<String,Object> paramMap = Maps.newHashMap();
            paramMap.put("ids", ids);
            String querySql = MessageFormat.format("select id, {1} destValue from {0}", tableName, columnName);
            List<QueryVO> localList = localeJdbcTemplate.query(querySql, paramMap, new BeanPropertyRowMapper<QueryVO>(QueryVO.class));
            // 合并本地库和标准库数据
            list.forEach(s->{
                localList.stream().filter(t->t.getId().equals(s.getId())).findAny().ifPresent(t->{
                    s.setDestValue(t.getDestValue());
                });
            });

            // 数据放到一起
            allList.addAll(list);
        });

        List<List<Object>> content = allList.stream().map(s -> {
            List<Object> list = Lists.newArrayList();
            list.add(s.getId());
            list.add(s.getSourceValue());
            list.add(s.getDestValue());
            return list;
        }).collect(Collectors.toList());

        log.info("开始生成文件: {}", tableName + ".xlsx");
        ExcelUtil.ExcelEntity excelEntity = new ExcelUtil.ExcelEntity();
        excelEntity.setFileFolder("/data/");
        excelEntity.setFileName(tableName + ".xlsx");
        excelEntity.setSheetName("sheet1");
        excelEntity.setHeaders(Lists.newArrayList("id", "zh_name", "en_name/vi_name"));
        excelEntity.setContent(content);
        ExcelUtil.createReport(excelEntity);

        log.info("结束生成文件: {}", tableName + ".xlsx");
    }

    public void execute(String sql, String tableName, String columnName, Consumer<List<QueryVO>> consumer) {
        this.tableName = tableName;
        this.columnName = columnName;
        NamedParameterJdbcTemplate jdbcTemplate = sourceJdbcTemplate;
        String countSql = MessageFormat.format("select count(1) from ({0}) a", sql);
        Integer total = jdbcTemplate.queryForObject(countSql, Maps.newHashMap(), Integer.class);
        log.info("共有{}条记录待处理", total);
        int size = total / PAGE_SIZE;
        int i = 0;
        for(; i < size; i ++) {
            executeOnce(sql, i*PAGE_SIZE, PAGE_SIZE, jdbcTemplate, consumer);
        }
        if(total % PAGE_SIZE != 0) {
            executeOnce(sql,i*PAGE_SIZE, PAGE_SIZE, jdbcTemplate, consumer);
        }
    }

    private void executeOnce(String sql, int offset, int pageSize, NamedParameterJdbcTemplate jdbcTemplate, Consumer<List<QueryVO>> consumer) {
        String querySql = MessageFormat.format("{0} limit {2} offset {1}", sql, String.valueOf(offset), String.valueOf(pageSize));
        List<QueryVO> list = jdbcTemplate.query(querySql, Maps.newHashMap(), new BeanPropertyRowMapper<QueryVO>(QueryVO.class));
        log.info("开始处理: {}", querySql);
        int i = 0;
        while(i++ < 10) {
            try {
                consumer.accept(list);
                break;
            }catch (Exception e) {
                log.error("处理失败，尝试{}次", i);
            }
        }
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
