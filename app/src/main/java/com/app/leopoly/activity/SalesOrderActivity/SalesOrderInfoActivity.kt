package com.app.leopoly.activity.SalesOrderActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.activity.HomeActivity.DashboardActivity
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.orderDetailResponse.AddOrderDetailResponse
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivitySalesOrderInfoBinding
import com.app.leopoly.helper.HELPER
import com.vovance.omcsalesapp.Common.PubFun
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class SalesOrderInfoActivity : BaseActivity(), View.OnClickListener {

    var binding: ActivitySalesOrderInfoBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var addSalesOrderDetailResponse: Response.Listener<JSONObject>? = null
    private var errorListener: Response.ErrorListener? = null
    private var soDate: String? = ""
    private var soNo: String? = ""
    private var billTo: String? = ""
    private var shipTo: String? = ""
    private var purOrderNo: String? = ""
    private var orderDate: String? = ""
    private var transport: String? = ""
    private var dueDays: String? = ""
    private var destination: String? = ""
    private val itemArrayList = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_sales_order_info)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        setSupportActionBar(binding!!.mainToolbar.toolbar)
        binding!!.mainToolbar.ivBack.setOnClickListener(this)
        binding!!.saveBtn.setOnClickListener(this)
        binding!!.mainToolbar.title.setText(R.string.salesOrderDetailInfoTitle)
        binding!!.grossEdt.setText(prefManager.totalAmount.toString().ifEmpty { "" })
        getSalesOrderData()
        addSalesOrderApi()
    }

    private fun getSalesOrderData() {
        val jsonString = intent.getStringExtra("orderObject")
        if (!jsonString.isNullOrBlank()) {
            try {
                val orderObjectData = JSONObject(jsonString)
                // Now you have the JSONObject, you can access its values
                soDate = orderObjectData.getString("soDate")
                soNo = orderObjectData.getString("soNo")
                billTo = orderObjectData.getString("billTo")
                shipTo = orderObjectData.getString("shipTo")
                purOrderNo = orderObjectData.getString("purOrderNo")
                orderDate = orderObjectData.getString("orderDate")
                transport = orderObjectData.getString("transport")
                dueDays = orderObjectData.getString("dueDays")
                destination = orderObjectData.getString("destination")
                val valuesArray = orderObjectData.getJSONArray("order_details")
                for (i in 0 until valuesArray.length()) {
                    val itemObj = JSONObject()
                    itemObj.put(
                        "item_id",
                        PubFun.removeSpaceFromText(
                            valuesArray.getJSONObject(i).getString("item_id")
                        )
                    )
                    itemObj.put(
                        "packing",
                        PubFun.removeSpaceFromText(
                            valuesArray.getJSONObject(i).getString("packing")
                        )
                    )
                    itemObj.put(
                        "less_wt",
                        PubFun.removeSpaceFromText(
                            valuesArray.getJSONObject(i).getString("less_wt")
                        )
                    )
                    itemObj.put(
                        "qty",
                        PubFun.removeSpaceFromText(valuesArray.getJSONObject(i).getString("qty"))
                    )
                    itemObj.put(
                        "rate",
                        PubFun.removeSpaceFromText(valuesArray.getJSONObject(i).getString("rate"))
                    )
                    itemObj.put(
                        "amount",
                        PubFun.removeSpaceFromText(valuesArray.getJSONObject(i).getString("amount"))
                    )
                    itemObj.put(
                        "description",
                        PubFun.removeSpaceFromText(
                            valuesArray.getJSONObject(i).getString("description")
                        )
                    )
                    itemObj.put(
                        "company_id",
                        valuesArray.getJSONObject(i).getString("company_id")
                    )
                    itemObj.put(
                        "ac_year_id",
                        valuesArray.getJSONObject(i).getString("ac_year_id")
                    )
                    itemArrayList.put(itemObj)
                }
                HELPER.print("soDate", soDate)
                HELPER.print("soNo", soNo)
                HELPER.print("billTo", billTo)
                HELPER.print("shipTo", shipTo)
                HELPER.print("purOrderNo", purOrderNo)
                HELPER.print("orderDate", orderDate)
                HELPER.print("transport", transport)
                HELPER.print("dueDays", dueDays)
                HELPER.print("destination", destination)
                HELPER.print("itemArrayList", itemArrayList.toString())

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun addSalesOrderApi() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                HELPER.showProgress(act)
            } else {
                HELPER.hideProgress(act)
            }
        }
        addSalesOrderDetailResponse = Response.Listener<JSONObject> { response: JSONObject? ->
            HELPER.print("addSalesOrderDetailResponse", response.toString())
            loaderModel!!.isLoading.value = false
            try {
                val salesOrderResponse: AddOrderDetailResponse =
                    gson.fromJson(response.toString(), AddOrderDetailResponse::class.java)
                if (salesOrderResponse.data != null && salesOrderResponse.status == 1) {
                    PubFun.commonSuccessDialog(
                        act,
                        getString(R.string.salesOrderDetailTitle),
                        salesOrderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        clickListener = {
                            prefManager.totalAmount = ""
                            val i = Intent(act, DashboardActivity::class.java)
                            startActivity(i)
                            act.overridePendingTransition(
                                R.anim.right_enter,
                                R.anim.left_out
                            )
                            act.finishAffinity()
                            finish()
                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.salesOrderDetailTitle),
                        salesOrderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                        })
                }
            } catch (e: Exception) {
                HELPER.print("Exception", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.mainToolbar.ivBack.id -> {
                onBackPressed()
            }
            binding!!.saveBtn.id -> {
                Utils.hideKeyboard(act)
                validation()
            }
        }
    }

    private fun validation() {
        var isError = false
        var isFocus = false
        val orderDescription =
            PubFun.removeSpaceFromText(binding!!.orderDescriptionEdt.text.toString())
        val grossEdt = PubFun.removeSpaceFromText(binding!!.grossEdt.text.toString())

//        if (orderDescription.trim().isEmpty()) {
//            isError = true
//            binding!!.orderDescriptionLayout.error = getString(R.string.descriptionError)
//            if (!isFocus) {
//                binding!!.orderDescriptionEdt.requestFocus()
//                isFocus = true
//            }
//        }
        if (grossEdt.trim().isEmpty()) {
            isError = true
            binding!!.grossAmountLayout.error = getString(R.string.grossAmountError)
            if (!isFocus) {
                binding!!.grossEdt.requestFocus()
                isFocus = true
            }
        }

        if (!isError) {
            if (Utils.hasNetwork(act)) {
                val orderObject = JSONObject()
                orderObject.put(
                    "sales_ord_date",
                    PubFun.removeSpaceFromText(soDate.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "sales_ord_no",
                    PubFun.removeSpaceFromText(soNo.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "party_id",
                    PubFun.removeSpaceFromText(billTo.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "shipTo",
                    PubFun.removeSpaceFromText(shipTo.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "ord_no",
                    PubFun.removeSpaceFromText(purOrderNo.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "ord_date",
                    PubFun.removeSpaceFromText(orderDate.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "trans_id",
                    PubFun.removeSpaceFromText(transport.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "days",
                    PubFun.removeSpaceFromText(dueDays.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "destination",
                    PubFun.removeSpaceFromText(destination.toString().ifEmpty { "" })
                )
                orderObject.put(
                    "company_id",
                    prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!
                )
                orderObject.put(
                    "ac_year_id",
                    prefManager.loginData!!.accountYear?.get(0)?.aCID!!
                )
//                orderObject.put(
//                    "grossAmount",
//                    PubFun.removeSpaceFromText(binding!!.grossEdt.text.toString())
//                )

                orderObject.put("sales_order_details", itemArrayList)
                HELPER.print("PASSING_DATA", orderObject.toString())
                HELPER.print("PASSING_GSON_DATA", gson.toJson(orderObject))
                loaderModel!!.isLoading.value = true
                ApiRequest.addSalesOrderDetail(
                    act,
                    addSalesOrderDetailResponse!!,
                    errorListener,
                    orderObject,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    }

}