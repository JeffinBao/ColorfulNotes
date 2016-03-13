package com.jeffinbao.colorfulnotes.event;

import java.util.List;
import java.util.Map;

/**
 * Author: baojianfeng
 * Date: 2016-01-11
 */
public abstract class BaseEvent<T> {

    protected List<T> list;
    protected Map<T, Integer> map;

    public BaseEvent(List<T> list, Map<T, Integer> map) {
        this.list = list;
        this.map = map;
    }
}
