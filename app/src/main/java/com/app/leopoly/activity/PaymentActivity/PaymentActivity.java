package com.app.leopoly.activity.PaymentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.leopoly.BaseActivity;
import com.app.leopoly.R;
import com.app.leopoly.adpters.CustomAdapter;
import com.app.leopoly.adpters.ManagerAdepter;
import com.app.leopoly.common.APIS;
import com.app.leopoly.common.CodeReUse;
import com.app.leopoly.common.Constant;
import com.app.leopoly.common.Utility;
import com.app.leopoly.databinding.ActivityPaymentBinding;
import com.app.leopoly.models.MultiModel;
import com.app.leopoly.models.PaymentModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends BaseActivity {
    private boolean isLoading = false;
    private ArrayList<MultiModel> models;
    private CustomAdapter adapter;
    private MultiModel selectedModel;
    private int isMRorSelf = 0;
    private ArrayList<PaymentModel> dispetchOrderList;

    private ArrayList<MultiModel> mrModels;
    private MultiModel selectedMrModel;
    private boolean isFirstCall = true;
    public static final int PENDING_ORDER_MODE = 0;
    public static final int SUPPLIED_ORDER = 1;

    private ArrayList<MultiModel> managerList;
    private MultiModel selectedManagerModel;


    private ActivityPaymentBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        act = this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(act, R.layout.activity_payment);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);

        setSupportActionBar(binding.toolbar);
        binding.titleToolbar.setText(getString(R.string.dispatch_details));
        binding.backIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        models = new ArrayList<>();

        mrModels = new ArrayList<>();
        managerList = new ArrayList<>();


        if (profile != null) {

            if (Integer.parseInt(profile.getUserType()) == Constant.ADMIN) {
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                binding.mgrLayout.setVisibility(View.VISIBLE);
                binding.mrEdtLayout.setVisibility(View.VISIBLE);
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                getManager(prefManager.getIpAddress() + APIS.ALL_MANAGER, Request.Method.GET);
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.MR) {
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                binding.selfSelection.setChecked(true);
                //load only mr party pass mr id
                //getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
                binding.radioGroup.setVisibility(View.VISIBLE);
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                binding.selfSelection.setChecked(true);
                //by default manager  id  for it's party
                //getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.PARTY) {
                //getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, profile.getUserId(), false);
                binding.partDetailsLayout.setVisibility(View.GONE);
                //   getPartyDetailsById(prefManager.getIpAddress()+APIS.GET_PARTY_BY_ID,profile.getUserId(),Request.Method.POST);
            }

            binding.partyEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.mrSelection.isChecked()) {
                        if (binding.mrEdit.getText().toString().length() == 0)
                            Toast.makeText(act, "Select MR first", Toast.LENGTH_SHORT).show();
                        else
                            showDialog();
                    } else {
                        if (Integer.parseInt(profile.getUserType()) == Constant.ADMIN) {
                            if (binding.mrEdit.getText().toString().length() == 0)
                                Toast.makeText(act, "Select MR first", Toast.LENGTH_SHORT).show();
                            else {
                                showDialog();
                            }
                        } else {
                            showDialog();
                        }
                    }

                }
            });

            binding.mrEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(profile.getUserType()) == Constant.ADMIN) {
                        if (binding.mgrEdit.getText().toString().length() == 0)
                            Toast.makeText(act, "Select Manager first", Toast.LENGTH_SHORT).show();
                        else
                            showMRDialog();
                    } else {
                        showMRDialog();
                    }
                }
            });

            binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (checkedId == binding.selfSelection.getId()) {
                        binding.productTable.removeAllViews();
                        addHeader();

                        isMRorSelf = Constant.MANAGER;
                        binding.mrEdtLayout.setVisibility(View.GONE);
                        isFirstCall = true;
                        refreshData();
                        binding.partyEdit.setText("");
                        binding.mrEdit.setText("");
                        //getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
                    }

                    if (checkedId == binding.mrSelection.getId()) {
                        binding.productTable.removeAllViews();
                        addHeader();
                        isMRorSelf = Constant.MR;

                        binding.partyEdit.setText("");
                        refreshData();
                        binding.partyEdtLayout.setVisibility(View.VISIBLE);
                        binding.mrEdtLayout.setVisibility(View.VISIBLE);
                        if (mrModels == null || mrModels.size() == 0)
                            getMrSelection(prefManager.getIpAddress() + APIS.GET_MR_BYE_MANAGER, profile.getUserId());
                        binding.mrEdit.setText("");
                    }

                }
            });
        }
        binding.viewall.setTag("0");
        binding.viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().toString().equalsIgnoreCase("0")) {
                    v.setTag("1");

                    //getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, "", true);

                    String Message = "<font color='#00AFEF'><u>Last 15 days</u></font>";

                    binding.viewall.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.viewall.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        binding.viewall.setText(Html.fromHtml(Message));
                    }

                } else {
                    v.setTag("0");

                    //getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, "", false);

                    String Message = "<font color='#00AFEF'><u>View all</u></font>";

                    binding.viewall.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.viewall.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        binding.viewall.setText(Html.fromHtml(Message));
                    }

                }
            }
        });


        binding.mgrEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showManagerList();
            }
        });
    }

    private void refreshData() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        binding.dlTxt.setText("");
        binding.addressTxt.setText("");
        binding.gstTxt.setText("");


    }

    public void setPartyDetails(int position) {


        selectedModel = models.get(position);
        binding.dlTxt.setText(selectedModel.getDLNO1());
        String addr = selectedModel.getACADD1();
        if (!selectedModel.getACADD2().isEmpty()) {
            if (addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACADD2();
        }
        if (!selectedModel.getACADD3().isEmpty()) {
            if (addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACADD3();
        }
        if (!selectedModel.getACCITY().isEmpty()) {
            if (addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACCITY();
        }
        binding.addressTxt.setText(addr);

        binding.gstTxt.setText(selectedModel.getGSTNO());

        if (selectedModel.getSupplyModel() != null) {
            Log.e("getSupplyModel", String.valueOf(selectedModel.getSupplyModel().getAMOUNT()));

        }

        //code for link color change

        /*String Message = "<font color='#00AFEF'><u>INV1020</u></font>";
             //   getPartyDetailsById(prefManager.getIpAddress()+APIS.GET_PARTY_BY_ID,profile.getUserId(),Request.Method.POST);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.invoiceNumberTxt.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.invoiceNumberTxt.setText(Html.fromHtml(Message));
        }
         binding.invoiceNumberTxt.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(act, OrderPreviewActivity.class);
                 startActivity(i);
                 overridePendingTransition(R.anim.right_enter, R.anim.left_out);
             }
         });*/

    }

    public void setSinglePartyDetails(MultiModel selectedModel) {
        refreshData();
        this.selectedModel = selectedModel;
        binding.dlTxt.setText(selectedModel.getDLNO1());
        String addr = selectedModel.getACADD1();
        if (!selectedModel.getACADD2().isEmpty()) {
            if (!addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACADD2();
        }
        if (!selectedModel.getACADD3().isEmpty()) {
            if (!addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACADD3();
        }
        if (!selectedModel.getACCITY().isEmpty()) {
            if (!addr.isEmpty())
                addr = addr + ", ";
            addr = addr + selectedModel.getACCITY();
        }
        binding.addressTxt.setText(addr);

        binding.gstTxt.setText(selectedModel.getGSTNO());

        if (selectedModel.getSupplyModel() != null) {
            binding.productTable.removeAllViews();
            addHeader();
            Log.e("getSupplyModel", String.valueOf(selectedModel.getSupplyModel().getAMOUNT()));
            ///addTableRow(selectedModel.getSupplyModel());
        }

    }

    public void addTableRow(PaymentModel selectedModel) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_add_product_layout, null);

        TextView columnOne = rowView.findViewById(R.id.columnOne);
        TextView columnSecond = rowView.findViewById(R.id.columnSecond);
        TextView columnThree = rowView.findViewById(R.id.columnThree);

        float invAmount = Float.parseFloat(selectedModel.getInvoiceAmount());
        columnSecond.setText(selectedModel.getInvoiceAmount());
        //columnSecond.setText(String.valueOf((int)invAmount));

        //columnThree.setText(selectedModel.get());
        String Message = "<font color='#00AFEF'><u>" + selectedModel.getBillNo() + "\n/" + selectedModel.getBillDate() + "</u></font>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            columnOne.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
        } else {
            columnOne.setText(Html.fromHtml(Message));
        }
        invAmount = Float.parseFloat(selectedModel.getOutStandingAmount());
        columnThree.setText(String.valueOf((int) invAmount));


        invAmount = Float.parseFloat(selectedModel.getInvoiceAmount());
        columnSecond.setText(String.valueOf((int) invAmount));


        columnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(act, OrderDetailsActivity.class);
//                i.putExtra("intentAction", OrderDetailsActivity.PAYMENT_VIEW);
//                i.putExtra("object", gson.toJson(selectedModel));
//                startActivity(i);
//                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });
        binding.productTable.addView(rowView);
    }

    private void getManager(String url, int method) {
        if (isLoading)
            return;
        isLoading = true;
        Utility.showLoading(act);
        Log.e("API ", url);
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isLoading = false;
                Utility.dismissLoading();
                Utility.Log("manager- ", response);
//                MultiModel responseModel = ResponseHandler.HandleManagerResponse(response);
//                if (responseModel != null && responseModel.getManagerList() != null && responseModel.getManagerList().size() != 0) {
//                    managerList.addAll(0, responseModel.getManagerList());
//                    Utility.Log("manager- ", " -  " + managerList.size());
//                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Utility.dismissLoading();
                error.printStackTrace();
            }
        }) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map<String, String> getHeaders() {
                Utility.Log("header", getHeader(CodeReUse.GET_FORM_HEADER).toString());
                return getHeader(CodeReUse.GET_FORM_HEADER);

            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("USERTYPE", profile.getUserType());
                map.put("user_id", profile.getUserId());

                Utility.Log("Dispect- ", map.toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

//    private void getParties(String url, int method, String id) {
//        if (isLoading)
//            return;
//        isLoading = true;
//
//        Utility.showLoading(act);
//        ANRequest.MultiPartBuilder request = AndroidNetworking.upload(url)
//                .addHeaders("Accept", "application/json")
//                .addHeaders("Content-Type", "application/json")
//                .addMultipartParameter("USERTYPE", profile.getUserType())
//                .addMultipartParameter("user_id", profile.getUserId())
//                .addMultipartParameter("mr_id", id)
//                .setTag("getPending Order")
//                .setPriority(Priority.HIGH);
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
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        Utility.Log("party- ", response);
//                        MultiModel responseModel = ResponseHandler.handlePartyRespone(response.toString());
//                        if (responseModel != null && responseModel.getPartyList() != null && responseModel.getPartyList().size() != 0)
//                            models = responseModel.getPartyList();
//                    /*    if (models!=null) {
//                            models.addAll(0, responseModel.getPartyList());
//                            adapter.notifyDataSetChanged();
//                            Utility.Log("party- ", " -  "+models.size());
//                        }*/
//
//                        assert responseModel != null;
//                        if (responseModel.getLinks() != null) {
//                            if (responseModel.getLinks().getNextLink() != null && responseModel.getLinks().getNextLink().length() != 0) {
//                                getPagination(responseModel.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), id);
//                            } else {
//                                if (isFirstCall) {
//                                    isFirstCall = false;
//                                    // binding.partyEdit.setText(models.get(0).getParty_name());
//                                    // setPartyDetails(0);
//                                }
//
//                                if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                                    getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, "", false);
//                                }
//                            }
//                        } else {
//                            if (isFirstCall) {
//                                isFirstCall = false;
//                                // binding.partyEdit.setText(models.get(0).getParty_name());
//                                //setPartyDetails(0);
//                            }
//                            if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                                getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, "", false);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        error.printStackTrace();
//                        String json;
//
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
//
//    }
//
//    private void getPagination(String url, String id) {
//        if (isLoading)
//            return;
//        isLoading = true;
//        ANRequest.MultiPartBuilder request = AndroidNetworking.upload(url)
//                .addHeaders("Accept", "application/json")
//                .addHeaders("Content-Type", "application/json")
//                .addMultipartParameter("USERTYPE", profile.getUserType())
//                .addMultipartParameter("user_id", profile.getUserId())
//                .addMultipartParameter("mr_id", id)
//                .setTag("getPending Order")
//                .setPriority(Priority.HIGH);
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
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        Utility.Log("party- ", response);
//                        MultiModel responseModel = ResponseHandler.handlePartyRespone(response.toString());
//                        if (responseModel != null && responseModel.getPartyList() != null && responseModel.getPartyList().size() != 0) {
//                            Collection<MultiModel> data = responseModel.getPartyList();
//                            int size = models.size();
//                            if (data.size() != 0) {
//                                models.addAll(size, data);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                        assert responseModel != null;
//                        if (responseModel.getLinks() != null) {
//
//                            if (responseModel.getLinks().getNextLink() != null && responseModel.getLinks().getNextLink().length() != 0) {
//                                getPagination(responseModel.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), id);
//
//                            } else {
//                                if (isFirstCall) {
//                                    isFirstCall = false;
//                                    //    binding.partyEdit.setText(models.get(0).getParty_name());
//                                    //  setPartyDetails(0);
//                                }
//                            }
//                        } else {
//                            if (isFirstCall) {
//                                isFirstCall = false;
//                                // binding.partyEdit.setText(models.get(0).getParty_name());
//                                // setPartyDetails(0);
//                            }
//
//                        }
//                        if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                            getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, "", false);
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        error.printStackTrace();
//                        String json;
//
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
//
//    private void getPartyDetailsById(String url, String userId, int method) {
//        if (isLoading)
//            return;
//        isLoading = true;
//        Utility.showLoading(act);
//        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                isLoading = false;
//                Utility.dismissLoading();
//                Utility.Log("GET_PARTY_BY_ID- ", response);
//                MultiModel responseModel = ResponseHandler.handlePartyRespone(response);
//                if (responseModel != null && responseModel.getPartyList() != null && responseModel.getPartyList().size() != 0)
//                    setSinglePartyDetails(responseModel.getPartyList().get(0));
//                getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, profile.getUserId(), false);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                isLoading = false;
//                Utility.dismissLoading();
//                error.printStackTrace();
//            }
//        }) {
//            /**
//             * Passing some request headers*
//             */
//            @Override
//            public Map<String, String> getHeaders() {
//                Utility.Log("header", getHeader(CodeReUse.GET_FORM_HEADER).toString());
//                return getHeader(CodeReUse.GET_FORM_HEADER);
//
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("party_id", userId);
//                Log.e("PARAM", map.toString());
//                return map;
//            }
//        };
//
//        LeoPolyApp.getInstance().cancelPendingRequests(Constant.CALL_PARTY_DETAILS);
//        LeoPolyApp.getInstance().addToRequestQueue(stringRequest, Constant.CALL_PARTY_DETAILS);
//    }

//    private void getPaymentStatus(String url, String party_id, boolean isViewAll) {
//        if (isLoading)
//            return;
//        isLoading = true;
//        Utility.showLoading(act);
//
//        ANRequest.MultiPartBuilder request = AndroidNetworking.upload(url)
//                .addHeaders("Accept", "application/json")
//                .addHeaders("Content-Type", "application/json")
//                .addMultipartParameter("USERTYPE", profile.getUserType())
//                .addMultipartParameter("user_id", profile.getUserId())
//                .addMultipartParameter("party_id", party_id)
//                .setTag("get Payment Order")
//                .setPriority(Priority.HIGH);
//
//        if (isViewAll)
//            request.addMultipartParameter("is_fif", "1");
//
//
//        Log.e("DATA", new Gson().toJson(request));
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
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        Log.e("PAyment", response.toString());
//                        dispetchOrderList = ResponseHandler.handlePaymentDetails(response.toString());
//                        setDispatchOrder(0);
//
//
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        isLoading = false;
//                        Utility.dismissLoading();
//                        error.printStackTrace();
//                        String json;
//
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
//
//    }

    private void getMrSelection(String url, String id) {
        if (isLoading)
            return;
        isLoading = true;
        Utility.showLoading(act);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            isLoading = false;
            Utility.dismissLoading();
            Utility.Log("mrSelecttion- ", response);
//            MultiModel responseModel = ResponseHandler.handleMRResponse(response);
//            if (responseModel != null && responseModel.getPartyList() != null && responseModel.getPartyList().size() != 0) {
//                mrModels.addAll(0, responseModel.getPartyList());
//                Utility.Log("size- ", " -  " + mrModels.size());
//            }

        }, error -> {
            isLoading = false;
            Utility.dismissLoading();
            error.printStackTrace();
        }) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("manager_id", id);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void setDispatchOrder(int index) {
        if (dispetchOrderList != null && dispetchOrderList.size() != 0) {
            binding.productTable.removeAllViews();
            addHeader();
            for (int i = index; i < dispetchOrderList.size(); i++) {
                addTableRow(dispetchOrderList.get(i));
            }
        } else {
            binding.viewall.setVisibility(View.GONE);
            binding.productTable.removeAllViews();
            addHeader();
        }
    }

    public void addPendingTable(MultiModel selectedModel) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_add_product_layout, null);

        TextView columnOne = rowView.findViewById(R.id.columnOne);
        TextView columnSecond = rowView.findViewById(R.id.columnSecond);
        TextView columnThree = rowView.findViewById(R.id.columnThree);
        columnOne.setText(selectedModel.getORDERNO());

        //columnThree.setText(selectedModel.getProductType());
        String Message = "<font color='#00AFEF'><u>INV1020</u></font>";

  /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            columnOne.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
        } else {
            columnOne.setText(Html.fromHtml(Message));
        }*/
        columnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.productTable.addView(rowView);
    }

    public void addHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_payment_table_header, null);
        binding.productTable.addView(rowView);
    }

    public void showManagerList() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false);


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        ImageView closeImage = dialogView.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        TextView closeText = dialogView.findViewById(R.id.closeText);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        TextView titleTxt = dialogView.findViewById(R.id.titleTxt);
        titleTxt.setText("Select Manager");
        ListView listView = dialogView.findViewById(R.id.listview);
        final ManagerAdepter listViewAdapter = new ManagerAdepter(act, act, R.layout.item_layout, managerList);
        listView.setAdapter(listViewAdapter);
        ManagerAdepter.handleSelectionEvent handleSelectionEvent = new ManagerAdepter.handleSelectionEvent() {
            @Override
            public void selectionEvent(MultiModel itemmodel, int positino) {
                alertDialog.dismiss();
                refreshData();
                Log.e("POS0-", String.valueOf(positino));
                for (int i = 0; i < managerList.size(); i++) {
                    if (managerList.get(i).getMGRID().equalsIgnoreCase(itemmodel.getMGRID())) {
                        positino = i;
                        break;
                    }
                }
                mrModels.clear();
                selectedManagerModel = managerList.get(positino);
                binding.mgrEdit.setText(selectedManagerModel.getManagerName());
                binding.productTable.removeAllViews();
                addHeader();
                binding.mrEdit.setText("");
                binding.partyEdit.setText("");
                isFirstCall = true;

                getMrSelection(prefManager.getIpAddress() + APIS.GET_MR_BYE_MANAGER, selectedManagerModel.getMGRID());

            }
        };
        listViewAdapter.setInteface(handleSelectionEvent);

        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                listViewAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        alertDialog.show();


    }

    public void showDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false);


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        ImageView closeImage = dialogView.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        TextView closeText = dialogView.findViewById(R.id.closeText);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

