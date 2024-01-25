package com.app.leopoly.activity.PasswordActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends BaseActivity {
    private Activity act;
    private ActivityChangePasswordBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act=this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        binding = DataBindingUtil.setContentView(act, R.layout.activity_change_password);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);
        setSupportActionBar(binding.toolbar);
        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.titleToolbar.setText(getString(R.string.change_password));

    }

    @Override
    public void onBackPressed() {
        CodeReUse.activityBackPress(act);
    }
}