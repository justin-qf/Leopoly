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
import com.app.leopoly.adpters.ItemListAdapter
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.lessWtResponse.LessWtResponse
import com.app.leopoly.apiHandling.itemResponse.ItemListData
import com.app.leopoly.apiHandling.itemResponse.ItemResponse
import com.app.leopoly.apiHandling.lessWtResponse.LessWtData
import com.app.leopoly.apiHandling.partyResponse.PartyListData
import com.app.leopoly.common.CodeReUse
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivitySalesOrderDetailBinding
import com.app.leopoly.helper.HELPER
import com.google.android.material.textfield.TextInputEditText
import com.vovance.omcsalesapp.Common.PubFun
import java.util.*
import com.app.leopoly.interfaces.ListClickListener
import org.json.JSONArray
import org.json.JSONObject

class SalesOrderDetailActivity : BaseActivity(), View.OnClickListener {

    var binding: ActivitySalesOrderDetailBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var addSalesOrderDetailResponse: Response.Listener<JSONObject>? = null
    private var itemListApiResponse: Response.Listener<String?>? = null
    private var errorListener: Response.ErrorListener? = null
    private val itemListData: ArrayList<ItemListData> = ArrayList()
    private var selectedItemModel: ItemListData? = null
    private var receivedList: ArrayList<String>? = null
    private var lessWtResponse: Response.Listener<String?>? = null
    private val lessWtList: ArrayList<LessWtData> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_sales_order_detail)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        setSupportActionBar(binding!!.mainToolbar.toolbar)
        binding!!.mainToolbar.ivBack.setOnClickListener(this)
        binding!!.mainToolbar.title.setText(R.string.salesOrderDetailTitle)
        binding!!.nextBtn.setOnClickListener(this)
        binding!!.Save.setOnClickListener(this)
        //getDataFromIntent
        receivedList = intent.getStringArrayListExtra(Constant.SALES_ORDER_DATA)
        //AddSalesOrderAPICalling
        addSalesOrderApi()
        //Remove Errors
        CodeReUse.RemoveError(binding!!.itemNameEdt, binding!!.itemNameLayout)
        CodeReUse.RemoveError(binding!!.packingEdt, binding!!.packingLayout)
        CodeReUse.RemoveError(binding!!.lessWtEdt, binding!!.lessWtLayout)
        CodeReUse.RemoveError(binding!!.qtyEdt, binding!!.qtyLayout)
        CodeReUse.RemoveError(binding!!.rateEdt, binding!!.rateLayout)
        CodeReUse.RemoveError(binding!!.amountEdt, binding!!.amountLayout)
        CodeReUse.RemoveError(binding!!.descriptionEdt, binding!!.descriptionLayout)
        //CodeReUse.RemoveError(binding!!.grossEdt, binding!!.grossAmountLayout)
        //CodeReUse.RemoveError(binding!!.orderdescriptionEdt, binding!!.orderdescriptionLayout)
        binding!!.lessWtEdt.setText("0")
        prefManager.totalAmount = ""
        calculateAmount()
        binding!!.itemNameEdt.setOnClickListener { showItemListDialog() }
        itemApi
        getLessWt
    }

    private fun clearAllFields() {
        binding!!.itemNameEdt.setText("")
        binding!!.descriptionEdt.setText("")
        binding!!.packingEdt.setText("")
        binding!!.lessWtEdt.setText("")
        binding!!.qtyEdt.setText("")
        binding!!.rateEdt.setText("")
        binding!!.amountEdt.setText("")
        binding!!.rateEdt.clearFocus()
        binding!!.mainLayout.clearFocus()
    }

    private fun calculateAmount() {
        // Add a TextWatcher to yourEditText
        binding!!.rateEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called to notify you that characters within s are about to be replaced with new text
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within s, the text has been changed
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                if (newText.isNotEmpty()) {
                    val qtyStr = binding!!.qtyEdt.text.toString()
                    val priceStr = binding!!.rateEdt.text.toString()
                    if (qtyStr.isNotEmpty() && priceStr.isNotEmpty()) {
                        val qty = qtyStr.toDouble()
                        val price = priceStr.toDouble()
                        val total = qty * price
                        val originalAmount = total.toString().toDoubleOrNull() ?: 0.0
                        val formattedAmount = String.format("%.2f", originalAmount)
                        binding!!.amountEdt.setText(formattedAmount)
                    } else {
                        binding!!.amountEdt.setText("")
                    }
                }
            }
        })
        binding!!.qtyEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called to notify you that characters within s are about to be replaced with new text
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within s, the text has been changed
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                if (newText.isNotEmpty()) {
                    val qtyStr = binding!!.qtyEdt.text.toString()
                    val priceStr = binding!!.rateEdt.text.toString()
                    if (qtyStr.isNotEmpty() && priceStr.isNotEmpty()) {
                        val qty = qtyStr.toDouble()
                        val price = priceStr.toDouble()
                        val total = qty * price
                        val originalAmount = total.toString().toDoubleOrNull() ?: 0.0
                        val formattedAmount = String.format("%.2f", originalAmount)
                        binding!!.amountEdt.setText(formattedAmount)
                    } else {
                        binding!!.amountEdt.setText("")
                    }
                }
            }
        })
    }

    private fun createData() {
//        salesOrderData.add(salesOrderDateEdt.trim())
//        salesOrderData.add(salesNoEdt.trim())
//        salesOrderData.add(selectedParty!!.pRTYID.toString().trim())
//        salesOrderData.add(selectedShipToParty!!.pRTYID.toString().trim())
//        salesOrderData.add(purOrderEdt.trim())
//        salesOrderData.add(orderDateEdt.trim())
//        salesOrderData.add(selectedTransport!!.TRPID.toString().trim())
//        salesOrderData.add(dueDayEdt.trim())
//        salesOrderData.add(destinationEdt.trim())
    }

    private val itemApi: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            itemListApiResponse = Response.Listener { response: String? ->
                HELPER.print("PartyListResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val partyList = gson.fromJson(response, ItemResponse::class.java)
                    itemListData.clear()
                    if (partyList.status == 1 && partyList.data!!.isNotEmpty()) {
                        itemListData.addAll(0, partyList.data!!)
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
                ApiRequest.getAllSalesParty(
                    act,
                    itemListApiResponse,
                    errorListener,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }

    private val getLessWt: Unit
        get() {
            loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
                if (isLoading) {
                    HELPER.showProgress(act)
                } else {
                    HELPER.hideProgress(act)
                }
            }
            lessWtResponse = Response.Listener { response: String? ->
                HELPER.print("GetLessWtResponse", response)
                loaderModel!!.isLoading.value = false
                try {
                    val lessWtResponse = gson.fromJson(response, LessWtResponse::class.java)
                    lessWtList.clear()
                    if (lessWtResponse.status == 1) {
                        lessWtList.addAll(0, lessWtResponse.data)
                        if (lessWtResponse.data.isNotEmpty() && lessWtList[0].LESSWT.toString()
                                .isNotEmpty()
                        ) {
                            binding!!.lessWtEdt.setText(lessWtList[0].LESSWT.toString())
                        } else {
                            binding!!.lessWtEdt.setText("0")
                        }
                    } else {
                        PubFun.commonDialog(
                            act,
                            getString(R.string.order),
                            getString(R.string.serverErrorMessage),
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
//                val orderResponse: OrderResponse =
//                    gson.fromJson(response, OrderResponse::class.java)
//                if (orderResponse.data != null && orderResponse.status == 1) {
//                    PubFun.commonDialog(
//                        act,
//                        getString(R.string.salesOrderDetailTitle),
//                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
//                        true,
//                        clickListener = {
//
//                        })
//                } else {
//                    PubFun.commonDialog(
//                        act,
//                        getString(R.string.salesOrderDetailTitle),
//                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
//                        false,
//                        clickListener = {
//                            act.onBackPressed()
//                        })
//                }
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

            binding!!.nextBtn.id -> {
                validation(false)
            }

            binding!!.Save.id -> {
                validation(true)
            }
        }
    }

    override fun onResume() {
        if (binding!!.itemNameEdt.text.toString().isNotEmpty()) {
            clearAllFields()
            selectedItemModel = null
        }
        super.onResume()
    }

    private val itemArrayList = JSONArray()
    private fun addItemToList() {
        val newItemObj = createItemObject()
        // Check if the item with the same 'item_id' is already in the list
        if (!isItemInList(newItemObj.getString("item_id"))) {
            itemArrayList.put(newItemObj)
        }
//        val itemObj = JSONObject()
//        itemObj.put("itemName", PubFun.removeSpaceFromText(binding!!.itemNameEdt.text.toString()))
//        itemObj.put(
//            "item_id",
//            PubFun.removeSpaceFromText(selectedItemModel!!.itemid.toString().trim())
//        )
//        itemObj.put("packing", PubFun.removeSpaceFromText(binding!!.packingEdt.text.toString()))
//        itemObj.put("less_wt", PubFun.removeSpaceFromText(binding!!.lessWtEdt.text.toString()))
//        itemObj.put("qty", PubFun.removeSpaceFromText(binding!!.qtyEdt.text.toString()))
//        itemObj.put("rate", PubFun.removeSpaceFromText(binding!!.rateEdt.text.toString()))
//        itemObj.put("amount", PubFun.removeSpaceFromText(binding!!.amountEdt.text.toString()))
//        itemObj.put(
//            "description",
//            PubFun.removeSpaceFromText(binding!!.descriptionEdt.text.toString())
//        )
//        itemObj.put(
//            "company_id",
//            PubFun.removeSpaceFromText(prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!)
//        )
//        itemObj.put(
//            "ac_year_id",
//            PubFun.removeSpaceFromText(prefManager.loginData!!.accountYear?.get(0)?.aCID!!)
//        )
//        itemArrayList.put(itemObj)
    }

    private fun createItemObject(): JSONObject {
        val itemObj = JSONObject()
        itemObj.put("itemName", PubFun.removeSpaceFromText(binding!!.itemNameEdt.text.toString()))
        itemObj.put(
            "item_id",
            PubFun.removeSpaceFromText(selectedItemModel!!.itemid.toString().trim())
        )
        itemObj.put("packing", PubFun.removeSpaceFromText(binding!!.packingEdt.text.toString()))
        itemObj.put("less_wt", PubFun.removeSpaceFromText(binding!!.lessWtEdt.text.toString()))
        itemObj.put("qty", PubFun.removeSpaceFromText(binding!!.qtyEdt.text.toString()))
        itemObj.put("rate", PubFun.removeSpaceFromText(binding!!.rateEdt.text.toString()))
        itemObj.put("amount", PubFun.removeSpaceFromText(binding!!.amountEdt.text.toString()))
        itemObj.put(
            "description",
            PubFun.removeSpaceFromText(binding!!.descriptionEdt.text.toString())
        )
        itemObj.put(
            "company_id",
            PubFun.removeSpaceFromText(prefManager.loginData!!.accountYear?.get(0)?.cOMPID!!)
        )
        itemObj.put(
            "ac_year_id",
            PubFun.removeSpaceFromText(prefManager.loginData!!.accountYear?.get(0)?.aCID!!)
        )

        return itemObj
    }

    private fun isItemInList(itemId: String): Boolean {
        for (i in 0 until itemArrayList.length()) {
            val existingItemId = itemArrayList.getJSONObject(i).getString("item_id")
            if (existingItemId == itemId) {
                // Item with the same 'item_id' is already in the list
                return true
            }
        }
        // Item is not in the list
        return false
    }

    private fun submitOrder() {

        val orderObject = JSONObject()
        orderObject.put("soDate", PubFun.removeSpaceFromText(receivedList!![0]))
        orderObject.put("soNo", PubFun.removeSpaceFromText(receivedList!![1]))
        orderObject.put("billTo", PubFun.removeSpaceFromText(receivedList!![2]))
        orderObject.put("shipTo", PubFun.removeSpaceFromText(receivedList!![3]))
        orderObject.put("purOrderNo", PubFun.removeSpaceFromText(receivedList!![4]))
        orderObject.put("orderDate", PubFun.removeSpaceFromText(receivedList!![5]))
        orderObject.put("transport", PubFun.removeSpaceFromText(receivedList!![6]))
        orderObject.put("dueDays", PubFun.removeSpaceFromText(receivedList!![7]))
        orderObject.put("destination", PubFun.removeSpaceFromText(receivedList!![8]))
        orderObject.put("order_details", itemArrayList)
        HELPER.print("PASSING_DATA", gson.toJson(orderObject))
        val i = Intent(act, SalesOrderInfoActivity::class.java)
        i.putExtra("orderObject", orderObject.toString())
        startActivity(i)
        act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
    }

    private fun validation(isFromSave: Boolean) {
        var isError = false
        var isFocus = false
        val itemNameEdt = PubFun.removeSpaceFromText(binding!!.itemNameEdt.text.toString())
        val packingEdt = PubFun.removeSpaceFromText(binding!!.packingEdt.text.toString())
        val lessWtEdt = PubFun.removeSpaceFromText(binding!!.lessWtEdt.text.toString())
        val qtyEdt = PubFun.removeSpaceFromText(binding!!.qtyEdt.text.toString())
        val rateEdt = PubFun.removeSpaceFromText(binding!!.rateEdt.text.toString())
        val amountEdt = PubFun.removeSpaceFromText(binding!!.amountEdt.text.toString())
        val descriptionEdt = PubFun.removeSpaceFromText(binding!!.descriptionEdt.text.toString())
        //val grossEdt = PubFun.removeSpaceFromText(binding!!.grossEdt.text.toString())

        if (itemNameEdt.trim().isEmpty()) {
            isError = true
            binding!!.itemNameLayout.error = getString(R.string.itemNameError)
            if (!isFocus) {
                binding!!.itemNameEdt.requestFocus()
                isFocus = true
            }
        }
        if (packingEdt.trim().isEmpty()) {
            isError = true
            binding!!.packingLayout.error = getString(R.string.packingError)
            if (!isFocus) {
                binding!!.packingEdt.requestFocus()
                isFocus = true
            }
        }
        if (lessWtEdt.trim().isEmpty()) {
            isError = true
            binding!!.lessWtLayout.error = getString(R.string.lessWtError)
            if (!isFocus) {
                binding!!.lessWtEdt.requestFocus()
                isFocus = true
            }
        }
        if (qtyEdt.trim().isEmpty()) {
            isError = true
            binding!!.qtyLayout.error = getString(R.string.qtyError)
            if (!isFocus) {
                binding!!.qtyEdt.requestFocus()
                isFocus = true
            }
        }
        if (rateEdt.trim().isEmpty()) {
            isError = true
            binding!!.rateLayout.error = getString(R.string.rateError)
            if (!isFocus) {
                binding!!.rateEdt.requestFocus()
                isFocus = true
            }
        }
        if (amountEdt.trim().isEmpty()) {
            isError = true
            binding!!.amountLayout.error = getString(R.string.amountError)
            if (!isFocus) {
                binding!!.amountEdt.requestFocus()
                isFocus = true
            }
        }
        if (descriptionEdt.trim().isEmpty()) {
            isError = true
            binding!!.descriptionLayout.error = getString(R.string.descriptionError)
            if (!isFocus) {
                binding!!.descriptionEdt.requestFocus()
            }
        }
//        if (grossEdt.trim().isEmpty()) {
//            isError = true
//            binding!!.grossAmountLayout.error = getString(R.string.grossAmountError)
//            if (!isFocus) {
//                binding!!.grossEdt.requestFocus()
//            }
//        }
        if (!isError) {
            if (Utils.hasNetwork(act)) {
                if (isFromSave) {
//                    val originalAmount = binding!!.amountEdt.text.toString().toDoubleOrNull() ?: 0.0
//                    val formattedAmount = String.format("%.2f", originalAmount)
//                    prefManager.totalAmount = formattedAmount
                    storeTotalAmount()
                    addItemToList()
                    submitOrder()
                } else {
                    Utils.hideKeyboard(act)
                    addItemToList()
                    storeTotalAmount()
                    selectedItemModel = null
                    clearAllFields()
                }
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    }

    private fun storeTotalAmount() {
        val currentTotalAmount = prefManager.totalAmount!!.toDoubleOrNull() ?: 0.0
        val additionalAmount =
            binding!!.amountEdt.text.toString().toDoubleOrNull() ?: 0.0
        val totalAmount =
            (currentTotalAmount + additionalAmount).toFloat().toString()
        val originalAmount = totalAmount.toDoubleOrNull() ?: 0.0
        val formattedAmount = String.format("%.2f", originalAmount)
        prefManager.totalAmount = formattedAmount
        HELPER.print("TOTAL_AMOUNT", prefManager.totalAmount.toString())

    }

    private fun showItemListDialog() {
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
            act.getText(R.string.dialogTitleItem)

        if (itemListData != null) {
            emptyText.visibility = View.GONE
            val listClickListener = ListClickListener { _, _, `object` ->
                alertDialog.dismiss()
                val contentModel = `object` as ItemListData
                var position = -1
                for (i in itemListData.indices) {
                    if (itemListData[i].itemid.equals(contentModel.itemid, ignoreCase = true)) {
                        position = i
                        break
                    }
                }
                binding!!.itemNameEdt.setText(itemListData[position].itemname)
                //binding!!.packingEdt.setText(itemListData[position])
                //binding!!.qtyEdt.setText(itemListData[position].ordqty)
                //binding!!.lessWtEdt.setText(itemListData[position])
                //binding!!.lessWtEdt.setText(itemListData[position])
                selectedItemModel = itemListData[position]
                HELPER.print("selectedItem::", gson.toJson(selectedItemModel))
                if (Utils.hasNetwork(act)) {
                    val data = HashMap<String, String>()
//                    data["itemId"] = "310"
//                    data["partyId"] = "835"
                    data["itemId"] = selectedItemModel!!.itemid.toString().trim()
                    data["partyId"] = PubFun.removeSpaceFromText(receivedList!![2])
                    loaderModel!!.isLoading.value = true
                    HELPER.print("PASSING_DATA", gson.toJson(data))
                    ApiRequest.getLessWt(
                        act,
                        lessWtResponse,
                        errorListener,
                        data,
                        prefManager
                    )
                } else {
                    HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
                }
            }
            val listViewAdapter = ItemListAdapter(
                act,
                act,
                R.layout.item_layout,
                itemListData,
                if (selectedItemModel != null && selectedItemModel!!.itemid!!.isNotEmpty()) {
                    Integer.valueOf(selectedItemModel!!.itemid!!)
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
                    arg3: Int,
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