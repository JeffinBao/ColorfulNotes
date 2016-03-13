package com.jeffinbao.colorfulnotes;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

/**
 * Author: baojianfeng
 * Date: 2015-11-26
 */
public class CApplication extends Application {

    private static final String EVERNOTE_CONSUMER_KEY = "";
    private static final String EVERNOTE_CONSUMER_SECRET = "";

    @Override
    public void onCreate() {
        super.onCreate();
        buildEvernoteSession();
    }

    private void buildEvernoteSession() {

        EvernoteSession.EvernoteService service;
        if (BuildConfig.DEBUG) {
            service = EvernoteSession.EvernoteService.SANDBOX;
        } else {
            service = EvernoteSession.EvernoteService.PRODUCTION;
        }

        new EvernoteSession.Builder(this)
                .setEvernoteService(service)
                .setSupportAppLinkedNotebooks(false)
                .build(EVERNOTE_CONSUMER_KEY, EVERNOTE_CONSUMER_SECRET)
                .asSingleton();
    }
}
