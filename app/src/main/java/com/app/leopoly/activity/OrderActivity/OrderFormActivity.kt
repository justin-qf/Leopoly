package com.app.leopoly.activity.OrderActivity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.activity.PackageSlipEntryActivity.PackingSlipEntryActivity
import com.app.leopoly.adpters.OrderAdapter
import com.app.leopoly.adpters.PackageSlipAdapter
import com.app.leopoly.adpters.PartyAdapter
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.ApiRequest.getOrderList
import com.app.leopoly.apiHandling.ApiRequest.getPackageSlipList
import com.app.leopoly.apiHandling.ApiRequest.getPartyList
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.orderListResponse.OrderListData
import com.app.leopoly.apiHandling.orderListResponse.OrderListResponse
import com.app.leopoly.apiHandling.orderResponse.OrderResponse
import com.app.leopoly.apiHandling.packageSlipResponse.PackageSlipList
import com.app.leopoly.apiHandling.packageSlipResponse.PackageSlipResponse
import com.app.leopoly.apiHandling.partyResponse.PartyListData
import com.app.leopoly.apiHandling.partyResponse.PartyResponse
import com.app.leopoly.apiHandling.partyResponse.TransportListData
import com.app.leopoly.apiHandling.partyResponse.TransportResponse
import com.app.leopoly.common.CodeReUse
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivityOrderFormBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.helper.HELPER.hideProgress
import com.app.leopoly.helper.HELPER.print
import com.app.leopoly.helper.HELPER.showDatePickerDialog
import com.app.leopoly.helper.HELPER.showProgress
import com.app.leopoly.helper.MySingleton
import com.app.leopoly.interfaces.ListClickListener
import com.google.android.material.textfield.TextInputEditText
import com.vovance.omcsalesapp.Common.PubFun
import java.util.*

class OrderFormActivity : BaseActivity(), View.OnClickListener {
    var binding: ActivityOrderFormBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var partyListResponse: Response.Listener<String?>? = null
    private var orderListResponse: Response.Listener<String?>? = null
    private var packageSlipListResponse: Response.Listener<String?>? = null
    private var addOrderResponse: Response.Listener<String>? = null
    private var errorListener: Response.ErrorListener? = null
    private val partyListData: ArrayList<PartyListData> = ArrayList()
    private val orderListData: ArrayList<OrderListData> = ArrayList()
    private val packageSlipListData: ArrayList<PackageSlipList> = ArrayList()
    private var selectedParty: PartyListData? = null
    private var selectedOrder: OrderListData? = null
    private var selectedPackageSlip: PackageSlipList? = null
    // private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = DataBindingUtil.setContentView(act, R.layout.activity_order_form)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        setSupportActionBar(binding!!.mainToolbar.toolbar)
        setCurrentDate()
        //OnClickListener
        binding!!.mainToolbar.ivBack.setOnClickListener(this)
        binding!!.loginBtn.setOnClickListener(this)
        binding!!.partyEdt.setOnClickListener { showPartyListDialog() }
        binding!!.salesOrderIdEdt.setOnClickListener { showOderListDialog() }
        binding!!.itemEdt.setOnClickListener { showPackageSlipListDialog() }
        binding!!.mainToolbar.title.setText(R.string.order)

