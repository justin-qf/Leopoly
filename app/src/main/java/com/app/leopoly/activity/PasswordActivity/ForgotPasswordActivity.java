package com.app.leopoly.activity.PasswordActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.databinding.ActivityForgotPasswordBinding;
import com.app.leopoly.activity.VerificationActivity.VerificationActivity;

public class ForgotPasswordActivity extends BaseActivity {
    private Activity act;
    private ActivityForgotPasswordBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        act=this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(act, R.layout.activity_forgot_password);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);

        setSupportActionBar(binding.toolbar);
        binding.titleToolbar.setText(getString(R.string.forgot_pass));
        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(act, VerificationActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });

    }

    @Override
    public void onBackPressed() {
        CodeReUse.activityBackPress(act);
    }
}