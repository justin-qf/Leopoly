package com.app.leopoly.activity.SalesOrderSlipUpdate

import android.content.Intent
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
import com.app.leopoly.activity.PackageSlipEntryActivity.PackingSlipEntryActivity
import com.app.leopoly.adpters.PackageSlipDetailAdapter
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.packgeOrderListResponse.PackageOrderListData
import com.app.leopoly.apiHandling.packgeOrderListResponse.PackageOrderListResponse
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivityPackageSlipListBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.interfaces.ListClickListener
import com.vovance.omcsalesapp.Common.PubFun
import java.util.ArrayList
import java.util.HashMap

class PackageSlipListActivity : BaseActivity(), View.OnClickListener {

    private var packageSlipListAdapter: PackageSlipDetailAdapter? = null
    private var binding: ActivityPackageSlipListBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var packageSlipDetailsListResponse: Response.Listener<String>? = null
    private var deletePackageSlipDetailsListResponse: Response.Listener<String>? = null
    private var errorListener: Response.ErrorListener? = null
    val data = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_package_slip_list)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        binding!!.toolbar.ivBack.setOnClickListener(this)
        binding!!.toolbar.title.setText(R.string.orderList_title)
        getOrderListData()
    }

    private fun getOrderListData() {
        try {
            loaderModel!!.isLoading.observe(this) { isLoading ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            packageSlipDetailsListResponse = Response.Listener<String> { response: String? ->
                HELPER.print("PackageSlipDetailsResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val orderListResponse: PackageOrderListResponse =
                        gson.fromJson(response, PackageOrderListResponse::class.java)
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
                            getString(R.string.orderList_title),
                            orderListResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                            false,
                            clickListener = {
                                //act.onBackPressed()
                            })
                    }
                } catch (e: Exception) {
                    HELPER.print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }

            if (Utils.hasNetwork(act)) {
                if (prefManager.loginData != null) {
                    data["company_id"] = prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!
                    data["ac_year_id"] = prefManager.loginData!!.accountYear?.get(0)?.aCID!!
                    val formattedDate = HELPER.formatDate(HELPER.getCurrentDate(), "dd-MM-yyyy")
                    HELPER.print("CurrentDate", formattedDate)
                    data["inv_date"] = formattedDate
                    loaderModel!!.isLoading.value = true
                    HELPER.print("PASSING_DATA", gson.toJson(data))
                    ApiRequest.getPackageSlipDetailList(
                        act,
                        packageSlipDetailsListResponse!!,
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

    private fun setAdapter(orderList: ArrayList<PackageOrderListData>) {
        binding!!.rootRecyclerList.layoutManager = LinearLayoutManager(
            act,
            LinearLayoutManager.VERTICAL,
            false
        )
        val deleteListClickListener = ListClickListener { _, _, `object` ->
            val contentModel = `object` as PackageOrderListData
        }

        val itemListClickListener = ListClickListener { _, _, `object` ->

            if (prefManager.orderData != null) {
                prefManager.orderData = null
            }
            val contentModel = `object` as PackageOrderListData
            val i = Intent(act, PackingSlipEntryActivity::class.java)
            i.putExtra("isFromOrderListScreen", true)
            i.putExtra("itemQTY", contentModel.BALQTY.toString().ifEmpty { "0.0" })
            i.putExtra("itemName", contentModel.ITEMNAME.toString())
            i.putExtra("itemDesc", "")
            i.putExtra("itemID", contentModel.ITEMID.toString())
            i.putExtra("lessWt", contentModel.LESSWT.toString())
            i.putExtra("invID", contentModel.INVID.toString())
            startActivity(i)
            act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
            act.finish()
        }
        packageSlipListAdapter = PackageSlipDetailAdapter(
            act,
            orderList,
            deleteListClickListener,
            itemListClickListener
        )
        packageSlipListAdapter!!.setHasStableIds(true)
        binding!!.rootRecyclerList.adapter = packageSlipListAdapter
        binding!!.rootRecyclerList.itemAnimator = DefaultItemAnimator()
        binding!!.rootRecyclerList.setHasFixedSize(true)
        packageSlipListAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.toolbar.ivBack.id -> {
                onBackPressed()
            }
        }
    }
}