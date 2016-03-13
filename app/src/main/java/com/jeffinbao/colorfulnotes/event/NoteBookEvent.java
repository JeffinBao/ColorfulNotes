package com.jeffinbao.colorfulnotes.event;

import com.jeffinbao.colorfulnotes.model.NoteBook;

import java.util.List;
import java.util.Map;

/**
 * Author: baojianfeng
 * Date: 2016-01-08
 */
public class NoteBookEvent extends BaseEvent<NoteBook> {

    private NoteBookAction action;

    public NoteBookEvent(List<NoteBook> list, Map<NoteBook, Integer> map) {
        super(list, map);
    }

    public NoteBookEvent(List<NoteBook> list, Map<NoteBook, Integer> map, NoteBookAction action) {
        super(list, map);
        this.action = action;
    }

    public List<NoteBook> getChangedNoteBookList() {
        return list;
    }

    public Map<NoteBook, Integer> getChangedNoteBookMap() {
        return map;
    }

    public NoteBookAction getNoteBookAction() {
        return action;
    }

    public enum NoteBookAction {
        ADD_NEW_NOTE_BOOK,
        UPDATE_NOTE_BOOK
    }
}