        //API Calls
        partyApi
        orderListApi
        packageListApi
        addSalesOrderApi()
        //Remove Errors
        CodeReUse.RemoveError(binding!!.dateEdt, binding!!.dateLayout)
        CodeReUse.RemoveError(binding!!.packingNoEdt, binding!!.packingNoLayout)
        CodeReUse.RemoveError(binding!!.partyEdt, binding!!.partyLayout)
        CodeReUse.RemoveError(binding!!.salesOrderIdEdt, binding!!.salesOrderIdLayout)
        CodeReUse.RemoveError(binding!!.purchaseEdt, binding!!.purchaseLayout)
        CodeReUse.RemoveError(binding!!.itemEdt, binding!!.itemLayout)
        CodeReUse.RemoveError(binding!!.itemQtyEdt, binding!!.itemQtyLayout)
        //firebase
        //FirebaseApp.initializeApp(this)
        //firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun setCurrentDate() {
        val currentDate = HELPER.getCurrentDate()
        val formattedDate = HELPER.formatDate(currentDate, "yyyy-MM-dd")
        binding!!.dateEdt.setText(formattedDate)
        binding!!.dateEdt.setOnClickListener {
            showDatePickerDialog(
                act,
                binding!!.dateEdt
            )
        }
    }

    private val partyApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    showProgress(act)
                } else {
                    hideProgress(act)
                }
            }
            partyListResponse = Response.Listener { response: String? ->
                print("PartyListResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val partyList = gson.fromJson(response, PartyResponse::class.java)
                    partyListData.clear()
                    if (partyList.status == 1 && partyList.data!!.isNotEmpty()) {
                        partyListData.addAll(0, partyList.data!!)
                    } else {
                        PubFun.commonDialog(
                            act,
                            getString(R.string.order),
                            partyList.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                            false,
                            clickListener = {
                                //act.onBackPressed()
                            })
                    }
                } catch (e: Exception) {
                    print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
            if (Utils.hasNetwork(act)) {
                loaderModel!!.isLoading.value = true
                getPartyList(act, partyListResponse, errorListener, prefManager)
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    private val orderListApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    showProgress(act)
                } else {
                    hideProgress(act)
                }
            }
            orderListResponse = Response.Listener { response: String? ->
                print("orderListResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val orderList = gson.fromJson(response, OrderListResponse::class.java)
                    orderListData.clear()
                    //Firebase Log
                   // val jsonObject = response?.let { JSONObject(it) }
                   // val dataObject = jsonObject!!.getJSONArray("data").getJSONObject(0)
//                    val bundle = Bundle()
//                    bundle.putString("ORDER_LIST_DATA", jsonObject.toString())
//                    firebaseAnalytics.logEvent("ORDER_DATA", bundle)
//                    val params = Bundle()
//                    params.putString(
//                        "API_Response",
//                        jsonObject.toString()
//                    ) // Store the API response data as a parameter
//                    FirebaseAnalytics.getInstance(act).logEvent("API_Response_Event", params)
                    binding!!.salesOrderIdEdt.setText("")
                    binding!!.purchaseEdt.setText("")
                    binding!!.packingNoEdt.setText("")
                    if (orderList.status == 1 && orderList.data!!.isNotEmpty()) {
//                        binding!!.salesOrderIdEdt.setText("")
//                        binding!!.purchaseEdt.setText("")
//                        binding!!.packingNoEdt.setText("")
                        orderListData.addAll(0, orderList.data!!)
                    } else {
                        PubFun.commonDialog(
                            act,
                            getString(R.string.order),
                            orderList.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                            false,
                            clickListener = {
                                //act.onBackPressed()
                            })
                    }
                } catch (e: Exception) {
                    print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
        }

    private var packageSlipId = ""

    private val packageListApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    showProgress(act)
                } else {
                    hideProgress(act)
                }
            }
            packageSlipListResponse = Response.Listener { response: String? ->
                print("PackageListApiResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val packageSlipList = gson.fromJson(response, PackageSlipResponse::class.java)
                    if (packageSlipList.status == 1 && packageSlipList.itemList!!.isNotEmpty()) {
                        packageSlipListData.clear()
                        if (packageSlipList?.packingSlip != null && packageSlipList.packingSlip!!.isNotEmpty()) {
                            packageSlipListData.addAll(0, packageSlipList.itemList!!)
                        }
                        if (packageSlipList != null) {
                            binding!!.packingNoEdt.setText(packageSlipList.packingSlip)
                        }
                        if (packageSlipList != null) {
                            binding!!.purchaseEdt.setText(packageSlipList.itemList!![0].oNO)
                        }
                        packageSlipId = packageSlipList!!.packingSlip!!
                    } else {
                        PubFun.commonDialog(
                            act,
                            getString(R.string.order),
                            packageSlipList.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                            false,
                            clickListener = {
                                //act.onBackPressed()
                            })
                    }
                } catch (e: Exception) {
                    print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
        }

    private fun addSalesOrderApi() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showProgress(act)
            } else {
                hideProgress(act)
            }
        }
        addOrderResponse = Response.Listener<String> { response: String? ->
            print("addSalesOrderApi", response)
            loaderModel!!.isLoading.value = false
            try {
                val orderResponse: OrderResponse =
                    gson.fromJson(response, OrderResponse::class.java)
                if (orderResponse.data != null && orderResponse.status == 1) {
                    print("ACID", orderResponse.data!!.aCID)
                    prefManager.orderData = orderResponse.data
                    PubFun.commonDialog(
                        act,
                        getString(R.string.order),
                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        true,
                        clickListener = {
                            val i = Intent(act, PackingSlipEntryActivity::class.java)
                            i.putExtra("isFromNextBtn", false)
                            i.putExtra("itemQTY", binding!!.itemQtyEdt.text.toString())
                            i.putExtra("itemName", binding!!.itemEdt.text.toString())
                            i.putExtra("itemDesc",  binding!!.itemDescEdt.text.toString())
                            i.putExtra("itemID", orderResponse.data!!.iTEMID.toString())
                            i.putExtra("lessWt", selectedPackageSlip!!.LESSWT.toString())
                            startActivity(i)
                            act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
                            act.finish()
                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.order),
                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            act.onBackPressed()
                        })
                }
            } catch (e: Exception) {
                print("Exception", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
    }

    private fun showPartyListDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false)
        val builder = AlertDialog.Builder(this, R.style.RoundedAlertDialogStyle)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val closeImage = dialogView.findViewById<ImageView>(R.id.closeImage)
        closeImage.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val closeText = dialogView.findViewById<TextView>(R.id.closeText)
        val emptyText = dialogView.findViewById<TextView>(R.id.emptyText)
        closeText.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val listView = dialogView.findViewById<ListView>(R.id.listview)
        if (partyListData != null) {
            emptyText.visibility = View.GONE
            val listClickListener = ListClickListener { _, _, `object` ->
                alertDialog.dismiss()
                val contentModel = `object` as PartyListData
                var position = -1
                for (i in partyListData.indices) {
                    if (partyListData[i].pRTYID.equals(contentModel.pRTYID, ignoreCase = true)) {
                        position = i
                        break
                    }
                }
                binding!!.partyEdt.setText(partyListData[position].pNAME)
                selectedParty = partyListData[position]
                if (Utils.hasNetwork(act)) {
                    val data = HashMap<String, String>()
                    data["partyId"] = selectedParty!!.pRTYID!!
                    loaderModel!!.isLoading.value = true
                    getOrderList(act, orderListResponse, errorListener, data, prefManager)
                } else {
                    HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
                }
            }
            val listViewAdapter = PartyAdapter(
                act,
                act,
                R.layout.item_layout,
                partyListData,
                if (selectedParty != null && selectedParty!!.pRTYID!!.isNotEmpty()) Integer.valueOf(
                    selectedParty!!.pRTYID!!
                ) else 0,
                listClickListener
            )
            listViewAdapter.setPartyMode(true)
            listView.adapter = listViewAdapter
            val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
            editText.visibility = View.VISIBLE
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                    listViewAdapter.filter.filter(cs)
                }

                override fun beforeTextChanged(
                    arg0: CharSequence, arg1: Int, arg2: Int,
                    arg3: Int
                ) {
                }

                override fun afterTextChanged(arg0: Editable) {}
            })
        } else {
            emptyText.visibility = View.VISIBLE
            emptyText.text = act.getText(R.string.listEmpty)
        }
        alertDialog.show()
    }

    private fun showOderListDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false)
        val builder = AlertDialog.Builder(this, R.style.RoundedAlertDialogStyle)
        builder.setView(dialogView)
        //R.style.RoundedAlertDialogStyle
        val alertDialog = builder.create()
        //        alertDialog.getWindow().getDecorView().setBackgroundResource(R.drawable.rounded_dialog_background);
        val closeImage = dialogView.findViewById<ImageView>(R.id.closeImage)
        closeImage.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val closeText = dialogView.findViewById<TextView>(R.id.closeText)
        val emptyText = dialogView.findViewById<TextView>(R.id.emptyText)
        val titleTxt = dialogView.findViewById<TextView>(R.id.titleTxt)
        closeText.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val listView = dialogView.findViewById<ListView>(R.id.listview)
        titleTxt.text = act.getText(R.string.salesOrderListTitle)
        if (orderListData != null) {
            emptyText.visibility = View.GONE
            val listClickListener = ListClickListener { _, _, `object` ->
                alertDialog.dismiss()
                val contentModel = `object` as OrderListData
                var position = -1
                for (i in orderListData.indices) {
                    if (orderListData[i].pRTYID.equals(contentModel.pRTYID, ignoreCase = true)) {
                        position = i
                        break
                    }
                }
                binding!!.salesOrderIdEdt.setText(orderListData[position].iNVNO)
                selectedOrder = orderListData[position]
                if (Utils.hasNetwork(act)) {
                    val data = HashMap<String, String>()
                    data["company_id"] = prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!
                    data["ac_year_id"] = prefManager.loginData!!.accountYear?.get(0)?.aCID!!
                    data["sales_ord_no"] = selectedOrder!!.iNVNO!!
                    data["party_id"] = selectedOrder!!.pRTYID!!
                    data["inv_item_id"] = selectedOrder!!.iNVID!!
                   // data["inv_item_id"] = selectedOrder!!.iNVITID!!
                    loaderModel!!.isLoading.value = true
                    getPackageSlipList(
                        act,
                        packageSlipListResponse,
                        errorListener,
                        data,
                        prefManager
                    )
                } else {
                    HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
                }
            }
            val listViewAdapter = OrderAdapter(
                act,
                act,
                R.layout.item_layout,
                orderListData,
                if (selectedOrder != null && selectedOrder!!.iNVID!!.isNotEmpty()) Integer.valueOf(
                    selectedOrder!!.iNVID!!
                ) else 0,
                listClickListener
            )
            listViewAdapter.setPartyMode(true)
            listView.adapter = listViewAdapter
            val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
            editText.visibility = View.VISIBLE
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                    // When user changed the Text
                    listViewAdapter.filter.filter(cs)
                }

                override fun beforeTextChanged(
                    arg0: CharSequence, arg1: Int, arg2: Int,
                    arg3: Int
                ) {
                }

                override fun afterTextChanged(arg0: Editable) {}
            })
        } else {
            emptyText.visibility = View.VISIBLE
            emptyText.text = act.getText(R.string.listEmpty)
        }
        alertDialog.show()
    }

    private fun showPackageSlipListDialog() {
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, viewGroup, false)
        val builder = AlertDialog.Builder(this, R.style.RoundedAlertDialogStyle)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val closeImage = dialogView.findViewById<ImageView>(R.id.closeImage)
        closeImage.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val closeText = dialogView.findViewById<TextView>(R.id.closeText)
        val emptyText = dialogView.findViewById<TextView>(R.id.emptyText)
        val titleTxt = dialogView.findViewById<TextView>(R.id.titleTxt)
        closeText.setOnClickListener { v: View? -> alertDialog.dismiss() }
        val listView = dialogView.findViewById<ListView>(R.id.listview)
        titleTxt.text = act.getText(R.string.packageSlipListTitle)

        if (packageSlipListData != null) {
            emptyText.visibility = View.GONE

            val listClickListener = ListClickListener { _, position, `object` ->
                alertDialog.dismiss()
                val contentModel = `object` as PackageSlipList
                var position = -1
                for (i in packageSlipListData.indices) {
                    if (packageSlipListData[i].iTEMID.equals(
                            contentModel.iTEMID,
                            ignoreCase = true
                        )
                    ) {
                        position = i
                        break
                    }
                }
                //binding!!.salesOrderIdEdt.setText(packageSlipListData[position].iTEMNAME)
                selectedPackageSlip = packageSlipListData[position]
                binding!!.itemEdt.setText(packageSlipListData[position].iTEMNAME)
                binding!!.itemQtyEdt.setText(selectedPackageSlip!!.bALQTY)
                binding!!.itemDescEdt.setText(selectedPackageSlip!!.ITMDESC)
                binding!!.lessWeightEdit.setText(selectedPackageSlip!!.LESSWT)
            }

            val listViewAdapter = PackageSlipAdapter(
                act,
                act,
                R.layout.item_layout,
                packageSlipListData,
                if (selectedPackageSlip != null && selectedPackageSlip!!.iNVID!!.isNotEmpty()) Integer.valueOf(
                    selectedPackageSlip!!.iNVID!!
                ) else 0,
                listClickListener
            )
            listViewAdapter.setPartyMode(true)
            listView.adapter = listViewAdapter
            val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
            editText.visibility = View.VISIBLE
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                    // When user changed the Text
                    listViewAdapter.filter.filter(cs)
                }

                override fun beforeTextChanged(
                    arg0: CharSequence, arg1: Int, arg2: Int,
                    arg3: Int
                ) {
                }

                override fun afterTextChanged(arg0: Editable) {}
            })
        } else {
            emptyText.visibility = View.VISIBLE
            emptyText.text = act.getText(R.string.listEmpty)
        }
        alertDialog.show()
    }

    private fun validation() {
        var isError = false
        var isFocus = false
        val dateEdt = PubFun.removeSpaceFromText(binding!!.dateEdt.text.toString())
        val packingNoEdt = PubFun.removeSpaceFromText(binding!!.packingNoEdt.text.toString())
        val partyEdt = PubFun.removeSpaceFromText(binding!!.partyEdt.text.toString())
        val salesOrderIdEdt = PubFun.removeSpaceFromText(binding!!.salesOrderIdEdt.text.toString())
        val purchaseEdt = PubFun.removeSpaceFromText(binding!!.purchaseEdt.text.toString())
        val itemEdt = PubFun.removeSpaceFromText(binding!!.itemEdt.text.toString())
        val itemQtyEdt = PubFun.removeSpaceFromText(binding!!.itemQtyEdt.text.toString())

        if (dateEdt.trim().isEmpty()) {
            isError = true
            binding!!.dateLayout.error = getString(R.string.date_error)
            if (!isFocus) {
                binding!!.dateEdt.requestFocus()
                isFocus = true
            }
        }
        if (packingNoEdt.trim().isEmpty()) {
            isError = true
            binding!!.packingNoLayout.error = getString(R.string.packing_Slip_error)
            if (!isFocus) {
                binding!!.packingNoEdt.requestFocus()
                isFocus = true
            }
        }
        if (partyEdt.trim().isEmpty()) {
            isError = true
            binding!!.partyLayout.error = getString(R.string.party_error)
            if (!isFocus) {
                binding!!.partyEdt.requestFocus()
                isFocus = true
            }
        }
        if (salesOrderIdEdt.trim().isEmpty()) {
            isError = true
            binding!!.salesOrderIdLayout.error = getString(R.string.sales_order_no_error)
            if (!isFocus) {
                binding!!.salesOrderIdEdt.requestFocus()
                isFocus = true
            }
        }
        if (purchaseEdt.trim().isEmpty()) {
            isError = true
            binding!!.purchaseLayout.error = getString(R.string.purchase_order_no_error)
            if (!isFocus) {
                binding!!.purchaseEdt.requestFocus()
                isFocus = true
            }
        }
        if (itemEdt.trim().isEmpty()) {
            isError = true
            binding!!.itemLayout.error = getString(R.string.item_error)
            if (!isFocus) {
                binding!!.itemEdt.requestFocus()
                isFocus = true
            }
        }
        if (itemQtyEdt.trim().isEmpty()) {
            isError = true
            binding!!.itemQtyLayout.error = getString(R.string.item_qty_error)
            if (!isFocus) {
                binding!!.itemQtyEdt.requestFocus()
            }
        }

        if (!isError) {
            if (Utils.hasNetwork(act)) {
                val data =
                    HashMap<String, String>()
                data["packing_slip"] = packageSlipId
                data["inv_date"] = Objects.requireNonNull(binding!!.dateEdt.text).toString()
                data["party_id"] = selectedParty!!.pRTYID!!
                data["company_id"] = prefManager.loginData!!.accountYear!![0].cOMPID!!
                data["ac_year_id"] = prefManager.loginData!!.accountYear!![0].aCID!!
                data["item_id"] = selectedPackageSlip!!.iTEMID!!
                data["inv_no"] = selectedPackageSlip!!.iNWNO!!
                data["invit_id"] = selectedPackageSlip!!.iNVITID!!
                data["less_wt"] = selectedPackageSlip!!.LESSWT!!
                print("PASSING_DATA", gson.toJson(data))
                loaderModel!!.isLoading.value = true
                MySingleton.getInstance(act)!!.getRequestQueue().cancelAll("addSalesOrder")
                ApiRequest.addSalesOrder(
                    act,
                    addOrderResponse!!,
                    errorListener,
                    data,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.mainToolbar.ivBack.id -> {
                onBackPressed()
            }
            binding!!.loginBtn.id -> {
                validation()
            }
        }
    }
}