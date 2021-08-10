package com.tongu.search.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 通用分页处理
 * @author ：wangjf
 * @date ：2021/2/2 12:00
 * @description：provider-com
 * @version: v1.1.0
 */
@Slf4j
public class PageUtil {

    /**
     * 对List分页处理
     * 用法: handle(list, 100, s->{System.out.println(s.size());});
     * @param list
     * @param pageSize
     * @param action
     * @param <T>
     */
    public static <T> void handler(List<T> list, int pageSize, Consumer<List<T>> action) {
        log.info("=====开始处理=====");
        Objects.requireNonNull(action);
        int page = list.size() / pageSize;
        for(int i = 0; i < page; i++) {
            log.info("处理 {}/{}", i, page);
            List<T> pageList = list.subList(i * pageSize, (i + 1)*pageSize);
            action.accept(pageList);
        }
        if(list.size() % pageSize != 0) {
            log.info("处理 {}/{}", page, page);
            List<T> pageList = list.subList(page * pageSize, list.size());
            action.accept(pageList);
        }
        log.info("=====处理完毕=====");
    }

    /**
     * 分页处理
     * 用法：
     *          Integer count = baseMapper.selectCount(Wrappers.lambdaQuery(MatchNami.class)
     *                 .select(MatchNami::getId)
     *                 .isNull(MatchNami::getStandardId));
     *
     *         Integer pageSize = 500;
     *         PageUtil.handler(count, pageSize, (current)->{
     *             IPage<MatchNami> matchNamis = baseMapper.selectPage(new Page(current, pageSize), Wrappers.lambdaQuery(MatchNami.class)
     *                     .select(MatchNami::getId, MatchNami::getHomeTeamId, MatchNami::getAwayTeamId, MatchNami::getMatchTime)
     *                     .isNull(MatchNami::getStandardId)
     *                     .orderByDesc(MatchNami::getId));
     *             List<MatchNami> updateList = Lists.newArrayList();
     *             matchNamis.getRecords().forEach(m->{
     *                 setStandardId(m);
     *                 if(m.getStandardId() != null) {
     *                     updateList.add(m);
     *                 }
     *             });
     *             this.updateBatchById(updateList);
     *         });
     *
     * @param total
     * @param pageSize
     * @param <T>
     */
    public static <T> void handler(Integer total, Integer pageSize, Consumer<Integer> action) {
        Integer page = total / pageSize;
        for(int i = 0; i < page; i ++) {
            action.accept(i);
        }
        if(total % pageSize != 0) {
            action.accept(page);
        }
    }

    public static List<Long> getListByPage(List<Long> ids, int page, int pageSize) {

        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        page = page < 1 ? 1 : page;
        pageSize = pageSize < 1 ? 20 :pageSize;

        int firstIndex = (page - 1) * pageSize;
        int lastIndex = page * pageSize;
        int actualLastIndex = 0;
        if (ids.size() >= lastIndex) {
            actualLastIndex = lastIndex;
        } else {
            actualLastIndex = ids.size();
        }
        return ids.subList(firstIndex, actualLastIndex);
    }
}
