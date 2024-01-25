package com.app.leopoly.activity.PackageSlipEntryActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.activity.HomeActivity.DashboardActivity
import com.app.leopoly.activity.OrderDetailListActivity
import com.app.leopoly.activity.ScannerActivity.ScannerActivity
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.orderDetailListResponse.DeleteOrderListResponse
import com.app.leopoly.apiHandling.orderDetailResponse.AddOrderDetailResponse
import com.app.leopoly.apiHandling.orderDetailResponse.OrderDetailResponse
import com.app.leopoly.common.CodeReUse
import com.app.leopoly.common.Constant
import com.app.leopoly.common.LeoPolyApp
import com.app.leopoly.databinding.ActivityPackingSlipEntryBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.helper.MySingleton
import com.app.leopoly.models.GetScannerData
import com.google.android.material.textview.MaterialTextView
import com.vovance.omcsalesapp.Common.PubFun
import java.net.SocketTimeoutException
import java.util.*

class PackingSlipEntryActivity : BaseActivity(), View.OnClickListener {
    private var binding: ActivityPackingSlipEntryBinding? = null
    private var orderDetailsResponse: Response.Listener<String>? = null
    private var addOrderDetailsResponse: Response.Listener<String>? = null
    private var deleteOrderResponse: Response.Listener<String>? = null
    private var errorListener: Response.ErrorListener? = null
    private var loaderModel: BaseViewModel? = null
    val data = HashMap<String, String>()
    private var orderResponse: OrderDetailResponse? = null
    private var totalBoxesWT: String? = "0"
    private var totalGrossWT: String? = "0"
    private var totalNetWT: String? = "0"
    private var isFromNextBtn: Boolean? = false
    private var isFromOrderListScreen: Boolean? = false
    private var selectedBtn: Int? = 0
    private var itemQTY: String? = null
    private var itemID: String? = null
    private var itemName: String? = null
    private var itemDesc: String? = null
    private var lessWt: String? = null
    private var invId: String? = null
    private var isDataAdded: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_packing_slip_entry)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        //onClickListener
        binding!!.toolbar.ivBack.setOnClickListener(this)
        binding!!.finishBtn.setOnClickListener(this)
        binding!!.nextBtn.setOnClickListener(this)
        binding!!.saveNListBtn.setOnClickListener(this)
        binding!!.toolbar.ivEye.setOnClickListener(this)
        binding!!.fetchBtn.setOnClickListener(this)
        binding!!.toolbar.title.setText(R.string.packing)
        binding!!.toolbar.ivEye.visibility = View.VISIBLE
        getAddOrderResponse()
        getDeleteOrderResponse()
        getDataFromIntent()
        getPackingDetails()
        //Remove Errors
        CodeReUse.RemoveError(binding!!.productSizeTxt, binding!!.sizeLayout)
        CodeReUse.RemoveError(binding!!.rollNoIdTxt, binding!!.rollNoLayout)
        CodeReUse.RemoveError(binding!!.boxesIdTxt, binding!!.boxesLayout)
        CodeReUse.RemoveError(binding!!.totalBoxWT, binding!!.totalBoxLayout)
        CodeReUse.RemoveError(binding!!.grossWTTxt, binding!!.grossLayout)
        CodeReUse.RemoveError(binding!!.totalGrossWTTxt, binding!!.totalGrossWTLayout)
        CodeReUse.RemoveError(binding!!.netWtIdTxt, binding!!.netWtLayout)
        CodeReUse.RemoveError(binding!!.totalNetWtId, binding!!.totalNetWtLayout)
        getPackingDetailsUsingEditText()
        editEditText(true)
        binding!!.scannerTxt.requestFocus()
    }

    private fun getDataFromIntent() {
        isFromNextBtn = intent.getBooleanExtra("isFromNextBtn", false)
        isFromOrderListScreen = intent.getBooleanExtra("isFromOrderListScreen", false)
        itemQTY = intent.getStringExtra("itemQTY")
        itemID = intent.getStringExtra("itemID")
        itemName = intent.getStringExtra("itemName")
        lessWt = intent.getStringExtra("lessWt")
        invId = intent.getStringExtra("invID")
        itemDesc = intent.getStringExtra("itemDesc")
        binding!!.itemEdt.setText(itemName)
        binding!!.lessWtEdt.setText(lessWt)
        binding!!.itemDescEdt.setText(itemDesc)
        HELPER.print("itemQTY", itemQTY.toString())
        HELPER.print("itemID", itemID.toString())
        HELPER.print("itemName", itemName.toString())
        HELPER.print("invId", invId.toString())
        HELPER.print("lessWt", lessWt.toString())
    }

    private fun getPackingDetailsUsingEditText() {
        //binding!!.scannerTxt.imeOptions = EditorInfo.IME_ACTION_DONE;
        binding!!.scannerTxt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                // The "Done" or "Go" button on the keyboard was pressed
                // Perform your API call here
                Utils.hideKeyboard(act)
                val inputText = binding!!.scannerTxt.text.toString()
                if (inputText.isNotEmpty()) {
                    scanningBarcodeApi()
                }
                true // Return true to consume the event
            } else {
                false // Return false to let the system handle the event
            }
        }
