package com.app.leopoly.common;

import android.content.Context;

import com.app.leopoly.helper.HELPER;

import java.util.Observable;

public class AppObserver extends Observable {

    private final Context context;
    private String userName;
    String DefaultName;
    private int nStatusType;
    private String data;

    public AppObserver(Context context) {
        this.context = context;
    }

    public String getData() {
        return data;
    }

    public int getValue() {
        return nStatusType;
    }

    public void setValue(int nStatusTyp) {
        HELPER.print("SET_VALUE", String.valueOf(nStatusTyp));
        this.nStatusType = nStatusTyp;
        setChanged();
        notifyObservers(userName);
    }

    public void setValue(int nStatusTyp, String data) {
        this.nStatusType = nStatusTyp;
        this.data = data;
        setChanged();
        notifyObservers(userName);
    }

}