//        ListView listView = dialogView.findViewById(R.id.listview);
//        final PartyAdpter listViewAdapter = new PartyAdpter(act, act, R.layout.item_layout, models);
//        listViewAdapter.setPartyMode(true);
//        listView.setAdapter(listViewAdapter);
//        PartyAdpter.handleSelectionEvent handleSelectionEvent = new PartyAdpter.handleSelectionEvent() {
//            @Override
//            public void selectionEvent(MultiModel itemmodel, int positino) {
//                alertDialog.dismiss();
//                refreshData();
//                Log.e("POS0-", String.valueOf(positino));
//                for (int i = 0; i < models.size(); i++) {
//                    if (models.get(i).getParty_id().equalsIgnoreCase(itemmodel.getParty_id())) {
//                        positino = i;
//                        break;
//                    }
//                }
//                Log.e("POS0-", String.valueOf(positino));
//
//                binding.partyEdit.setText(models.get(positino).getParty_name());
//                setPartyDetails(positino);
//                getPaymentStatus(prefManager.getIpAddress() + APIS.GET_PAYMETN_DETAILS, models.get(positino).getParty_id(), false);
//
//            }
//        };
//        listViewAdapter.setInteface(handleSelectionEvent);

        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //listViewAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        alertDialog.show();


    }

    public void showMRDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false);


        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        ImageView closeImage = dialogView.findViewById(R.id.closeImage);
        TextView titleTxt = dialogView.findViewById(R.id.titleTxt);
        titleTxt.setText("Select MR");
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        TextView closeText = dialogView.findViewById(R.id.closeText);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        ListView listView = dialogView.findViewById(R.id.listview);
//        final PartyAdpter listViewAdapter = new PartyAdpter(act, act, R.layout.item_layout, mrModels);
//        listView.setAdapter(listViewAdapter);
//        PartyAdpter.handleSelectionEvent handleSelectionEvent = new PartyAdpter.handleSelectionEvent() {
//            @Override
//            public void selectionEvent(MultiModel itemmodel, int positino) {
//                alertDialog.dismiss();
//                refreshData();
//                models.clear();
//                Log.e("POS0-", String.valueOf(positino));
//                for (int i = 0; i < mrModels.size(); i++) {
//                    if (mrModels.get(i).getMRID().equalsIgnoreCase(itemmodel.getMRID())) {
//                        positino = i;
//                        break;
//                    }
//                }
//
//                Log.e("POS0-", String.valueOf(positino));
//
//                binding.mrEdit.setText(mrModels.get(positino).getParty_name());
//                isFirstCall = true;
//                selectedMrModel = mrModels.get(positino);
//                binding.partyEdit.setText("");
//                getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, selectedMrModel.getMRID());
//            }
//        };
//        listViewAdapter.setInteface(handleSelectionEvent);

        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //listViewAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CodeReUse.activityBackPress(act);
    }
}