//        binding!!.scannerTxt.addTextChangedListener(object : TextWatcher {
//            private val delay: Long = 2000 // Adjust the delay as needed
//            private val handler = Handler(Looper.getMainLooper())
//            private var apiCallInProgress = false
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                // Not used in this example
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // Not used in this example
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                // Check if an API call is already in progress
//                if (!apiCallInProgress) {
//                    // Remove any previously scheduled tasks
//                    handler.removeCallbacksAndMessages(null)
//                    // Schedule a new task to call the API after the specified delay
//                    handler.postDelayed({
//                        // Set the flag to indicate that an API call is in progress
//                        apiCallInProgress = true
//                        // Call your API here
//                        binding!!.scannerTxt.setText(s.toString())
//                        if (s.toString().isNotEmpty()) {
//                            getPackingDetails()
//                        }
//                        // Reset the flag after the API call is complete
//                        apiCallInProgress = false
//                    }, delay)
//                }
//            }
//        })
        binding!!.scannerTxt.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2 // Index for drawableEnd
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding!!.scannerTxt.right - binding!!.scannerTxt.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    val i = Intent(act, ScannerActivity::class.java)
                    startActivity(i)
                    act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun editEditText(isEditable: Boolean) {
        if (isEditable) {
            HELPER.changeEditText(act, binding!!.sizeLayout, binding!!.productSizeTxt, true)
            HELPER.changeEditText(act, binding!!.rollNoLayout, binding!!.rollNoIdTxt, true)
            HELPER.changeEditText(act, binding!!.boxesLayout, binding!!.boxesIdTxt, true)
            HELPER.changeEditText(act, binding!!.totalBoxLayout, binding!!.totalBoxWT, true)
            HELPER.changeEditText(act, binding!!.grossLayout, binding!!.grossWTTxt, true)
            HELPER.changeEditText(
                act,
                binding!!.totalGrossWTLayout,
                binding!!.totalGrossWTTxt,
                true
            )
            HELPER.changeEditText(act, binding!!.netWtLayout, binding!!.netWtIdTxt, true)
            HELPER.changeEditText(act, binding!!.totalNetWtLayout, binding!!.totalNetWtId, true)
            HELPER.changeEditText(act, binding!!.unitLayout, binding!!.unitIdTxt, true)
            HELPER.changeEditText(act, binding!!.remarkLayout, binding!!.remarkId, true)
        } else {
            HELPER.changeEditText(act, binding!!.sizeLayout, binding!!.productSizeTxt, false)
            HELPER.changeEditText(act, binding!!.rollNoLayout, binding!!.rollNoIdTxt, false)
            HELPER.changeEditText(act, binding!!.boxesLayout, binding!!.boxesIdTxt, false)
            HELPER.changeEditText(act, binding!!.totalBoxLayout, binding!!.totalBoxWT, false)
            HELPER.changeEditText(act, binding!!.grossLayout, binding!!.grossWTTxt, false)
            HELPER.changeEditText(
                act,
                binding!!.totalGrossWTLayout,
                binding!!.totalGrossWTTxt,
                false
            )
            HELPER.changeEditText(act, binding!!.netWtLayout, binding!!.netWtIdTxt, false)
            HELPER.changeEditText(act, binding!!.totalNetWtLayout, binding!!.totalNetWtId, false)
            HELPER.changeEditText(act, binding!!.unitLayout, binding!!.unitIdTxt, false)
            HELPER.changeEditText(act, binding!!.remarkLayout, binding!!.remarkId, false)
        }
    }

    private fun scanningBarcodeApi() {
        try {
            if (Utils.hasNetwork(act)) {
                //data["id"] = prefManager.scannerId.toString().ifEmpty { "2109230001" }
                data["id"] = binding!!.scannerTxt.text.toString().ifEmpty { "" }
                loaderModel!!.isLoading.value = true
                ApiRequest.getPackingOrder(
                    act,
                    orderDetailsResponse!!,
                    errorListener,
                    data,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        } catch (e: Exception) {
            loaderModel!!.isLoading.value = false
            HELPER.commonDialog(act, getString(R.string.serverErrorMessage))
            e.printStackTrace()
        }
    }

    private fun getPackingDetails() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                HELPER.showProgress(act)
            } else {
                HELPER.hideProgress(act)
            }
        }
        orderDetailsResponse = Response.Listener<String> { response: String? ->
            HELPER.print("orderDetailsResponse", response)
            loaderModel!!.isLoading.value = false
            try {
                orderResponse = gson.fromJson(response, OrderDetailResponse::class.java)
                HELPER.printData("orderDetailsResponse", gson.toJson(orderResponse!!.data))
                if (orderResponse!!.data != null && orderResponse!!.status == 1) {
                    if (itemID == orderResponse!!.data!!.ITEMID) {
                        prefManager.scannerId = ""
                        prefManager.packingDetail = orderResponse!!.data!!

                        HELPER.print("orderResponse", gson.toJson(orderResponse!!.data!!))

                        totalBoxesWT = if (orderResponse!!.data!!.BOX!!.toString().isNotEmpty())
                            (orderResponse!!.data!!.BOX!!.toDouble() + totalBoxesWT!!.toDouble()).toFloat()
                                .toString() else ("1".toDouble() + totalBoxesWT!!.toDouble()).toFloat()
                            .toString()

                        HELPER.print("GROSS_WT", orderResponse!!.data!!.GROSSWT!!.toString())

                        totalGrossWT =
                            (orderResponse!!.data!!.GROSSWT!!.toDouble() + totalGrossWT!!.toDouble()).toFloat()
                                .toString()
                        HELPER.print("totalGrossWT", orderResponse!!.data!!.GROSSWT!!.toString())


                        binding!!.netWtIdTxt.setText(
                            (orderResponse!!.data!!.GROSSWT!!.toDouble() - lessWt!!.toDouble()).toFloat()
                                .toString()
                        )

                        totalNetWT =
                            (binding!!.netWtIdTxt.text.toString()
                                .toDouble() + totalNetWT!!.toDouble()).toString()

                        setWithClearText(true, false)
                    } else {
                        //SAME ID
                        PubFun.itemQTYAlertDialog(
                            act,
                            noClickListener = {
                                binding!!.scannerTxt.requestFocus()
                            },
                            yesClickListener = {
                                setWithClearText(false, true)
                                binding!!.scannerTxt.requestFocus()
                                //onBackPressed()
                            },
                            fromSameItemId = true
                        )
                    }
                } else if (orderResponse!!.status == 0) {
                    setWithClearText(false, true)
                    PubFun.commonDialog(
                        act,
                        getString(R.string.packageTitle),
                        orderResponse!!.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            binding!!.scannerTxt.requestFocus()
                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.packageTitle),
                        getString(R.string.notFound),
                        false,
                        clickListener = {
                            binding!!.scannerTxt.requestFocus()
                        })
                }
            } catch (e: Exception) {
                HELPER.print("EXCEPTION", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }

//        if (Utils.hasNetwork(act)) {
//            //data["id"] = prefManager.scannerId.toString().ifEmpty { "2109230001" }
//            data["id"] = binding!!.scannerTxt.text.toString().ifEmpty { "" }
//            loaderModel!!.isLoading.value = true
//            ApiRequest.getPackingOrder(
//                act,
//                orderDetailsResponse!!,
//                errorListener,
//                data,
//                prefManager
//            )
//        } else {
//            HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
//        }
    }

    private fun setWithClearText(isSetText: Boolean, isFromDetailScreen: Boolean) {
        CodeReUse.hideKeyboard(act, binding!!.scannerTxt)
        if (isSetText) {
            binding!!.productSizeTxt.setText(orderResponse!!.data!!.SIZE)
            binding!!.rollNoIdTxt.setText(orderResponse!!.data!!.ROLLS)
            if (orderResponse!!.data!!.BOX!!.isEmpty()) "1" else binding!!.boxesIdTxt.setText(
                orderResponse!!.data!!.BOX
            )
            binding!!.totalBoxWT.setText(totalBoxesWT)
            binding!!.grossWTTxt.setText(orderResponse!!.data!!.GROSSWT)
            binding!!.totalGrossWTTxt.setText(totalGrossWT)
            //binding!!.netWtIdTxt.setText(orderResponse!!.data!!.NETWT)
            binding!!.totalNetWtId.setText(totalNetWT!!.toFloat().toString())
            binding!!.unitIdTxt.setText(orderResponse!!.data!!.UNIT)
            val itemQty = itemQTY!!.toFloat()
            val totalNetWt = binding!!.totalNetWtId.text.toString().toFloat()
            HELPER.print("itemQty", itemQty.toString())
            HELPER.print("totalNetWt", totalNetWt.toString())
            if (totalNetWt > itemQty) {
                PubFun.itemQTYAlertDialog(
                    act,
                    noClickListener = {
                        setWithClearText(false, true)
                    },
                    yesClickListener = {
                    },
                    fromSameItemId = false
                )
            }
        } else {
            if (isFromDetailScreen) {
                HELPER.clearText(binding!!.scannerTxt)
            }
            HELPER.clearText(binding!!.productSizeTxt)
            HELPER.clearText(binding!!.rollNoIdTxt)
            HELPER.clearText(binding!!.boxesIdTxt)
            HELPER.clearText(binding!!.totalBoxWT)
            HELPER.clearText(binding!!.grossWTTxt)
            HELPER.clearText(binding!!.totalGrossWTTxt)
            HELPER.clearText(binding!!.netWtIdTxt)
            HELPER.clearText(binding!!.totalNetWtId)
            HELPER.clearText(binding!!.unitIdTxt)
        }
    }

    private fun getDeleteOrderResponse() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                HELPER.showProgress(act)
            } else {
                HELPER.hideProgress(act)
            }
        }
        deleteOrderResponse = Response.Listener<String> { response: String? ->
            loaderModel!!.isLoading.value = false
            try {
                val deletePackageResponse: DeleteOrderListResponse =
                    gson.fromJson(response, DeleteOrderListResponse::class.java)
                if (deletePackageResponse.status == 1 && deletePackageResponse.message != null) {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.packageTitle),
                        deletePackageResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            scanningBarcodeApi()
                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.packageTitle),
                        deletePackageResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
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

    private fun getAddOrderResponse() {
        loaderModel!!.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                HELPER.showProgress(act)
            } else {
                HELPER.hideProgress(act)
            }
        }
        addOrderDetailsResponse = Response.Listener<String> { response: String? ->
            loaderModel!!.isLoading.value = false
            try {
                val orderResponse: AddOrderDetailResponse =
                    gson.fromJson(response, AddOrderDetailResponse::class.java)
                isDataAdded = true
                HELPER.printData("addOrderResponse", gson.toJson(orderResponse.data))
                if (orderResponse.data != null && orderResponse.status == 1) {
                    when (selectedBtn) {
                        0 -> {
                            val i = Intent(act, DashboardActivity::class.java)
                            startActivity(i)
                            act.overridePendingTransition(
                                R.anim.right_enter,
                                R.anim.left_out
                            )
                            act.finishAffinity()
                            finish()
                        }
                        1 -> {
                            setWithClearText(false, true)
                            isFromNextBtn = true
                        }
                        else -> {
                            val i = Intent(act, OrderDetailListActivity::class.java)
                            i.putExtra("isFromAPI", true)
                            i.putExtra("invID", invId.toString().ifEmpty { "" })
                            startActivity(i)
                            act.overridePendingTransition(
                                R.anim.right_enter,
                                R.anim.left_out
                            )
                        }
                    }
//                    PubFun.commonDialog(
//                        act,
//                        getString(R.string.packageTitle),
//                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
//                        false,
//                        clickListener = {
//                            when (selectedBtn) {
//                                0 -> {
//                                    val i = Intent(act, DashboardActivity::class.java)
//                                    startActivity(i)
//                                    act.overridePendingTransition(
//                                        R.anim.right_enter,
//                                        R.anim.left_out
//                                    )
//                                    act.finishAffinity()
//                                    finish()
//                                }
//                                1 -> {
//                                    setWithClearText(false, true)
//                                    isFromNextBtn = true
////                                    val i = Intent(act, PackingSlipEntryActivity::class.java)
////                                    i.putExtra("isFromNextBtn", true)
////                                    startActivity(i)
////                                    act.overridePendingTransition(
////                                        R.anim.right_enter,
////                                        R.anim.left_out
////                                    )
//                                }
//                                else -> {
//                                    val i = Intent(act, OrderDetailListActivity::class.java)
//                                    i.putExtra("isFromAPI", true)
//                                    startActivity(i)
//                                    act.overridePendingTransition(
//                                        R.anim.right_enter,
//                                        R.anim.left_out
//                                    )
//                                }
//                            }
//                        })
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.packageTitle),
                        orderResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            act.onBackPressed()
                        })
                }
            } catch (e: Exception) {
                HELPER.print("Exception", e.printStackTrace().toString())
                setWithClearText(false, true)
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError ->
            obj.printStackTrace()
            setWithClearText(false, true)
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.toolbar.ivBack.id -> {
                onBackPressed()
            }
            binding!!.toolbar.ivEye.id -> {
                val i = Intent(act, OrderDetailListActivity::class.java)
                i.putExtra("isFromAPI", false)
                i.putExtra("invID", invId.toString().ifEmpty { "" })
                startActivity(i)
                act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
            }
            binding!!.scannerTxt.id -> {
                val i = Intent(act, ScannerActivity::class.java)
                startActivity(i)
                act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
            }
            binding!!.fetchBtn.id -> {
                if (binding!!.fetchTxt.text.toString().isNotEmpty()) {
                    scanningBarcodeApi()
                }
            }
            binding!!.finishBtn.id -> {
                onBackPressed()
//                selectedBtn = 0
//                validation()
            }
            binding!!.nextBtn.id -> {
                selectedBtn = 1
                validation()
            }
            binding!!.saveNListBtn.id -> {
                selectedBtn = 2
                validation()
            }
        }
    }

    private fun validation() {
        var isError = false
        var isFocus = false
        val productSizeTxt = PubFun.removeSpaceFromText(binding!!.productSizeTxt.text.toString())
        val rollNoIdTxt = PubFun.removeSpaceFromText(binding!!.rollNoIdTxt.text.toString())
        val boxesIdTxt = PubFun.removeSpaceFromText(binding!!.boxesIdTxt.text.toString())
        val totalGrossWT = PubFun.removeSpaceFromText(binding!!.totalBoxWT.text.toString())
        val grossWTTxt = PubFun.removeSpaceFromText(binding!!.grossWTTxt.text.toString())
        val totalGrossWTTxt = PubFun.removeSpaceFromText(binding!!.totalGrossWTTxt.text.toString())
        val netWtIdTxt = PubFun.removeSpaceFromText(binding!!.netWtIdTxt.text.toString())
        val totalNetWtId = PubFun.removeSpaceFromText(binding!!.totalNetWtId.text.toString())
        // val unitIdTxt = PubFun.removeSpaceFromText(binding!!.unitIdTxt.text.toString())
        // val remarkId = PubFun.removeSpaceFromText(binding!!.remarkId.text.toString())

//        if (productSizeTxt.trim().isEmpty()) {
//            isError = true
//            binding!!.sizeLayout.error = getString(R.string.size_error)
//            if (!isFocus) {
//                binding!!.productSizeTxt.requestFocus()
//                isFocus = true
//            }
//        }
//        if (rollNoIdTxt.trim().isEmpty()) {
//            isError = true
//            binding!!.rollNoLayout.error = getString(R.string.roll_no_error)
//            if (!isFocus) {
//                binding!!.rollNoIdTxt.requestFocus()
//                isFocus = true
//            }
//        }
        if (boxesIdTxt.trim().isEmpty()) {
            isError = true
            binding!!.boxesLayout.error = getString(R.string.box_error)
            if (!isFocus) {
                binding!!.boxesIdTxt.requestFocus()
                isFocus = true
            }
        }
        if (totalGrossWT.trim().isEmpty()) {
            isError = true
            binding!!.totalBoxLayout.error = getString(R.string.total_box_error)
            if (!isFocus) {
                binding!!.totalBoxWT.requestFocus()
                isFocus = true
            }
        }
        if (grossWTTxt.trim().isEmpty()) {
            isError = true
            binding!!.grossLayout.error = getString(R.string.gross_error)
            if (!isFocus) {
                binding!!.grossWTTxt.requestFocus()
                isFocus = true
            }
        }
        if (totalGrossWTTxt.trim().isEmpty()) {
            isError = true
            binding!!.totalGrossWTLayout.error = getString(R.string.total_gross_error)
            if (!isFocus) {
                binding!!.totalGrossWTTxt.requestFocus()
                isFocus = true
            }
        }
        if (netWtIdTxt.trim().isEmpty()) {
            isError = true
            binding!!.netWtLayout.error = getString(R.string.netWT_error)
            if (!isFocus) {
                binding!!.netWtIdTxt.requestFocus()
                isFocus = true
            }
        }
        if (totalNetWtId.trim().isEmpty()) {
            isError = true
            binding!!.totalNetWtLayout.error = getString(R.string.total_netWT_error)
            if (!isFocus) {
                binding!!.totalNetWtId.requestFocus()
                isFocus = true
            }
        }
//        if (unitIdTxt.trim().isEmpty()) {
//            isError = true
//            binding!!.unitLayout.error = getString(R.string.unit_error)
//            if (!isFocus) {
//                binding!!.unitIdTxt.requestFocus()
//                isFocus = true
//            }
//        }
//        if (remarkId.trim().isEmpty()) {
//            isError = true
//            binding!!.remarkLayout.error = getString(R.string.remark_error)
//            if (!isFocus) {
//                binding!!.remarkId.requestFocus()
//                isFocus = true
//            }
//        }
        if (!isError) {
            if (Utils.hasNetwork(act)) {
                val data = HashMap<String, String>()
                data["size"] = binding!!.productSizeTxt.text.toString().trim()
                data["roll_no"] = binding!!.rollNoIdTxt.text.toString().trim()
                data["boxes"] = binding!!.boxesIdTxt.text.toString().trim()
                data["gross_weight"] = binding!!.grossWTTxt.text.toString().trim()
                data["net_weight"] = binding!!.netWtIdTxt.text.toString().trim()
                data["unit"] = binding!!.unitIdTxt.text.toString().trim()
                data["remark"] = binding!!.remarkId.text.toString().trim()

                //New Logic
                if(isFromOrderListScreen==true){
                    data["order_id"] =
                        invId.toString()
                }else {
                    data["order_id"] =
                        prefManager.orderData!!.iNVID.toString().trim()
                }
                //Old Logic
//                if(prefManager.orderData!=null && prefManager.orderData!!.iNVID.toString().isNotEmpty() ){
//                    data["order_id"] =
//                       prefManager.orderData!!.iNVID.toString().trim()
//                }else{
//                    data["order_id"] =
//                        invId.toString()
//                }
//                data["order_id"] =
//                    invId.toString().ifEmpty { prefManager.orderData!!.iNVID.toString().trim() }
                data["qr_scan_id"] = binding!!.scannerTxt.text.toString().ifEmpty { "" }
                data["rmountid"] = orderResponse!!.data!!.RMOUTID.toString().trim()
                data["company_id"] = prefManager.loginData!!.accountYear!![0].cOMPID!!.toString()
                data["ac_year_id"] = prefManager.loginData!!.accountYear!![0].aCID!!.toString()
                // data["less_wt"] = // grosswt-lesswt
                loaderModel!!.isLoading.value = true
                HELPER.print("PACKING_SLIP_PASSING_DATA",gson.toJson(data))
                MySingleton.getInstance(act)!!.getRequestQueue().cancelAll("addPackage")
                ApiRequest.addPackageInfo(
                    act,
                    addOrderDetailsResponse!!,
                    errorListener,
                    data,
                    prefManager
                )
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.scannerTxt.requestFocus()
//        if (binding!!.scannerTxt.text.toString().isNotEmpty())
//            scanningBarcodeApi()
    }

    @Deprecated("Deprecated in Java")
    override fun update(observable: Observable?, data: Any?) {
        super.update(observable, data)
        runOnUiThread {
            when (leoPolyApp.observer!!.value) {
                Constant.OBSERVER_SCANNING_DATA -> {
                    val getScannerData: GetScannerData =
                        gson.fromJson(leoPolyApp.observer!!.data, GetScannerData::class.java)
                    HELPER.print("GET_SCAN_DATA", gson.toJson(getScannerData))
                    binding!!.scannerTxt.setText(getScannerData.scannerData.toString())
                    if (getScannerData.scannerData.toString().isNotEmpty()) {
                        binding!!.scannerTxt.requestFocus()
                        scanningBarcodeApi()
                    }
                }
                Constant.OBSERVER_BACK_FROM_DETAIL_SCREEN -> {
                    setWithClearText(false, true)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (isDataAdded != true) {
            if(isFromOrderListScreen != true)
            LeoPolyApp.instance!!.observer!!.value = Constant.OBSERVER_BACK_FROM_PACKAGE_SLIP
//            if (isFromOrderListScreen == true) {
//                if(prefManager.orderData!=null)
//                prefManager.orderData!!.iNVID =
//                    invId.toString().ifEmpty { "" }
//            }
        }
        backPressDialog(
            act,
            noClickListener = {
                setWithClearText(false, true)
                binding!!.scannerTxt.requestFocus()
            },
            yesClickListener = {
                act.onBackPressed()
                super.onBackPressed()
            },
            totalNW = totalNetWT!!.toFloat().toString().ifEmpty { "Not Filled" },
            isDataAdded = isDataAdded
        )
    }

    private fun backPressDialog(
        act: Activity?,
        yesClickListener: () -> Unit,
        noClickListener: () -> Unit,
        totalNW: String,
        isDataAdded: Boolean?,
    ) {
        try {
            if (act != null && !act.isFinishing) {
                val builder = AlertDialog.Builder(act, R.style.RoundShapeTheme)
                val customLayout = act.layoutInflater.inflate(R.layout.logout_dialog, null)
                val loggedUserId = customLayout.findViewById<TextView>(R.id.userId)
                val title = customLayout.findViewById<MaterialTextView>(R.id.tvDialogTitle)
                val cancelBtn = customLayout.findViewById<AppCompatButton>(R.id.btnDialogCancel)
                val logoutBtn = customLayout.findViewById<AppCompatButton>(R.id.btnDialogLogout)
                loggedUserId.text =
                    if (isDataAdded == true) "Are you sure want to cancel order?\nTotal NET WT:$totalNW" else act.getString(
                        R.string.cancelOrderMessage
                    )
                title.text = act.getString(R.string.cancelOrderTitle)
                cancelBtn.visibility = View.VISIBLE
                cancelBtn.text = act.getString(R.string.no)
                logoutBtn.text = act.getString(R.string.yes)
                builder.setView(customLayout)
                val dialog = builder.create()
                dialog.window!!.setBackgroundDrawableResource(R.color.transparent_color)
                dialog.setCancelable(false)
                if (!dialog.isShowing) {
                    dialog.show()
                }
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                    noClickListener()
                }
                logoutBtn.setOnClickListener {
                    dialog.dismiss()
                    yesClickListener()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
/*
* s.o. no fetch krva query call che
* STRTMP = 'SELECT MAX(INVNO) AS INVNO FROM SALORDERM WHERE COMPID = ' + STR(MCOMPID) + ' AND '
	STRTMP = STRTMP + "ACID = " + STR(MACID)
	MLSTRESULT = SQLEXEC(LNCONN,STRTMP,'RSTMP')
	IF ISNULL(INVNO)
		MORD = 1
	ELSE
		MORD = VAL(INVNO) + 1
	ENDIF
	USE
	Thisform.Pageframe1.Page1.Date.Value = Date()
	IF THISFORM.Pageframe1.Page1.DATE.Value >= CTOD('01/04/2022')
	MLASTBILLNO = REPLICATE('0',4-LEN(LTRIM(STR(MORD)))) + LTRIM(STR(MORD))
	MLASTBILLNO = MLASTBILLNO + "/" + RIGHT(LTRIM(STR(YEAR(MACSDATE))),2)+'-'+RIGHT(LTRIM(STR(YEAR(MACEDATE))),2)
	THISFORM.PAGEframe1.PAGE1.SONO.VALUE = MLASTBILLNO
* */
