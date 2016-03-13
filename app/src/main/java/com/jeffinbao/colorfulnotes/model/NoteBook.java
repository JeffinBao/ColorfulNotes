package com.jeffinbao.colorfulnotes.model;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Author: baojianfeng
 * Date: 2015-10-14
 * Role: notebook model
 */
@Table(name = "notebook")
public class NoteBook {

    private int id;
    private int count;
    private String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
