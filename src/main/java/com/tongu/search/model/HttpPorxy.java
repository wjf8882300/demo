package com.tongu.search.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：wangjf
 * @date ：2021/6/2 14:14
 * @description：demo
 * @version: v1.1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HttpPorxy {
    private String host;
    private Integer port;
}
