package com.wf.wisdom_safety.bean.api;

import java.util.List;

/**
 * Created by Junhua Lv on 2016/7/21.
 * 分页结果格式
 */
public class ListResponse<T> {
    int total;
    List<T> rows;

    public int getTotal() {
        return total;
    }

    public List<T> getRows() {
        return rows;
    }
}
