package com.jeffinbao.colorfulnotes.event;

import android.support.annotation.Nullable;

import com.jeffinbao.colorfulnotes.model.Note;

/**
 * Author: baojianfeng
 * Date: 2016-01-11
 */
public class NoteEvent {
    private Note note;
    private int position;
    private NoteAction action;

    public NoteEvent(@Nullable Note note, int position, NoteAction action) {
        this.note = note;
        this.position = position;
        this.action = action;
    }

    public Note getChangedNote() {
        return note;
    }

    public int getChangedNotePosition() {
        return position;
    }

    public NoteAction getAction() {
        return action;
    }

    public enum NoteAction {
        CREATE,
        EDIT,
        EDIT_ADD_LOCATION,
        CHANGE_NOTEBOOK,
        DELETE
    }
}
