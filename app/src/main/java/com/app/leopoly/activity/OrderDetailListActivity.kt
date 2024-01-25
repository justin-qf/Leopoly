package com.app.leopoly.activity

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.adpters.OrderDetailAdapter
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.orderDetailListResponse.DeleteOrderListResponse
import com.app.leopoly.apiHandling.orderDetailListResponse.OrderDetailListData
import com.app.leopoly.apiHandling.orderDetailListResponse.OrderDetailListResponse
import com.app.leopoly.common.Constant
import com.app.leopoly.common.LeoPolyApp
import com.app.leopoly.databinding.ActivityOrderDetailListBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.interfaces.ListClickListener
import com.vovance.omcsalesapp.Common.PubFun
import java.util.ArrayList
import java.util.HashMap

class OrderDetailListActivity : BaseActivity(), View.OnClickListener {

    private var notificationAdapter: OrderDetailAdapter? = null
    private var binding: ActivityOrderDetailListBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var orderDetailsListResponse: Response.Listener<String>? = null
    private var deleteOrderDetailsListResponse: Response.Listener<String>? = null
    private var errorListener: Response.ErrorListener? = null
    val data = HashMap<String, String>()
    private var isFromAPI: Boolean? = false
    private var invId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_order_detail_list)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        isFromAPI = intent.getBooleanExtra("isFromAPI", false)
        binding!!.toolbar.ivBack.setOnClickListener(this)
        binding!!.toolbar.title.setText(R.string.order_details_title)
        invId = intent.getStringExtra("invID")
        HELPER.print("INV_ID", invId.toString())
        HELPER.print("INV_ID", invId.toString())
        getPackingDetails()
        deletePackingDetails()
    }

    private fun getPackingDetails() {
        try {
            loaderModel!!.isLoading.observe(this) { isLoading ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            orderDetailsListResponse = Response.Listener<String> { response: String? ->
                HELPER.print("orderDetailsResponse", response)
                loaderModel!!.isLoading.value = false

                val orderListResponse: OrderDetailListResponse =
                    gson.fromJson(response, OrderDetailListResponse::class.java)
                if (orderListResponse.data != null && orderListResponse.status == 1) {
                    if (orderListResponse.data.isNotEmpty()) {
                        binding!!.rootRecyclerList.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                        setAdapter(orderListResponse.data)
                    } else {
                        binding!!.rootRecyclerList.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.orderDetail),
                        orderListResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            //act.onBackPressed()
                        })
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
            if (Utils.hasNetwork(act)) {
                if (invId.toString().isNotEmpty() || prefManager.orderData != null) {
                    if (prefManager.orderData != null && prefManager.orderData!!.iNVID.toString()
                            .isNotEmpty()
                    ) {
                        data["order_id"] =
                            prefManager.orderData!!.iNVID.toString().trim()
                    } else {
                        HELPER.print("INV_ID", invId.toString())
                        data["order_id"] =
                            invId.toString()
                    }
//                    data["order_id"] =
//                        invId.toString().ifEmpty { prefManager.orderData!!.iNVID.toString() }
                    loaderModel!!.isLoading.value = true
                    ApiRequest.getPackageDetailList(
                        act,
                        orderDetailsListResponse!!,
                        errorListener,
                        data,
                        prefManager
                    )
                } else {
                    binding!!.rootRecyclerList.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }

            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        } catch (e: Exception) {
            HELPER.print("Exception", e.printStackTrace().toString())
        }
    }

    private fun deletePackingDetails() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                HELPER.showProgress(act)
            } else {
                HELPER.hideProgress(act)
            }
        }
        deleteOrderDetailsListResponse = Response.Listener<String> { response: String? ->
            HELPER.print("orderDetailsResponse", response)
            loaderModel!!.isLoading.value = false
            try {
                val orderListResponse: DeleteOrderListResponse =
                    gson.fromJson(response, DeleteOrderListResponse::class.java)
                if (orderListResponse.status == 1 && orderListResponse.message != null) {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.orderDetail),
                        orderListResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            getPackingDetails()
                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.orderDetail),
                        orderListResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            act.onBackPressed()
                        })
                }
            } catch (e: Exception) {
                HELPER.print("Exception", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
    }

    private fun setAdapter(notificationList: ArrayList<OrderDetailListData>) {
        binding!!.rootRecyclerList.layoutManager = LinearLayoutManager(
            act,
            LinearLayoutManager.VERTICAL,
            false
        )
        val deleteListClickListener = ListClickListener { _, _, `object` ->
            val contentModel = `object` as OrderDetailListData
            PubFun.confirmationDialog(
                act,
                act.getString(R.string.deleteMessage),
                listener = {
                    if (Utils.hasNetwork(act)) {
                        loaderModel!!.isLoading.value = true
                        data["package_id"] = contentModel.INVITID.toString()
                        ApiRequest.deletePackageById(
                            act,
                            deleteOrderDetailsListResponse!!,
                            errorListener,
                            data,
                            prefManager
                        )
                    } else {
                        HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
                    }
                })
        }

        notificationAdapter = OrderDetailAdapter(
            act,
            notificationList,
            deleteListClickListener
        )
        //notificationAdapter!!.setHasStableIds(true)
        binding!!.rootRecyclerList.adapter = notificationAdapter
        binding!!.rootRecyclerList.itemAnimator = DefaultItemAnimator()
        //binding!!.rootRecyclerList.setHasFixedSize(true)
        notificationAdapter!!.notifyDataSetChanged()
    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.toolbar.ivBack.id -> {
                onBackPressed()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isFromAPI == true) {
            LeoPolyApp.instance!!.observer!!.value = Constant.OBSERVER_BACK_FROM_DETAIL_SCREEN
        }
        super.onBackPressed()
    }
}