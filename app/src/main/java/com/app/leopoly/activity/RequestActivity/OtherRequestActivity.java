package com.app.leopoly.activity.RequestActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.databinding.ActivityOtherRequestBinding;
import com.app.leopoly.databinding.DialogFormRequestBinding;
import com.app.leopoly.interfaces.OnAlertDialogDismiss;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OtherRequestActivity extends BaseActivity implements OnAlertDialogDismiss {
    private ActivityOtherRequestBinding binding;
    private boolean isLoading=false;
    private AlertDialog alertDialog;
    private DialogFormRequestBinding reqBinding;

    public static int INV_REQUEST=0;
    public static int OS_REQUEST=1;
    public static int LEFER_REQUEST=2;
    public static int DL_REQUEST=3;
    public static int ADDRESS_REQ=4;
    public static int MOBILE_REQ=5;
    public static int EMAIL_REQ=6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act=this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.setContentView(act, R.layout.activity_other_request);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);
        setSupportActionBar(binding.toolbar);
        binding.titleToolbar.setText(getString(R.string.party_request_title));
        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.reqInvoiceNoTxr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(INV_REQUEST);
            }
        });

        binding.osReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(OS_REQUEST);
            }
        });

        binding.lefferReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(LEFER_REQUEST);
            }
        });
        binding.dlReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(DL_REQUEST);
            }
        });

        binding.addrReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(ADDRESS_REQ);
            }
        });
        binding.mobileReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(MOBILE_REQ);
            }
        });
        binding.emailReqLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestForm(EMAIL_REQ);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CodeReUse.activityBackPress(act);
    }

    public void showRequestForm(int type){

            if (alertDialog != null && alertDialog.isShowing())
                alertDialog.dismiss();

        reqBinding = DataBindingUtil.inflate(LayoutInflater.from(act), R.layout.dialog_form_request, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(act, R.style.MyAlertDialogStyle_extend);
            builder.setView(reqBinding.getRoot());
            alertDialog = builder.create();
            alertDialog.setContentView(reqBinding.getRoot());

            if (type == INV_REQUEST){
                reqBinding.invRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Invoice Request");
            }else if (type == OS_REQUEST){
                reqBinding.osRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("O/S Request");
            }else if (type == LEFER_REQUEST){
                reqBinding.ledferRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Ledger Copy");
            }else if (type == DL_REQUEST){
                reqBinding.dlRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Update DL No.");
            }else if (type == ADDRESS_REQ){
                reqBinding.addrRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Update Address");
            }else if (type == MOBILE_REQ){
                reqBinding.mobileRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Update Mobile No.");
            }else if (type == EMAIL_REQ){
                reqBinding.emailRequestLayout.setVisibility(View.VISIBLE);
                reqBinding.titleTxt.setText("Update Email-Id");
            }



            reqBinding.fromDateEdt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CodeReUse.datePicker(act, reqBinding.fromDateEdt);
                    }
                });
            reqBinding.endDateEdt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CodeReUse.datePicker(act, reqBinding.endDateEdt);
                }
            });


        CodeReUse.RemoveError(reqBinding.invEdt, reqBinding.invTxtLayout);
        CodeReUse.RemoveError(reqBinding.osEdt, reqBinding.osTxtLayout);
        CodeReUse.RemoveError(reqBinding.fromDateEdt, reqBinding.fromDateTxtLayout);
        CodeReUse.RemoveError(reqBinding.endDateEdt, reqBinding.endDateTxtLayout);
        CodeReUse.RemoveError(reqBinding.DLEdt, reqBinding.DLTxtLayout);
        CodeReUse.RemoveError(reqBinding.addressEdt, reqBinding.addressTxtLayout);
        CodeReUse.RemoveError(reqBinding.mobileEdt, reqBinding.mobileTxtLayout);
        CodeReUse.RemoveError(reqBinding.emailEdt, reqBinding.emailTxtLayout);

        reqBinding.AmenitiesSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CodeReUse.hideKeyboard(act, v);
                    reqBinding.scrollView.smoothScrollTo(0, 0);
                    boolean isError = false;
                    boolean isFocus = false;
                    String param1="",param2="";

                    if (type == INV_REQUEST){

                        if (reqBinding.invEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.invTxtLayout.setError(getString(R.string.inv_no_error));
                            isFocus = true;
                            reqBinding.invEdt.requestFocus();

                        }else {
                            param1=reqBinding.invEdt.getText().toString();
                        }

                    }else if (type == OS_REQUEST){

                        if (reqBinding.osEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.osTxtLayout.setError(getString(R.string.os_report_error));
                            isFocus = true;
                            reqBinding.osEdt.requestFocus();

                        }else {
                            param1=reqBinding.osEdt.getText().toString();
                        }

                    }else if (type == LEFER_REQUEST){

                        if (reqBinding.fromDateEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.fromDateTxtLayout.setError(getString(R.string.from_date_error));
                            isFocus = true;
                            reqBinding.fromDateEdt.requestFocus();

                        }else {
                            param1=reqBinding.fromDateEdt.getText().toString();
                        }

                        if (reqBinding.endDateEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.endDateTxtLayout.setError(getString(R.string.end_date_error));
                            isFocus = true;
                            reqBinding.endDateEdt.requestFocus();

                        }else {
                            param2=reqBinding.endDateEdt.getText().toString();
                        }

                    }else if (type == DL_REQUEST){
                        if (reqBinding.DLEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.DLTxtLayout.setError(getString(R.string.dl_error));
                            isFocus = true;
                            reqBinding.DLEdt.requestFocus();

                        }else {
                            param1=reqBinding.DLEdt.getText().toString();
                        }

                    }else if (type == ADDRESS_REQ){
                        if (reqBinding.addressEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.addressTxtLayout.setError(getString(R.string.address_error));
                            isFocus = true;
                            reqBinding.addressEdt.requestFocus();

                        }else {
                            param1=reqBinding.addressEdt.getText().toString();
                        }

                    }else if (type == MOBILE_REQ){
                        if (reqBinding.mobileEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.mobileTxtLayout.setError(getString(R.string.conact_error));
                            isFocus = true;
                            reqBinding.mobileEdt.requestFocus();

                        }else if (!CodeReUse.isContactValid(reqBinding.mobileEdt.getText().toString())) {
                            isError = true;
                            reqBinding.mobileEdt.setError(getString(R.string.invalid_mobile_error));
                            isFocus = true;
                            reqBinding.mobileEdt.requestFocus();
                        }else {
                            param1=reqBinding.mobileEdt.getText().toString();
                        }

                    }else if (type == EMAIL_REQ){
                        if (reqBinding.emailEdt.getText().toString().length() == 0) {
                            isError = true;
                            reqBinding.emailTxtLayout.setError(getString(R.string.email_error));
                            isFocus = true;
                            reqBinding.emailEdt.requestFocus();
                        }else  if (!CodeReUse.isEmailValid(reqBinding.emailEdt.getText().toString())) {
                            isError = true;
                            reqBinding.emailTxtLayout.setError(getString(R.string.email_id_error));
                            isFocus = true;
                            reqBinding.emailEdt.requestFocus();
                        }
                        else {
                            param1=reqBinding.emailEdt.getText().toString();
                        }
                    }


                    if (!isError) {
                        //addPartyRequest(type,param1,param2);
                         alertDialog.dismiss();
                    }

                }
            });
        reqBinding.dialogCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
            alertDialog.show();

    }



    public void datePicker(Activity act, TextInputEditText editText) {

        DatePickerDialog picker;
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        picker = new DatePickerDialog(act,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = (month + 1);
                        String dateStr = "";
                        String monthStr = "";
                        if (dayOfMonth < 10) {
                            dateStr = "0" + dayOfMonth;
                        } else {
                            dateStr = String.valueOf(dayOfMonth);
                        }

                        if (month < 10) {
                            monthStr = "0" + month;
                        } else {
                            monthStr = String.valueOf(month);
                        }

                        if (reqBinding.fromDateEdt.getId() != editText.getId()) {
                            if (compareDate2(reqBinding.fromDateEdt.getText().toString(),(dateStr + "-" + monthStr + "-" + year))) {
                                editText.setText(dateStr + "-" + monthStr + "-" + year);
                            } else {
                                Toast.makeText(OtherRequestActivity.this, "Please Choose Next Date To Schedule Date 1", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
                            String currentDateandTime = formatDate(sd.format(new Date()));

                            if (compareDate(currentDateandTime,(dateStr + "-" + monthStr + "-" + year))){
                                editText.setText(dateStr + "-" + monthStr + "-" + year);
                            } else {
                                Toast.makeText(OtherRequestActivity.this, "Please choose valid date", Toast.LENGTH_SHORT).show();
                            }

                        }


                        Log.e("SELECT_DATE", dateStr + "-" + monthStr + "-" + year);
                    }

                }, year, month, day);

        picker.getDatePicker().setCalendarViewShown(false);
        picker.getDatePicker().setSpinnersShown(true);

        picker.show();
    }


    public static boolean compareDate(String fromDate,String toDate) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date currentDate = fmt.parse(toDate);
            Date examDate = fmt.parse(fromDate);
            Log.e("DATE-",currentDate+"------ "+examDate);
            assert currentDate != null;
            if (currentDate.compareTo(examDate) == 0) {
                return true;
            }
            if (currentDate.compareTo(examDate) > 0) {
                return true;
            }
            if (currentDate.compareTo(examDate) < 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }




    //functino for compare date
    public static boolean compareDate2(String fromDate,String toDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        try {

            Date todateD = fmt.parse(toDate);
            Date fromDateD = fmt.parse(fromDate);
            assert todateD != null;
            if (todateD.compareTo(fromDateD) == 0) {
                return false;
            }
            if (todateD.compareTo(fromDateD) > 0) {
                return true;
            }
            if (todateD.compareTo(fromDateD) < 0) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    @SuppressLint("SimpleDateFormat")
    private String formatDate(String dateString) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date d = sd.parse(dateString);
            sd = new SimpleDateFormat("dd/MM/yyyy");
            assert d != null;
            return sd.format(d).replace("/", "-");
        } catch (ParseException ignored) {
        }
        return "";
    }




