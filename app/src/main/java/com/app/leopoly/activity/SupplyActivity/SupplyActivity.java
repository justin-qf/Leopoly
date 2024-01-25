package com.app.leopoly.activity.SupplyActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.app.leopoly.databinding.ActivitySupplyBinding;
import com.app.leopoly.helper.HELPER;
import com.app.leopoly.models.MultiModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SupplyActivity extends BaseActivity {

    private Activity act;
    private ActivitySupplyBinding binding;
    private boolean isLoading = false;
    private ArrayList<MultiModel> models;
    private CustomAdapter adapter;
    private MultiModel selectedModel;
    private int isMRorSelf = 0;
    private ArrayList<MultiModel> dispetchOrderList;
    private CustomAdapter mrAdapter;
    private ArrayList<MultiModel> mrModels;
    private MultiModel selectedMrModel;
    private boolean isFirstCall = true;
    public static final int PENDING_ORDER_MODE = 0;
    public static final int SUPPLIED_ORDER = 1;
    public int viewMode = -1;


    private ArrayList<MultiModel> managerList;
    private MultiModel selectedManagerModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(act, R.layout.activity_supply);
        binding.toolbar.setTitle("");
        binding.toolbar.setTitleMarginStart(0);
        setSupportActionBar(binding.toolbar);
        viewMode = getIntent().getIntExtra("ActivityType", -1);

        if (viewMode == PENDING_ORDER_MODE) {
            binding.titleToolbar.setText(getString(R.string.pending_order_title));
            binding.titleProduct.setText("Pending Orders");
        } else {
            binding.titleToolbar.setText(getString(R.string.packagin_list));
            String Message = "<font color='#00AFEF'><u>View all</u></font>";

            binding.viewall.setVisibility(View.GONE);
            binding.titleProduct.setText("Last 15 Day " + getString(R.string.packagin_list));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.viewall.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
            } else {
                binding.viewall.setText(Html.fromHtml(Message));
            }

        }
        binding.productTable.removeAllViews();
        binding.disProductTable.removeAllViews();
        if (viewMode == PENDING_ORDER_MODE)
            addHeader(0);
        else
            addHeaderDispetch(1);
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

                getManager(prefManager.getIpAddress() + APIS.ALL_MANAGER, Request.Method.GET);
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.MR) {
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                //load only mr party pass mr id
                //getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
                binding.radioGroup.setVisibility(View.VISIBLE);
                binding.partyEdtLayout.setVisibility(View.VISIBLE);
                //by default manager  id  for it's party
                if (viewMode == PENDING_ORDER_MODE)
                    //getPendingOrders(prefManager.getIpAddress() + APIS.GET_PENDING_ORDER, Request.Method.POST, "");

               // getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
                binding.selfSelection.setChecked(true);
            }
            if (Integer.parseInt(profile.getUserType()) == Constant.PARTY) {
                binding.partyDetailsLayout.setVisibility(View.GONE);

                if (viewMode == PENDING_ORDER_MODE)
                    HELPER.print("","");
                    //getPendingOrders(prefManager.getIpAddress() + APIS.GET_PENDING_ORDER, Request.Method.POST, profile.getUserId());
                else {
                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, profile.getUserId(), false);
                }
                binding.selfSelection.setChecked(true);
                // getPartyDetailsById(prefManager.getIpAddress()+APIS.GET_PARTY_BY_ID,profile.getUserId(),Request.Method.POST);
            }

            binding.partyEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.mrSelection.isChecked()) {
                        if (binding.mrEdit.getText().toString().length() == 0)
                            Toast.makeText(act, "Select MR first", Toast.LENGTH_SHORT).show();
                        else
                            showDialog();
                    } else
                        showDialog();
                }
            });

         /*   binding.partyEdt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("getParty_id", String.valueOf(models.get(position).getParty_id()));
                    refreshData();
                    setPartyDetails(position);
                    if (viewMode == PENDING_ORDER_MODE) {
                        getPendingOrders(prefManager.getIpAddress()+APIS.GET_PENDING_ORDER,Request.Method.POST,models.get(position).getParty_id());
                        //getDispetchOrder(prefManager.getIpAddress()+APIS.GET_DISPETCH_ORDER,Request.Method.POST,models.get(position).getParty_id());
                    }
                    else
                        getDispetchOrder(prefManager.getIpAddress()+APIS.GET_DISPETCH_ORDER,Request.Method.POST,models.get(position).getParty_id(),false);
                }
            });*/
          /*  binding.mrEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //load selected mr party
                    showMRDialog();

                }
            });*/
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
                        isFirstCall = true;
                        isMRorSelf = Constant.MANAGER;
                        binding.mrEdtLayout.setVisibility(View.GONE);
                        binding.productTable.removeAllViews();
                        binding.disProductTable.removeAllViews();
                        if (viewMode == PENDING_ORDER_MODE)
                            addHeader(0);
                        else
                            addHeaderDispetch(1);
                        refreshData();
                        binding.partyEdit.setText("");
                        binding.mrEdit.setText("");
                        //getParties(prefManager.getIpAddress() + APIS.GET_ALL_PARTY, Request.Method.POST, "");
                    }

                    if (checkedId == binding.mrSelection.getId()) {
                        //  if (request!=null && request)
                        binding.productTable.removeAllViews();
                        binding.disProductTable.removeAllViews();
                        //AndroidNetworking.cancelAll();
                        if (viewMode == PENDING_ORDER_MODE)
                            addHeader(0);
                        else
                            addHeaderDispetch(1);
                        isMRorSelf = Constant.MR;
                        binding.partyEdit.setText("");
                        refreshData();
                        binding.partyEdtLayout.setVisibility(View.VISIBLE);
                        binding.mrEdtLayout.setVisibility(View.VISIBLE);
                        if (mrModels == null || mrModels.size() == 0)
                            getMrSelection(prefManager.getIpAddress() + APIS.GET_MR_BYE_MANAGER, Request.Method.POST, profile.getUserId());
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

                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, "", true);

                    String Message = "<font color='#00AFEF'><u>Last 15 days</u></font>";

                    binding.viewall.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.viewall.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        binding.viewall.setText(Html.fromHtml(Message));
                    }

                } else {
                    v.setTag("0");

                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, "", false);

                    String Message = "<font color='#00AFEF'><u>View all</u></font>";

                    binding.viewall.setVisibility(View.GONE);

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

