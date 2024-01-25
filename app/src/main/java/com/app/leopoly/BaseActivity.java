package com.app.leopoly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.app.leopoly.Utils.Utils;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.common.LeoPolyApp;
import com.app.leopoly.common.NetworkChangeReceiver;
import com.app.leopoly.common.PrefManager;
import com.app.leopoly.dialogs.DialogToast;
import com.app.leopoly.models.MultiModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public abstract class BaseActivity extends AppCompatActivity implements Observer {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private static Dialog noInternetConnectionAlertDialog;
    public Activity act;
    public PrefManager prefManager;
    public MultiModel profile;
    public LeoPolyApp leoPolyApp;
    private BroadcastReceiver mNetworkReceiver;

    public Gson gson;
    public static Activity staticAct;
    public static DialogToast noConnectionAlertDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        staticAct = this;
        prefManager = new PrefManager(act);
        gson = new Gson();
        profile = prefManager.getUserProfile();
        // In Activity's onCreate() for instance
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        leoPolyApp = (LeoPolyApp) this.getApplication();
        leoPolyApp.getObserver().addObserver(this);
        noConnectionAlertDialog = new DialogToast(this);
        noInternetConnectionAlertDialog = new Dialog(this);
        noInternetConnectionAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void InternetError(boolean value) {
        if (!staticAct.isDestroyed() && !staticAct.isFinishing()) {
            if (value) {
                if (noInternetConnectionAlertDialog.isShowing()) {
                    noInternetConnectionAlertDialog.dismiss();
                }
            } else {
                showNoConnectionDialog();
            }
        }
    }

    private static void showNoConnectionDialog() {
        if (!noInternetConnectionAlertDialog.isShowing()) {
            noInternetConnectionAlertDialog.setContentView(R.layout.no_connection_dialog);
            noInternetConnectionAlertDialog.setCancelable(false);
            noInternetConnectionAlertDialog.findViewById(R.id.closeBtn).setOnClickListener(view -> {
                noInternetConnectionAlertDialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    staticAct.startActivity(new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY));
                } else {
                    staticAct.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            noInternetConnectionAlertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void update(Observable observable, Object data) {

    }

    @SuppressLint("ClickableViewAccessibility")
    public void hideKeyboard(View view) {

        if (!(view instanceof EditText)) {
            view.setOnTouchListener((View v, MotionEvent event) -> {
                Utils.INSTANCE.hideKeyboard(act);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboard(innerView);
            }
        }
    }

    protected Map<String, String> getHeader(int flag) {
        Map<String, String> headers = new HashMap<>();
        if (flag == CodeReUse.GET_JSON_HEADER) {
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");
        } else {
            headers.put("Accept", "application/x-www-form-urlencoded");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
        }

        return headers;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