//    private void addPartyRequest(int type,String param1,String param2) {
//        Utility.showLoading(act);
//
//        ANRequest.MultiPartBuilder request = AndroidNetworking.upload(prefManager.getIpAddress()+APIS.ADD_PARTY_REQUEST)
//                .addHeaders("Accept", "application/json")
//                .addHeaders("Content-Type", "application/json")
//                .addMultipartParameter("USERTYPE", profile.getUserType())
//                .addMultipartParameter("user_id",profile.getUserId())
//                .setTag("Add Party Request")
//                .setPriority(Priority.HIGH);
//
//        request.addMultipartParameter("party_id",profile.getUserId());
//
//        if (type == INV_REQUEST){
//            request.addMultipartParameter("invoice_req",param1);
//        }else if (type == OS_REQUEST){
//            request.addMultipartParameter("o_s_req",param1);
//        }else if (type == LEFER_REQUEST){
//            request.addMultipartParameter("ledfer_copy_from",param1);
//            request.addMultipartParameter("ledfer_copy_to",param2);
//        }else if (type == DL_REQUEST){
//            request.addMultipartParameter("update_dl",param1);
//        }else if (type == ADDRESS_REQ){
//            request.addMultipartParameter("address_update",param1);
//        }else if (type == MOBILE_REQ){
//            request.addMultipartParameter("mobileno_update",param1);
//        }else if (type == EMAIL_REQ){
//            request.addMultipartParameter("email_id_update",param1);
//        }
//        Log.e("request : ",gson.toJson(request));
//
//
//        request.build().setUploadProgressListener(new UploadProgressListener() {
//            @Override
//            public void onProgress(long bytesUploaded, long totalBytes) {
//                // do anything with progress
//            }
//        })
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Utility.dismissLoading();
//                        isLoading = false;
//                        Utility.Log("ADD_PARTY_REQUEST- ", response);
//                        Utility.showAlert(act,ResponseHandler.getString(response,"message"),"Flag");
//
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        error.printStackTrace();
//                        Log.e("onError errorDetail : ",gson.toJson(error));
//                        if (error.getErrorCode() != 0) {
//                            Log.e("onError errorCode : ", String.valueOf(error.getErrorCode()));
//                            Log.e("onError errorBody : ", error.getErrorBody());
//                            Log.e("onError errorDetail : ", error.getErrorDetail());
//                        } else {
//                            Log.e("onError errorDetail : ", error.getErrorDetail());
//
//                        }
//                    }
//                });
//
//    }

    @Override
    public void onDialogDismiss(String flag) {

    }
}