package com.app.leopoly.activity.SupplyActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.databinding.ActivitySupplyTypeBinding;

public class SupplyTypeActivity extends BaseActivity {
    private ActivitySupplyTypeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        act=this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(act, R.layout.activity_supply_type);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);

        setSupportActionBar(binding.toolbar);
        binding.titleToolbar.setText(getString(R.string.packagin_list));
        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.viewPendingSupply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act, SupplyActivity.class);
                i.putExtra("ActivityType",SupplyActivity.PENDING_ORDER_MODE);
                startActivity(i);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });


        binding.viewLastSupply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act, SupplyActivity.class);
                i.putExtra("ActivityType",SupplyActivity.SUPPLIED_ORDER);
                startActivity(i);

                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });

    /*    binding.viewPaymentStatys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(act, PaymentActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        CodeReUse.activityBackPress(act);
    }
}