/*    public void setSinglePartyDetails(MultiModel selectedModel) {
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
            binding.disProductTable.removeAllViews();
            if (viewMode == PENDING_ORDER_MODE)
                addHeader(0);
            else
                addHeader(1);
            Log.e("getSupplyModel", String.valueOf(selectedModel.getSupplyModel().getAMOUNT()));
            ///addTableRow(selectedModel.getSupplyModel());
        }

    }*/


    //load dispatch adepter
    public void addTableRow(MultiModel selectedModel) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_supply_details_add_product_layout, null);

        TextView columnOne = rowView.findViewById(R.id.columnOne);
        TextView columnSecond = rowView.findViewById(R.id.columnSecond);
        TextView columnThree = rowView.findViewById(R.id.columnThree);

        TextView columnLrNo = rowView.findViewById(R.id.columnLrNo);
        TextView columnLrDate = rowView.findViewById(R.id.columnLrDate);
        TextView columnTransport = rowView.findViewById(R.id.columnTransport);
        TextView columnParcel = rowView.findViewById(R.id.columnParcel);

        columnLrNo.setText(selectedModel.getLRNO());
        columnLrDate.setText(selectedModel.getLRDT());
        columnTransport.setText(selectedModel.getTRANSPORT());
        columnParcel.setText(selectedModel.getPARCELS());
        columnSecond.setText(selectedModel.getAMOUNT());

        float invAmount = Float.parseFloat(selectedModel.getAMOUNT());
        columnSecond.setText(String.valueOf((int) invAmount));

        columnThree.setText(selectedModel.getWEBLINNK());


        //columnThree.setText(selectedModel.getProductType());
        String Message = "<font color='#00AFEF'><u>" + selectedModel.getINVNO() + "\n/" + selectedModel.getINVDATE() + "</u></font>";
        String linkStr = "<font color='#00AFEF'><u>" + selectedModel.getWEBLINNK() + "</u></font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            columnOne.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
            columnThree.setText(Html.fromHtml(linkStr, Html.FROM_HTML_MODE_COMPACT));
        } else {
            columnOne.setText(Html.fromHtml(Message));
            columnThree.setText(Html.fromHtml(linkStr));
        }

        columnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (!selectedModel.getWEBLINNK().contains("http://"))
                    selectedModel.setWEBLINNK("http://" + selectedModel.getWEBLINNK());

                i.setData(Uri.parse(selectedModel.getWEBLINNK()));
                startActivity(i);
            }
        });

        columnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(act, OrderDetailsActivity.class);
