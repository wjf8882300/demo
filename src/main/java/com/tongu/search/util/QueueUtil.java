package com.tongu.search.util;

import com.google.common.collect.Lists;
import com.tongu.search.model.HttpPorxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：wangjf
 * @date ：2021/6/2 14:15
 * @description：demo
 * @version: v1.1.0
 */
public class QueueUtil{

    private static Queue<HttpPorxy> queue;

    /**
     * 获取代理
     * @return
     */
    public static HttpPorxy get() {
        if(queue == null) {
            synchronized (QueueUtil.class) {
                if(queue == null) {
                    queue = new Queue<>();
                    List<String> hosts = Lists.newArrayList(
                            "10.148.0.19",
                            "10.148.0.20",
                            "10.148.0.21",
                            "10.148.0.22",
                            "10.148.0.23",
                            "10.148.0.24",
                            "10.148.0.25",
                            "10.148.0.26",
                            "10.148.0.27",
                            "10.148.0.29",
                            "10.148.0.28",
                            "10.148.0.30",
                            "10.148.0.31",
                            "10.148.0.33",
                            "10.148.0.34"
                            //"10.148.0.35"
                            );
                    hosts.forEach(h->{
                        queue.enqueue(new HttpPorxy(h, 9527));
                    });
                }
            }
        }
        return queue.dequeue();
    }

    public static class Queue<T> {
        /**
         * 容器
         */
        private List<T> values;
        /**
         * head 表示队头下标，tail表示对尾下标
         */
        private int head = 0;
        private int tail = 0;

        public Queue() {
            values = new ArrayList<>();
        }

        public synchronized Boolean enqueue(T value) {
            values.add(value);
            tail += 1;
            return true;
        }

        public synchronized T dequeue() {
            T ret = values.get(head);
            head += 1;
            if(head == tail) {
                head = 0;
            }
            return ret;
        }
    }
}
