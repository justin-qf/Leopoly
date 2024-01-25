package com.app.leopoly.activity.SalesOrderActivity

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
import com.app.leopoly.adpters.PartyAdapter
import com.app.leopoly.adpters.TransportAdapter
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.SalesOrderNoResponse
import com.app.leopoly.apiHandling.partyResponse.PartyListData
import com.app.leopoly.apiHandling.partyResponse.PartyResponse
import com.app.leopoly.apiHandling.partyResponse.TransportListData
import com.app.leopoly.apiHandling.partyResponse.TransportResponse
import com.app.leopoly.common.CodeReUse
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivitySalesOrderBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.interfaces.ListClickListener
import com.google.android.material.textfield.TextInputEditText
import com.vovance.omcsalesapp.Common.PubFun
import java.util.*

class SalesOrderActivity : BaseActivity(), View.OnClickListener {

    var binding: ActivitySalesOrderBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var transportListResponse: Response.Listener<String?>? = null
    private var salesPartyListResponse: Response.Listener<String?>? = null
    private var salesOrderNoResponse: Response.Listener<String?>? = null
    private var errorListener: Response.ErrorListener? = null
    private val partyListData: ArrayList<PartyListData> = ArrayList()
    private var selectedParty: PartyListData? = null
    private var selectedShipToParty: PartyListData? = null
    private val transportListData: ArrayList<TransportListData> = ArrayList()
    private var selectedTransport: TransportListData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = DataBindingUtil.setContentView(act, R.layout.activity_sales_order)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        setSupportActionBar(binding!!.mainToolbar.toolbar)
        binding!!.mainToolbar.title.setText(R.string.salesOrderTitle)
        binding!!.mainToolbar.ivBack.setOnClickListener(this)
        binding!!.continues.setOnClickListener(this)
        hideKeyboard(binding!!.parentLayout)
        //Remove Errors
        CodeReUse.RemoveError(binding!!.salesOrderDateEdt, binding!!.salesOrderLayout)
        CodeReUse.RemoveError(binding!!.salesNoEdt, binding!!.salesNoLayout)
        CodeReUse.RemoveError(binding!!.billEdt, binding!!.billLayout)
        CodeReUse.RemoveError(binding!!.shipEdt, binding!!.shipLayout)
        CodeReUse.RemoveError(binding!!.purOrderEdt, binding!!.purOrderLayout)
        CodeReUse.RemoveError(binding!!.orderDateEdt, binding!!.orderDateLayout)
        CodeReUse.RemoveError(binding!!.transportEdt, binding!!.transportLayout)
        CodeReUse.RemoveError(binding!!.dueDayEdt, binding!!.dueDayLayout)
        CodeReUse.RemoveError(binding!!.destinationEdt, binding!!.destinationLayout)
        setDate()
        binding!!.transportEdt.setOnClickListener { showTransportListDialog() }
        binding!!.billEdt.setOnClickListener { showSalesPartyListDialog(false) }
        binding!!.shipEdt.setOnClickListener { showSalesPartyListDialog(true) }
        //API Calls
        transportApi
        partyApi
        salesOrderNo
    }

    private fun setDate() {
        binding!!.salesOrderDateEdt.setOnClickListener {
            HELPER.showDatePickerDialog(
                act,
                binding!!.salesOrderDateEdt
            )
        }
        binding!!.orderDateEdt.setOnClickListener {
            HELPER.showDatePickerDialog(
                act,
                binding!!.orderDateEdt
            )
        }
    }

    private val transportApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            transportListResponse = Response.Listener { response: String? ->
                HELPER.print("PartyListResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val transportList = gson.fromJson(response, TransportResponse::class.java)
                    transportListData.clear()
                    if (transportList.status == 1 && transportList.data.isNotEmpty()) {
                        transportListData.addAll(0, transportList.data)
                    } else {
                        PubFun.commonDialog(
                            act,
                            getString(R.string.order),
                            transportList.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                            false,
                            clickListener = {
                            })
                    }
                } catch (e: Exception) {
                    HELPER.print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
            if (Utils.hasNetwork(act)) {
                loaderModel!!.isLoading.value = true
                ApiRequest.getTransportList(act, transportListResponse, errorListener, prefManager)

            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    private val salesOrderNo: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            salesOrderNoResponse = Response.Listener { response: String? ->
                HELPER.print("salesOrderNoResponse",response)
                loaderModel!!.isLoading.value = false
                try {
                    val salesOrderNoResponse = gson.fromJson(response, SalesOrderNoResponse::class.java)
                    HELPER.print("OrderNo",salesOrderNoResponse.data.toString().trim())
                    binding!!.salesNoEdt.setText(salesOrderNoResponse.data.toString().trim())
                } catch (e: Exception) {
                    HELPER.print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
            if (Utils.hasNetwork(act)) {
                val data = HashMap<String, String>()
                data["compId"] = prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!
                data["accId"] = prefManager.loginData!!.accountYear?.get(0)?.aCID!!
                loaderModel!!.isLoading.value = true
                ApiRequest.getSalesOrderNo(
                    act,
                    salesOrderNoResponse,
                    errorListener,
                    data,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    private val partyApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            salesPartyListResponse = Response.Listener { response: String? ->
                HELPER.print("PartyListResponse", response)
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
                            })
                    }
                } catch (e: Exception) {
                    HELPER.print("Exception", e.printStackTrace().toString())
                }
            }
            errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
            if (Utils.hasNetwork(act)) {
                loaderModel!!.isLoading.value = true
                ApiRequest.getSalesPartyList(
                    act,
                    salesPartyListResponse,
                    errorListener,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }

    private fun validation() {
        var isError = false
        var isFocus = false
        val salesOrderDateEdt =
            PubFun.removeSpaceFromText(binding!!.salesOrderDateEdt.text.toString())
        val salesNoEdt = PubFun.removeSpaceFromText(binding!!.salesNoEdt.text.toString())
        val billEdt = PubFun.removeSpaceFromText(binding!!.billEdt.text.toString())
        val shipEdt = PubFun.removeSpaceFromText(binding!!.shipEdt.text.toString())
        val purOrderEdt = PubFun.removeSpaceFromText(binding!!.purOrderEdt.text.toString())
        val orderDateEdt = PubFun.removeSpaceFromText(binding!!.orderDateEdt.text.toString())
        val transportEdt = PubFun.removeSpaceFromText(binding!!.transportEdt.text.toString())
        val dueDayEdt = PubFun.removeSpaceFromText(binding!!.dueDayEdt.text.toString())
        val destinationEdt = PubFun.removeSpaceFromText(binding!!.destinationEdt.text.toString())

        if (salesOrderDateEdt.trim().isEmpty()) {
            isError = true
            binding!!.salesOrderLayout.error = getString(R.string.soDateError)
            if (!isFocus) {
                binding!!.salesOrderDateEdt.requestFocus()
                isFocus = true
            }
        }
        if (salesNoEdt.trim().isEmpty()) {
            isError = true
            binding!!.salesNoLayout.error = getString(R.string.soNoError)
            if (!isFocus) {
                binding!!.salesNoEdt.requestFocus()
                isFocus = true
            }
        }
        if (billEdt.trim().isEmpty()) {
            isError = true
            binding!!.billLayout.error = getString(R.string.billToError)
            if (!isFocus) {
                binding!!.billEdt.requestFocus()
                isFocus = true
            }
        }
        if (shipEdt.trim().isEmpty()) {
            isError = true
            binding!!.shipLayout.error = getString(R.string.shipToError)
            if (!isFocus) {
                binding!!.shipEdt.requestFocus()
                isFocus = true
            }
        }
        if (purOrderEdt.trim().isEmpty()) {
            isError = true
            binding!!.purOrderLayout.error = getString(R.string.purOrderError)
            if (!isFocus) {
                binding!!.purOrderEdt.requestFocus()
                isFocus = true
            }
        }
        if (orderDateEdt.trim().isEmpty()) {
            isError = true
            binding!!.orderDateLayout.error = getString(R.string.orderDateError)
            if (!isFocus) {
                binding!!.orderDateEdt.requestFocus()
                isFocus = true
            }
        }
        if (transportEdt.trim().isEmpty()) {
            isError = true
            binding!!.transportLayout.error = getString(R.string.transportError)
            if (!isFocus) {
                binding!!.transportEdt.requestFocus()
            }
        }

//        if (dueDayEdt.trim().isEmpty()) {
//            isError = true
//            binding!!.dueDayLayout.error = getString(R.string.dueDaysError)
//            if (!isFocus) {
//                binding!!.dueDayEdt.requestFocus()
//            }
//        }
//        if (destinationEdt.trim().isEmpty()) {
//            isError = true
//            binding!!.destinationLayout.error = getString(R.string.destinationError)
//            if (!isFocus) {
//                binding!!.destinationEdt.requestFocus()
//            }
//        }

        if (!isError) {
            val salesOrderData = ArrayList<String>()
            salesOrderData.add(salesOrderDateEdt.trim())
            salesOrderData.add(salesNoEdt.trim())
            salesOrderData.add(selectedParty!!.pRTYID.toString().trim())
            salesOrderData.add(selectedShipToParty!!.pRTYID.toString().trim())
            salesOrderData.add(purOrderEdt.trim())
            salesOrderData.add(orderDateEdt.trim())
            salesOrderData.add(selectedTransport!!.TRPID.toString().trim())
            salesOrderData.add(dueDayEdt.trim())
            salesOrderData.add(destinationEdt.trim())
            val i = Intent(act, SalesOrderDetailActivity::class.java)
            i.putStringArrayListExtra(Constant.SALES_ORDER_DATA, salesOrderData)
            startActivity(i)
            act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.mainToolbar.ivBack.id -> {
                onBackPressed()
            }
            binding!!.continues.id -> {
                validation()
            }
        }
    }

    private fun showTransportListDialog() {
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
        titleTxt.text = act.getText(R.string.transportDialogTitle)
        if (transportListData != null) {
            emptyText.visibility = View.GONE
            val listClickListener = ListClickListener { _, _, `object` ->
                alertDialog.dismiss()
                val contentModel = `object` as TransportListData
                var position = -1
                for (i in transportListData.indices) {
                    if (transportListData[i].TRPID.equals(contentModel.TRPID, ignoreCase = true)) {
                        position = i
                        break
                    }
                }
                binding!!.transportEdt.setText(transportListData[position].TRPNAME)
                selectedTransport = transportListData[position]
            }
            val listViewAdapter = TransportAdapter(
                act,
                act,
                R.layout.item_layout,
                transportListData,
                if (selectedTransport != null && selectedTransport!!.TRPID!!.isNotEmpty()) Integer.valueOf(
                    selectedTransport!!.TRPID!!
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

    private fun showSalesPartyListDialog(isFromShipTo: Boolean) {
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
        titleTxt.text =
            if (isFromShipTo) act.getText(R.string.selectShipToDialogTitle) else act.getText(R.string.selectBillToDialogTitle)
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
                if (isFromShipTo) {
                    binding!!.shipEdt.setText(partyListData[position].pNAME)
                    selectedShipToParty = partyListData[position]
                } else {
                    binding!!.billEdt.setText(partyListData[position].pNAME)
                    binding!!.shipEdt.setText(partyListData[position].pNAME)
                    selectedParty = partyListData[position]
                    selectedShipToParty = partyListData[position]
                }
                HELPER.print("selectedShipToParty::", gson.toJson(selectedShipToParty))
                HELPER.print("selectedParty::", gson.toJson(selectedParty))
            }
            val listViewAdapter = PartyAdapter(
                act,
                act,
                R.layout.item_layout,
                partyListData,
                if (isFromShipTo && selectedShipToParty != null && selectedShipToParty!!.pRTYID!!.isNotEmpty()) {
                    Integer.valueOf(selectedShipToParty!!.pRTYID!!)
                } else if (selectedParty != null && selectedParty!!.pRTYID!!.isNotEmpty()) {
                    Integer.valueOf(selectedParty!!.pRTYID!!)
                } else 0,
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

}