//                i.putExtra("intentAction", OrderDetailsActivity.ORDER_HISTORY);
//                String json = gson.toJson(selectedModel);
//
//                i.putExtra("object", json);
//                startActivity(i);
//                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });
        binding.disProductTable.addView(rowView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CodeReUse.activityBackPress(act);
    }

//    private void getParties(String url, int method, String id) {
//        if (isLoading)
//            return;
//        isLoading = true;
//        selectedModel = null;
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
//
//
//                        assert responseModel != null;
//                        if (responseModel.getLinks() != null) {
//                            if (responseModel.getLinks().getNextLink() != null && responseModel.getLinks().getNextLink().length() != 0) {
//                                getPagination(responseModel.getLinks().getNextLink().replace("localhost", prefManager.getIpAddress()), id);
//                            } else {
//                                if (isFirstCall) {
//                                    isFirstCall = false;
//                                    // binding.partyEdit.setText(models.get(0).getParty_name());
//                                    //  setPartyDetails(0);
//                                }
//
//                                if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                                    if (viewMode != PENDING_ORDER_MODE)
//                                        getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, id, false);
//                                }
//                            }
//                        } else {
//                            if (isFirstCall) {
//                                isFirstCall = false;
//                                //  binding.partyEdit.setText(models.get(0).getParty_name());
//                                // setPartyDetails(0);
//                            }
//                            if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                                if (viewMode != PENDING_ORDER_MODE)
//                                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, id, false);
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
//                            Log.e("vgetParties  : ", String.valueOf(error.getErrorCode()));
//                            Log.e("onError errorBody : ", error.getErrorBody());
//                            Log.e("onError errorDetail : ", error.getErrorDetail());
//                        } else {
//                            Log.e("getParties : ", error.getErrorDetail());
//                        }
//                    }
//                });
//
//
//    }

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
//
//                        assert responseModel != null;
//                        if (responseModel.getLinks() != null) {
//
//                            if (responseModel.getLinks().getNextLink() != null && responseModel.getLinks().getNextLink().length() != 0) {
//                                getPagination(responseModel.getLinks().getNextLink().replace("localhost", prefManager.getIpAddress()), id);
//                            } else {
//                                if (isFirstCall) {
//                                    isFirstCall = false;
//                                    // binding.partyEdit.setText(models.get(0).getParty_name());
//                                    //  setPartyDetails(0);
//                                }
//                            }
//                        } else {
//                            if (isFirstCall) {
//                                isFirstCall = false;
//                                // binding.partyEdit.setText(models.get(0).getParty_name());
//                                //  setPartyDetails(0);
//                            }
//                            if (Integer.parseInt(profile.getUserType()) == Constant.MANAGER) {
//                                if (viewMode != PENDING_ORDER_MODE)
//                                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, id, false);
//                            }
//                        }
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
//    }


    private void getDispetchOrder(String url, int method, String party_id, boolean isViewAll) {
        if (isLoading)
            return;
        isLoading = true;
        Utility.showLoading(act);
        Log.e("DIS--", url);
        Log.e("party_id", party_id);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        isLoading = false;
                        Utility.dismissLoading();
                        Log.e("Res-Dispatch", response.toString());
//                        MultiModel model = ResponseHandler.handleDispetchList(response.toString());
//                        dispetchOrderList = model.getArrayList();
//                        setDispatchOrder(0, false);
//                        if (model.getLinks() != null) {
//                            Log.e("data", "" + gson.toJson(model.getLinks()));
//                            if (model.getLinks().getNextLink() != null && model.getLinks().getNextLink().length() != 0) {
//                                getDispetchOrderNextPage(model.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), party_id);
//                            }
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Utility.dismissLoading();
                error.printStackTrace();
            }
        }) {

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("USERTYPE", profile.getUserType());
                params.put("user_id", profile.getUserId());
                params.put("party_id", party_id);
                params.put("is_fif", "1");
                Log.e("response", "" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
               // params.put("Accept", "application/json");
                return params;
            }
        };
        myReq.setShouldCache(false);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(act);
        //queue.getCache().clear();
        queue.add(myReq);


    }

    private void getDispetchOrderNextPage(String url, String party_id) {
        if (isLoading)
            return;
        isLoading = true;
        Log.e("DIS--", url);
        Log.e("party_id", party_id);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isLoading = false;
                        Utility.dismissLoading();

//                        MultiModel model = ResponseHandler.handleDispetchList(response.toString());
//                        Utility.Log("getDispetchOrderNextPage- ", model.getArrayList().get(0).getINVNO());
//                        int lastIndex = dispetchOrderList.size();
//                        dispetchOrderList.addAll(model.getArrayList());
//
//                        setDispatchOrder(lastIndex, true);
//                        if (model.getLinks() != null) {
//                            Log.e("data", "" + gson.toJson(model.getLinks()));
//                            if (model.getLinks().getNextLink() != null && model.getLinks().getNextLink().length() != 0) {
//                                 getDispetchOrderNextPage(model.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), party_id);
//                            }
//                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Utility.dismissLoading();
                error.printStackTrace();
            }
        }) {

            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("USERTYPE", profile.getUserType());
                params.put("user_id", profile.getUserId());
                params.put("party_id", party_id);
                params.put("is_fif", "1");
                Log.e("response", "" + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                //params.put("Accept", "application/json");
                Log.e("response", "" + params);
                return params;
            }
        };
        myReq.setShouldCache(false);
        RequestQueue queue = Volley.newRequestQueue(act);
      //  queue.getCache().clear();
        queue.add(myReq);
    }

  /*  private void getDispetchOrder(String url, int method, String party_id, boolean isViewAll) {
        if (isLoading)
            return;
        isLoading = true;
        Utility.showLoading(act);
        Log.e("DIS--", url);
        Log.e("party_id", party_id);

        request = AndroidNetworking.upload(url)
                .addHeaders("Accept", "application/json")
                .addHeaders("Content-Type", "application/json")
                .addMultipartParameter("USERTYPE", profile.getUserType())
                .addMultipartParameter("user_id", profile.getUserId())
                .addMultipartParameter("party_id", party_id)
                .addMultipartParameter("is_fif", "1")
                .setTag("getPending Order " + url)
                .setPriority(Priority.HIGH);

        //if (isViewAll)
        request.addMultipartParameter("is_fif", "1");
        Log.e("PrintOUT", gson.toJson(request));
        request.getResponseOnlyFromNetwork();
        request.doNotCacheResponse();
        request.build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                // do anything with progress
            }
        })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isLoading = false;
                        Utility.dismissLoading();
                        Log.e("Res-Dispatch", response.toString());
                        MultiModel model = ResponseHandler.handleDispetchList(response.toString());
                        dispetchOrderList = model.getArrayList();
                        setDispatchOrder(0, false);
                        if (model.getLinks() != null) {
                            if (model.getLinks().getNextLink() != null && model.getLinks().getNextLink().length() != 0) {
                                getDispetchOrderNextPage(model.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), party_id);
                                //  getDispetchOrderNextPage(model.getLinks().getNextLink().replace("localhost", prefManager.getIpAddress()), party_id);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        isLoading = false;
                        Utility.dismissLoading();
                        error.printStackTrace();
                        String json;

                        if (error.getErrorCode() != 0) {
                            Log.e("DIS errorCode : ", String.valueOf(error.getErrorCode()));
                            Log.e("DIS errorBody : ", error.getErrorBody());
                            Log.e("DIS errorDetail : ", error.getErrorDetail());
                        } else {
                            Log.e("  DIS : ", error.getErrorDetail());

                        }
                    }
                });


    }*/

    //ANRequest.MultiPartBuilder request;

  /*  private void getDispetchOrderNextPage(String url, String party_id) {
        if (isLoading)
            return;
        isLoading = true;
        // Utility.showLoading(act);

        request = AndroidNetworking.upload(url)
                .addHeaders("Accept", "application/json")
                .addHeaders("Content-Type", "application/json")
                .addMultipartParameter("USERTYPE", profile.getUserType())
                .addMultipartParameter("user_id", profile.getUserId())
                .addMultipartParameter("party_id", party_id)
                .addMultipartParameter("is_fif", "1")
                .setTag("getPending Order " + url)
                .setPriority(Priority.HIGH);

        Log.e("Respsone", new Gson().toJson(request));
        request.getResponseOnlyFromNetwork();
        request.doNotCacheResponse();
        request.build().setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                // do anything with progress
            }
        })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isLoading = false;
                        //  Utility.dismissLoading();
                        Utility.Log("Dispect- ", response);

                        MultiModel model = ResponseHandler.handleDispetchList(response.toString());
                        int lastIndex = dispetchOrderList.size();
                        dispetchOrderList.addAll(model.getArrayList());

                        setDispatchOrder(lastIndex, true);
                        if (model.getLinks() != null) {
                            if (model.getLinks().getNextLink() != null && model.getLinks().getNextLink().length() != 0) {
                                getDispetchOrderNextPage(model.getLinks().getNextLink().replace("http://192.168.1.105", prefManager.getIpAddress()), party_id);

                                // getDispetchOrderNextPage(model.getLinks().getNextLink().replace("localhost", prefManager.getIpAddress()), party_id);
                            }
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        isLoading = false;
                        //  Utility.dismissLoading();
                        error.printStackTrace();
                        String json;

                        if (error.getErrorCode() != 0) {
                            Log.e("onError errorCode : ", String.valueOf(error.getErrorCode()));
                            Log.e("onError errorBody : ", error.getErrorBody());
                            Log.e("onError errorDetail : ", error.getErrorDetail());
                        } else {
                            Log.e("onError errorDetail : ", error.getErrorDetail());

                        }
                    }
                });


    }*/

    private void getMrSelection(String url, int method, String id) {
        if (isLoading)
            return;
        isLoading = true;
        Utility.Log("SACHINNN- " + id + "s", url);
        Utility.showLoading(act);
        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                isLoading = false;
                Utility.dismissLoading();
                Utility.Log("mrSelecttion- ", response);
//                MultiModel responseModel = ResponseHandler.handleMRResponse(response);
//                if (responseModel != null && responseModel.getPartyList() != null && responseModel.getPartyList().size() != 0) {
//                    mrModels.addAll(0, responseModel.getPartyList());
//
//                    Utility.Log("size- ", " -  " + mrModels.size());
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            /**
             * Passing some request headers*
             */
          /*  @Override
            public Map<String, String> getHeaders() {
                Utility.Log("header", getHeader(CodeReUse.GET_JSON_HEADER).toString());
                return getHeader(CodeReUse.GET_JSON_HEADER);

            }*/
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("manager_id", id);
                Log.e("MAPPP", map.toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void setDispatchOrder(int index, boolean isPaging) {
        binding.disptchOrderLayout.setVisibility(View.VISIBLE);
        binding.productTable.setVisibility(View.GONE);
        binding.viewall.setVisibility(View.GONE);
        if (dispetchOrderList != null && dispetchOrderList.size() != 0) {
            if (!isPaging) {
                binding.disProductTable.removeAllViews();
                if (viewMode == PENDING_ORDER_MODE)
                    addHeaderDispetch(0);
                else
                    addHeaderDispetch(1);
            }

            for (int i = index; i < dispetchOrderList.size(); i++) {
                addTableRow(dispetchOrderList.get(i));
            }


        } else {
            binding.viewall.setVisibility(View.GONE);
            binding.productTable.removeAllViews();
            binding.disProductTable.removeAllViews();
            if (viewMode == PENDING_ORDER_MODE)
                addHeaderDispetch(0);
            else
                addHeaderDispetch(1);
        }
    }

    public void addHeaderDispetch(int flag) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        if (flag == 0) {
            rowView = inflater.inflate(R.layout.item_pending_header, null);
            binding.productTable.addView(rowView);
        } else {
            rowView = inflater.inflate(R.layout.item_supply_details_add_header_product_layout, null);
            binding.disProductTable.addView(rowView);
        }

    }

    public void setPendingOrder() {
        if (dispetchOrderList != null) {
            binding.productTable.removeAllViews();
            binding.disProductTable.removeAllViews();
            if (viewMode == PENDING_ORDER_MODE)
                addHeader(0);
            else
                addHeader(1);
            for (int i = 0; i < dispetchOrderList.size(); i++) {
                addPendingTable(dispetchOrderList.get(i));
            }
        }
    }

    public void addPendingTable(MultiModel selectedModel) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.item_add_pending_product_layout, null);

        TextView columnOne = rowView.findViewById(R.id.columnOne);
        TextView columnSecond = rowView.findViewById(R.id.columnSecond);

        columnOne.setText(selectedModel.getORDERNO());

        String Message = "<font color='#00AFEF'><u>" + selectedModel.getORDERNO() + "</u></font>";
        columnSecond.setText(selectedModel.getORDERDATE());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            columnOne.setText(Html.fromHtml(Message, Html.FROM_HTML_MODE_COMPACT));
        } else {
            columnOne.setText(Html.fromHtml(Message));
        }

        columnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(act, OrderDetailsActivity.class);
