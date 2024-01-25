package com.app.leopoly.activity.VerificationActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.common.APIS;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.common.ResponseHandler;
import com.app.leopoly.common.Utility;
import com.app.leopoly.databinding.ActivityVerificationBinding;
import com.app.leopoly.interfaces.OnAlertDialogDismiss;
import com.app.leopoly.models.MultiModel;
import com.app.leopoly.activity.HomeActivity.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VerificationActivity extends BaseActivity implements OnAlertDialogDismiss {
    private Activity act;
    private ActivityVerificationBinding binding;


    private boolean mTimerRunnig;
    private CountDownTimer countDownTimer;
    private long mTimeLeftInMills = START_TIME_IN_MILLIS;
    private static final long START_TIME_IN_MILLIS = 30000;

    private MultiModel profileModel;
    private String OTP;

    // 9879538164 MANAGER
    // 9427215994 MR
    // 9427217292 PARTY

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act=this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding= DataBindingUtil.setContentView(act, R.layout.activity_verification);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);
        setSupportActionBar(binding.toolbar);
        binding.titleToolbar.setText(getString(R.string.verification_Txt));

        binding.numberShow.setText("We sent OTP to verify your number \n" + "+91 "+getIntent().getStringExtra("mobileNumber"));
        OTP=getIntent().getStringExtra("otp");

        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.ResendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp();
            }
        });
        //binding.submitBtn.otp


        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OTP.equalsIgnoreCase(binding.otpEdt.getText().toString())){
                    login();
                }else {
                    Toast.makeText(act, "Otp does not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        counter();
    }

    private void counter() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(mTimeLeftInMills, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

                mTimerRunnig = false;
                binding.ResendText.setVisibility(View.VISIBLE);
                binding.CouterText.setVisibility(View.GONE);
            }
        }.start();

        mTimerRunnig = true;
        binding.CouterText.setVisibility(View.VISIBLE);
        binding.ResendText.setVisibility(View.GONE);
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMills / 1000) / 60;
        int second = (int) (mTimeLeftInMills / 1000) % 60;
        String timeLeftFormeted = String.format(Locale.getDefault(), "%02d:%02d", minutes, second);
        binding.CouterText.setText(timeLeftFormeted);
    }

    /**
     * Passing some request headers*
     */
    private void getOtp() {

        Utility.showLoading(act);
        Log.e("API", prefManager.getIpAddress() + APIS.GET_OTP);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, prefManager.getIpAddress() + APIS.GET_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                 Utility.Log("OTP-Response", response);
                Utility.dismissLoading();
                JSONObject responseJS=ResponseHandler.createJsonObject(response);
                if (ResponseHandler.getBool(responseJS,"status")) {
                    OTP=ResponseHandler.getString(responseJS,"otp");
                    counter();
                }else{
                    Utility.showAlertNew(act,ResponseHandler.getString(responseJS, "message"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Utility.dismissLoading();
                error.printStackTrace();
            }
        }) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map<String, String> getHeaders() {
                Utility.Log("Login-Header", getHeader(CodeReUse.GET_FORM_HEADER).toString());
                return getHeader(CodeReUse.GET_FORM_HEADER);

            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("phone_no", getIntent().getStringExtra("mobileNumber"));
                Utility.Log("Login-Param", hashMap.toString());
                return hashMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }


    private void login() {

        Utility.showLoading(act);
        Log.e("API", prefManager.getIpAddress() + APIS.LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, prefManager.getIpAddress() + APIS.LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utility.Log("prefManager-Login", response);
                Utility.dismissLoading();
                profileModel = ResponseHandler.handleLogin(response);

                if (profileModel != null) {
                    prefManager.setUserProfile(profileModel);
                    prefManager.setLogin(true);
                    Utility.dismissLoading();
                    Intent i = new Intent(act, DashboardActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                    finish();

                } else {
                    Utility.dismissLoading();
                    JSONObject jsonObject = ResponseHandler.createJsonObject(response);
                    if (!ResponseHandler.isSuccess(response, null)) {
                        Utility.showAlert(act, ResponseHandler.getString(jsonObject, "message"));
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Utility.dismissLoading();
                error.printStackTrace();
            }
        }) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map<String, String> getHeaders() {
                Utility.Log("Login-Header", getHeader(CodeReUse.GET_FORM_HEADER).toString());
                return getHeader(CodeReUse.GET_FORM_HEADER);

            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("mobile", getIntent().getStringExtra("mobileNumber"));
                if (getIntent().hasExtra("masterUser"))
                hashMap.put("master_id",getIntent().getStringExtra("masterUser"));


                Utility.Log("Login-Param", hashMap.toString());
                return hashMap;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag("login");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.cancelAll("login");
        queue.add(stringRequest);
    }


    @Override
    public void onDialogDismiss(String flag) {
        CodeReUse.activityBackPress(act);
    }
}