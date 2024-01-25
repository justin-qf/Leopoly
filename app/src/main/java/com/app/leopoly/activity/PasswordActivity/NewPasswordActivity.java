package com.app.leopoly.activity.PasswordActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.databinding.ActivityNewPasswordBinding;

public class NewPasswordActivity extends BaseActivity {
    private Activity act;
    private ActivityNewPasswordBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        act = this;

        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding = DataBindingUtil.setContentView(act, R.layout.activity_new_password);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);
        setSupportActionBar(binding.toolbar);


        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.titleToolbar.setText(getString(R.string.reset_password_Txt));
    }
    @Override
    public void onBackPressed() {

        Intent setIntent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        finish();
    }
}