//                i.putExtra("intentAction", OrderDetailsActivity.PENDING_VIEW);
//                i.putExtra("object", gson.toJson(selectedModel));
//                startActivityForResult(i, 101);
//                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });
        binding.productTable.addView(rowView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                MultiModel multiModel = gson.fromJson(data.getStringExtra("object"), MultiModel.class);
                for (int i = 0; i < dispetchOrderList.size(); i++) {
                    if (dispetchOrderList.get(i).getORDID().equalsIgnoreCase(multiModel.getORDID())) {
                        dispetchOrderList.set(i, multiModel);
                        break;
                    }
                }
                setPendingOrder();
            }
        }
    }

    //0 for pending,1 for supply details
    public void addHeader(int flag) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        if (flag == 0)
            rowView = inflater.inflate(R.layout.item_pending_header, null);
        else
            rowView = inflater.inflate(R.layout.item_table_header_for_supply_details, null);

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

//    private void getPendingOrders(String url, int method, String party_id) {
//        Utility.showLoading(act);
//
//
//        //profile image passing remains
//
//        ANRequest.MultiPartBuilder request = AndroidNetworking.upload(url)
//                .addHeaders("Accept", "application/json")
//                .addHeaders("Content-Type", "application/json")
//                .addMultipartParameter("USERTYPE", profile.getUserType())
//                .addMultipartParameter("user_id", profile.getUserId())
//                .addMultipartParameter("party_id", party_id)
//
//                .setTag("getPending Order")
//
//                .setPriority(Priority.HIGH);
//        Log.e("USERTYPE", profile.getUserType());
//        Log.e("user_id", profile.getUserId());
//        Log.e("party_id", party_id);
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
//                        Log.e("Pending Orders", response.toString());
//                        isLoading = false;
//                        Utility.dismissLoading();
//
//                        dispetchOrderList = ResponseHandler.handlePendingOrderLIst(response.toString());
//                        setPendingOrder();
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
//                        Log.e("onError : ", new Gson().toJson(error));
//                        if (error.getErrorCode() != 0) {
//                            Log.e("onError PENDING : ", String.valueOf(error.getErrorCode()));
//                            Log.e("onError PENDING : ", error.getErrorBody());
//                            Log.e("onError PENDING : ", error.getErrorDetail());
//                        } else {
//                            Log.e("PENDING : ", error.getErrorDetail());
//
//                        }
//                    }
//                });
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (viewMode == PENDING_ORDER_MODE){
            prefManager=new PrefManager(act);
            MultiModel multiModel=gson.fromJson(prefManager.getEditedObject(),MultiModel.class);
            if (multiModel!=null) {
                for (int i = 0; i < dispetchOrderList.size(); i++) {
                    if (dispetchOrderList.get(i).getORDID().equalsIgnoreCase(multiModel.getORDID())) {
                        dispetchOrderList.set(i, multiModel);
                        break;
                    }
                }
                setPendingOrder();

            }
            prefManager.setEditedObject("");
        }*/

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
        TextView titleTxt = dialogView.findViewById(R.id.titleTxt);
        titleTxt.setText("Select Party");
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
//
//                refreshData();
//                for (int i = 0; i < models.size(); i++) {
//                    if (models.get(i).getParty_id().equalsIgnoreCase(itemmodel.getParty_id())) {
//                        positino = i;
//                        break;
//                    }
//                }
//
//                binding.partyEdit.setText(models.get(positino).getParty_name());
//                setPartyDetails(positino);
//                if (viewMode == PENDING_ORDER_MODE) {
//                    getPendingOrders(prefManager.getIpAddress() + APIS.GET_PENDING_ORDER, Request.Method.POST, models.get(positino).getParty_id());
//                    //getDispetchOrder(prefManager.getIpAddress()+APIS.GET_DISPETCH_ORDER,Request.Method.POST,models.get(position).getParty_id());
//                } else
//                    getDispetchOrder(prefManager.getIpAddress() + APIS.GET_DISPETCH_ORDER, Request.Method.POST, models.get(positino).getParty_id(), false);
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
               // listViewAdapter.getFilter().filter(cs);
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
                models.clear();
                mrModels.clear();
                selectedManagerModel = managerList.get(positino);
                binding.mgrEdit.setText(selectedManagerModel.getManagerName());
                binding.productTable.removeAllViews();
                binding.disProductTable.removeAllViews();
                if (viewMode == PENDING_ORDER_MODE)
                    addHeader(0);
                else
                    addHeader(1);
                binding.mrEdit.setText("");
                binding.partyEdit.setText("");
                isFirstCall = true;

                getMrSelection(prefManager.getIpAddress() + APIS.GET_MR_BYE_MANAGER, Request.Method.POST, selectedManagerModel.getMGRID());

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

//        ListView listView = dialogView.findViewById(R.id.listview);
//        final PartyAdpter listViewAdapter = new PartyAdpter(act, act, R.layout.item_layout, mrModels);
//        listView.setAdapter(listViewAdapter);
//        PartyAdpter.handleSelectionEvent handleSelectionEvent = new PartyAdpter.handleSelectionEvent() {
//            @Override
//            public void selectionEvent(MultiModel itemmodel, int positino) {
//                alertDialog.dismiss();
//                refreshData();
//                models.clear();
//                for (int i = 0; i < mrModels.size(); i++) {
//                    if (mrModels.get(i).getMRID().equalsIgnoreCase(itemmodel.getMRID())) {
//                        positino = i;
//                        break;
//                    }
//                }
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
               // listViewAdapter.getFilter().filter(cs);
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
}