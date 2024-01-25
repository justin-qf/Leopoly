package com.app.leopoly.activity.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.leopoly.R;
import com.app.leopoly.adpters.DropDownAdpt;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.databinding.FragmentListBottomListBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;


public class ListBottomFragment extends BottomSheetDialogFragment {

    private ArrayList<String> listModels;
    private int calledFlag;
    private ListBottomFragment context;
    private String titleStr;

    public ListBottomFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            CodeReUse.setWhiteNavigationBar(dialog, getActivity());
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentListBottomListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_bottom_list, container, false);
        View view = binding.getRoot();
        Activity act = getActivity();
        context = this;
        listModels = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            listModels.add("Option " + i);
        }
        DropDownAdpt adpt = new DropDownAdpt(act, listModels, calledFlag);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(act);
        binding.recyclerList.setLayoutManager(mLayoutManager);
        binding.recyclerList.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerList.setAdapter(adpt);
        DropDownAdpt.DismissLayout dismissLayout = new DropDownAdpt.DismissLayout() {
            @Override
            public void onSelection() {
                context.dismiss();
            }
        };
        adpt.setDismissLayout(dismissLayout);

        binding.recyclerList.requestFocus();

        return view;
    }

}
