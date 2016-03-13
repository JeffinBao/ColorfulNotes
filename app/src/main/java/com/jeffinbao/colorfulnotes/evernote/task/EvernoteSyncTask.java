package com.jeffinbao.colorfulnotes.evernote.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMUserException;
import com.jeffinbao.colorfulnotes.BuildConfig;
import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.evernote.Evernote;
import com.jeffinbao.colorfulnotes.model.Note;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Author: baojianfeng
 * Date: 2015-12-22
 */
public class EvernoteSyncTask extends AsyncTask<List<Note>, Void, Void> {

    private Context context;
    private Evernote evernote;

    public EvernoteSyncTask(Context context, Evernote evernote) {
        super();
        this.context = context;
        this.evernote = evernote;
    }

    @Override
    protected void onPreExecute() {
        EventBus.getDefault().post(SyncState.START);
    }

    @Override
    protected Void doInBackground(List<Note>... params) {
        List<Note> notes = params[0];
        if (notes.size() == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.evernote_no_note_in_db, Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            evernote.makeSureNotebookExist(context.getString(R.string.app_name));
        } catch (EDAMUserException e) {
            EDAMErrorCode errorCode = e.getErrorCode();

            switch (errorCode) {
                case AUTH_EXPIRED: {
                    evernote.logout();
                    EventBus.getDefault().post(SyncState.AUTH_EXPIRED);
                    break;
                }
                case PERMISSION_DENIED: {
                    EventBus.getDefault().post(SyncState.PREMISSION_DENIED);
                    break;
                }
                case QUOTA_REACHED: {
                    EventBus.getDefault().post(SyncState.QUOTA_REACHED);
                    break;
                }
                case RATE_LIMIT_REACHED: {
                    if (!BuildConfig.DEBUG) {
                        EventBus.getDefault().post(SyncState.RATE_LIMIT_REACHED);
                    }
                    break;
                }

                default: {
                    EventBus.getDefault().post(SyncState.OTHER_ERROR);
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            evernote.pushNote(notes);
            EventBus.getDefault().post(EvernoteSyncTask.SyncState.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(SyncState.OTHER_ERROR);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public enum SyncState {
        START,
        SUCCESS,
        AUTH_EXPIRED,
        PREMISSION_DENIED,
        QUOTA_REACHED,
        RATE_LIMIT_REACHED,
        OTHER_ERROR
    }
}
