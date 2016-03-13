package com.jeffinbao.colorfulnotes.model;

import android.text.TextUtils;

import com.evernote.client.android.EvernoteUtil;

import net.tsz.afinal.annotation.sqlite.Table;

/**
 * Author: baojianfeng
 * Date: 2015-10-20
 * Role: Note model
 */
@Table(name = "note")
public class Note {
    private int id;
    private String guid;
    private String title;
    private String content;
    private String noteBookName;
    private String createTime;
    private String lastUpdateTime;
    private long lastUpdateTimeInLong;
    private String createLocation;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getNoteBookName() {
        return noteBookName;
    }
    public void setNoteBookName(String noteBookName) {
        this.noteBookName = noteBookName;
    }

    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastUpdateTimeInLong() {
        return lastUpdateTimeInLong;
    }
    public void setLastUpdateTimeInLong(long lastUpdateTimeInLong) {
        this.lastUpdateTimeInLong = lastUpdateTimeInLong;
    }

    public String getCreateLocation() {
        return createLocation;
    }
    public void setCreateLocation(String location) {
        this.createLocation = location;
    }

    public boolean isFirstPush() {
        return TextUtils.isEmpty(getGuid());
    }

    public com.evernote.edam.type.Note toEvernote() {
        com.evernote.edam.type.Note evernote = new com.evernote.edam.type.Note();
        evernote.setTitle(title);
        evernote.setContent(convertContentToEvernoteFormat(content));

        return evernote;
    }

    private String convertContentToEvernoteFormat(String content) {
        return EvernoteUtil.NOTE_PREFIX + content + EvernoteUtil.NOTE_SUFFIX;
    }

}
