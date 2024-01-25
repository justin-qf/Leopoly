package com.app.leopoly.activity.HomeActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.adpters.DashboardMenuAdpter
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivityDashboardBinding
import com.app.leopoly.helper.HELPER.print
import com.app.leopoly.models.DashboardModel
import com.app.leopoly.activity.SplashScreen.SplashScreen
import com.app.leopoly.apiHandling.ApiRequest
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.orderDetailListResponse.DeleteOrderListResponse
import com.app.leopoly.helper.HELPER
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.vovance.omcsalesapp.Common.PubFun
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class DashboardActivity : BaseActivity(), OnOffsetChangedListener, OnClickListener {

    private var mIsTheTitleVisible = false
    private var mIsTheTitleContainerVisible = true
    private var binding: ActivityDashboardBinding? = null
    private var deleteOrderResponse: Response.Listener<String>? = null
    private var errorListener: Response.ErrorListener? = null
    private var loaderModel: BaseViewModel? = null
    private val passingData = HashMap<String, String>()
    // private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(act, R.layout.activity_dashboard)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        init()
        binding!!.logoutBtn.setOnClickListener(this)
        getDeleteOrderResponse()
        // FirebaseApp.initializeApp(this)
        // firebaseAnalytics = FirebaseAnalytics.getInstance(this)
//        val bundle = Bundle()
//        bundle.putString("ITEM", "21")
//        bundle.putString("NAME", "JUSTIN") // Replace with the actual name value
//        firebaseAnalytics.logEvent("TESTING", bundle)
    }

    private fun init() {
        binding!!.menuRecycler.visibility = View.VISIBLE
        dashboardList()
        binding!!.toolbar.title = ""
        setSupportActionBar(binding!!.toolbar)
        binding!!.nameTxt.text = prefManager.loginData!!.uSERNAME.toString().ifEmpty { "" }
        print("loginResponse", gson.toJson(prefManager.loginData))
        setAppBar()
    }

    private fun setAppBar() {
        binding!!.appbarLayout.addOnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            val isShow = true
            var scrollRange = -1
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (scrollRange + verticalOffset == 0) {
                binding!!.toolbarTitle.text = prefManager.loginData!!.uSERNAME
                binding!!.toolbarTitle.visibility = View.VISIBLE
                binding!!.collapsingToolbarLayout.setCollapsedTitleTextColor(
                    ContextCompat.getColor(
                        act,
                        R.color.colorWhite
                    )
                )
            } else if (isShow) {
                binding!!.toolbarTitle.visibility = View.GONE
            }
        }
    }

    private fun logout() {
        val idAddress = prefManager.ipAddress
        prefManager.Logout()
        prefManager.ipAddress = idAddress
        val i = Intent(act, SplashScreen::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out)
        finish()
    }

    private fun dashboardList() {

        val hasPackSlip = prefManager.loginData?.PACKSLIP == "Y"
        val hasOrderReg = prefManager.loginData?.ORDERREG == "Y"
        val hasPsUpdate =
            prefManager.loginData?.PSUPDATE == "Y" || prefManager.loginData?.PSUPDATE == null
        val trueCount = listOf(hasPackSlip, hasOrderReg, hasPsUpdate).count { it }

        val menuList = ArrayList<DashboardModel>()
        var model = DashboardModel()
        model.menuId = Constant.PACKING_SLIP
        model.menuName = getString(R.string.packagin_list)
        model.menuImage = R.drawable.order_entry
        if (hasPackSlip) menuList.add(model)

        model = DashboardModel()
        model.menuId = Constant.SALES_ORDERs
        model.menuName = getString(R.string.salesOrderTitle)
        model.menuImage = R.drawable.packing_slip
        if (hasOrderReg) menuList.add(model)

        model = DashboardModel()
        model.menuId = Constant.DISPATCH
        model.menuName = getString(R.string.sales_order_update)
        model.menuImage = R.drawable.packageslip
        if (hasPsUpdate) menuList.add(model)

        model = DashboardModel()
        model.menuId = Constant.PENDING_ORDER
        model.menuName = getString(R.string.pending_orders)
        model.menuImage = R.drawable.ic_other_request
        // menuList.add(model)
        val dashboardMenuAdapter = DashboardMenuAdpter(menuList, this)

        val spanCount = when {
            hasPackSlip && hasOrderReg && hasPsUpdate -> 2 // All three are "Y"
            hasPackSlip || hasOrderReg || hasPsUpdate -> 1 // One of them is "Y"
            else -> 2 // Default span count when none of the conditions is met
        }
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(
            this,
            spanCount
        )
        binding!!.menuRecycler.layoutManager = mLayoutManager
        binding!!.menuRecycler.setHasFixedSize(true)
        binding!!.menuRecycler.adapter = dashboardMenuAdapter
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val maxScroll = appBarLayout.totalScrollRange
        val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
        handleAlphaOnTitle(percentage)
        handleToolbarTitleVisibility(percentage)
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                binding!!.toolbarTitle.text = ""
                binding!!.toolbarTitle.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding!!.toolbar.setBackgroundColor(getColor(R.color.colorPrimary))
                }
                startAlphaAnimation(
                    binding!!.toolbarTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                mIsTheTitleVisible = true
            }
        } else {
            if (mIsTheTitleVisible) {
                binding!!.toolbarTitle.text = ""
                binding!!.toolbarTitle.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding!!.toolbarTitle.setBackgroundColor(getColor(R.color.colorTransperent))
                }
                startAlphaAnimation(
                    binding!!.toolbarTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                mIsTheTitleVisible = false
            }
        }
    }

    private fun handleAlphaOnTitle(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(
                    binding!!.mainLinearlayoutTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.INVISIBLE
                )
                mIsTheTitleContainerVisible = false
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(
                    binding!!.mainLinearlayoutTitle,
                    ALPHA_ANIMATIONS_DURATION.toLong(),
                    View.VISIBLE
                )
                mIsTheTitleContainerVisible = true
            }
        }
    }

    companion object {
        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.8f
        private const val PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.2f
        private const val ALPHA_ANIMATIONS_DURATION = 100
        private fun startAlphaAnimation(v: View?, duration: Long, visibility: Int) {
            val alphaAnimation =
                if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
            alphaAnimation.duration = duration
            alphaAnimation.fillAfter = true
            v!!.startAnimation(alphaAnimation)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        act.finishAffinity()
        act.finish()
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
                print("deletePackageResponse", deletePackageResponse.toString())
//                if (deletePackageResponse.status == 1 && deletePackageResponse.message != null) {
//                    PubFun.commonDialog(
//                        act,
//                        getString(R.string.dashboard),
//                        deletePackageResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
//                        false,
//                        clickListener = {
//                        })
//                } else {
//                    PubFun.commonDialog(
//                        act,
//                        getString(R.string.dashboard),
//                        deletePackageResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
//                        false,
//                        clickListener = {
//                            act.onBackPressed()
//                        })
//                }
            } catch (e: Exception) {
                print("Exception", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { obj: VolleyError -> obj.printStackTrace() }
    }

    @Deprecated("Deprecated in Java")
    override fun update(observable: Observable?, data: Any?) {
        super.update(observable, data)
        runOnUiThread {
            when (leoPolyApp.observer!!.value) {
                Constant.OBSERVER_BACK_FROM_PACKAGE_SLIP -> {
                    print("OBSERVER_BACK_FROM_PACKAGE_SLIP", "DONE")
                    if (Utils.hasNetwork(act)) {
                        loaderModel!!.isLoading.value = true
                        passingData["order_id"] =
                            if (prefManager.orderData != null) prefManager.orderData!!.iNVID.toString()
                                .trim() else ""
                        ApiRequest.deleteOrderById(
                            act,
                            deleteOrderResponse!!,
                            errorListener,
                            passingData,
                            prefManager
                        )
                    } else {
                        HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            binding!!.logoutBtn.id -> {
                PubFun.logoutDialog(act, clickListener = {
                    logout()
                })
            }
        }
    }
}