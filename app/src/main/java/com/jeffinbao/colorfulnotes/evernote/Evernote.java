package com.jeffinbao.colorfulnotes.evernote;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.User;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;
import com.jeffinbao.colorfulnotes.utils.TimeUtil;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-11-26
 */
public class Evernote {
    private static final String TAG = "evernote";

    private Context context;
    private EvernoteSession session;
    private FinalDb db;

    public Evernote(Context context, FinalDb db) {
        this.context = context;
        this.db = db;
        session = EvernoteSession.getInstance();
    }

    public void authLogin(Activity activity) {
        if (activity == null) {
            return;
        }

        session.authenticate(activity);
    }

    public boolean isLoggedIn() {
        return session != null && session.isLoggedIn();
    }

    public void logout() {
        session.logOut();
    }

    public void getUser(EvernoteCallback<User> callback) {
        session.getEvernoteClientFactory().getUserStoreClient().getUserAsync(callback);
    }

    public void pushNote(List<com.jeffinbao.colorfulnotes.model.Note> notes) throws Exception {
        for (com.jeffinbao.colorfulnotes.model.Note note : notes) {
            if (note.isFirstPush()) {
                createEvernote(note);
            } else {
                updateEvernote(note);
            }
        }
    }

    private List<Notebook> getEvernotebookList() {
        List<Notebook> list = new ArrayList<>();

        try {
            list = session.getEvernoteClientFactory().getNoteStoreClient().listNotebooks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void makeSureNotebookExist(@NonNull String notebookName) throws Exception {
        String guid =  PreferenceUtil.getDefault(context).getString(NConstants.EVERNOTE_NOTEBOOK_GUID);

        if (!TextUtils.isEmpty(guid)) {
            Notebook notebook = findEvernoteBook(guid);

            if (null != notebook && TextUtils.equals(notebook.getName(), notebookName)) {
                PreferenceUtil.getDefault(context).putString(NConstants.EVERNOTE_NOTEBOOK_GUID, notebook.getGuid());

            } else {
                createEvernoteBook(notebookName);
            }
        } else {
            createEvernoteBook(notebookName);
        }
    }

    private Notebook findEvernoteBook(String guid) throws Exception {
        Notebook notebook;

        try {
            notebook = session.getEvernoteClientFactory().getNoteStoreClient().getNotebook(guid);
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
            notebook = null;
        }

        return notebook;
    }

    private boolean createEvernoteBook(@NonNull String notebookName) {
        Notebook notebook = new Notebook();
        notebook.setName(notebookName);

        boolean result = false;
        try {
            Notebook resultNotebook = session.getEvernoteClientFactory().getNoteStoreClient().createNotebook(notebook);

            PreferenceUtil.getDefault(context).putString(NConstants.EVERNOTE_NOTEBOOK_GUID, resultNotebook.getGuid());
        } catch (EDAMUserException e) {
            if (e.getErrorCode() == EDAMErrorCode.DATA_CONFLICT) {
                List<Notebook> notebooks = getEvernotebookList();
                for (Notebook notebook1 : notebooks) {
                    if (TextUtils.equals(notebook1.getName(), notebookName)) {
                        PreferenceUtil.getDefault(context).putString(NConstants.EVERNOTE_NOTEBOOK_GUID, notebook1.getGuid());
                    }
                }
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private Note createEvernote(com.jeffinbao.colorfulnotes.model.Note note) throws Exception {
        if (null == note) {
            return null;
        }

        Note evernote = note.toEvernote();
        evernote.setNotebookGuid(PreferenceUtil.getDefault(context).getString(NConstants.EVERNOTE_NOTEBOOK_GUID));
        evernote.setActive(true);

        Log.d(TAG, "create--evernotebook guid: " + PreferenceUtil.getDefault(context).getString(NConstants.EVERNOTE_NOTEBOOK_GUID));

        Note resultEvernote = session.getEvernoteClientFactory().getNoteStoreClient().createNote(evernote);
        if (null == resultEvernote) {
            return null;
        }

        Log.d(TAG, "create--evernote guid: " + resultEvernote.getGuid());
        note.setGuid(resultEvernote.getGuid());
        db.update(note);

        return resultEvernote;
    }

    private Note updateEvernote(com.jeffinbao.colorfulnotes.model.Note note) throws Exception {
        Note updateEvernote = note.toEvernote();
        updateEvernote.setGuid(note.getGuid());
        updateEvernote.setActive(true);

        Log.d(TAG, "update--note guid: " + note.getGuid());
        Note result = session.getEvernoteClientFactory().getNoteStoreClient().updateNote(updateEvernote);
        note.setLastUpdateTimeInLong(result.getUpdated());
        note.setLastUpdateTime(TimeUtil.convertTimeLongToString(result.getUpdated()));
        db.update(note);

        return result;
    }

}
