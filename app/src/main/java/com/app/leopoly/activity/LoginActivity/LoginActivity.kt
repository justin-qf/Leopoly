package com.app.leopoly.activity.LoginActivity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.VolleyError
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils
import com.app.leopoly.activity.HomeActivity.DashboardActivity
import com.app.leopoly.apiHandling.ApiRequest.login
import com.app.leopoly.apiHandling.BaseViewModel
import com.app.leopoly.apiHandling.Demo
import com.app.leopoly.apiHandling.loginResponse.LoginResponse
import com.app.leopoly.common.CodeReUse.RemoveError
import com.app.leopoly.common.Constant
import com.app.leopoly.databinding.ActivityLoginBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.helper.HELPER.hideProgress
import com.app.leopoly.helper.HELPER.print
import com.app.leopoly.helper.HELPER.showProgress
import com.vovance.omcsalesapp.Common.PubFun

class LoginActivity : BaseActivity() {
    private var binding: ActivityLoginBinding? = null
    private var loaderModel: BaseViewModel? = null
    private var loginResponse: Response.Listener<String?>? = null
    private var errorListener: Response.ErrorListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = DataBindingUtil.setContentView(act, R.layout.activity_login)
        act.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        loaderModel = ViewModelProvider(this)[Demo::class.java]
        hideKeyboard(binding!!.parentLayout)
        RemoveError(binding!!.emailEditText, binding!!.emailLayout)
        RemoveError(binding!!.passwordEdittext, binding!!.passwordLayout)
        //binding!!.loginBtn.setOnClickListener { validation() }
        binding!!.loginBtn.setOnClickListener {
            validation()
        }
        //ASK IP ADDRESS
        if (prefManager.ipAddress.toString().isEmpty()) {
            PubFun.askIpAddressDialog(
                act,
                prefManager,
            )
        }
        loginApi()
    }

    private fun validation() {
        var isFocus = false
        var isError = false
        if (binding!!.emailEditText.text!!.isEmpty()) {
            binding!!.emailLayout.error = "Enter email id"
            binding!!.emailEditText.requestFocus()
            isFocus = true
            isError = true
        }
        if (binding!!.passwordEdittext.text!!.isEmpty()) {
            binding!!.passwordLayout.error = "Enter password"
            if (!isFocus) {
                binding!!.passwordEdittext.requestFocus()
            }
            isError = true
        }
        if (!isError) {
            if (Utils.hasNetwork(act)) {
                val data = HashMap<String, String>()
                data["username"] = binding!!.emailEditText.text!!.toString().ifEmpty { "admin" }
                data["password"] = binding!!.passwordEdittext.text!!.toString().ifEmpty { "admin" }
                loaderModel!!.isLoading.value = true
                login(act, loginResponse, errorListener, data, prefManager)
            } else {
                HELPER.commonDialog(act, Constant.NETWORK_ERROR_MESSAGE)
            }
        }
    }

    private fun loginApi() {
        loaderModel!!.isLoading.observe(this) { isLoading: Boolean ->
            if (isLoading) {
                showProgress(act)
            } else {
                hideProgress(act)
            }
        }
        loginResponse = Response.Listener { response: String? ->
            print("loginResponse", response)
            loaderModel!!.isLoading.value = false
            try {
                val loginResponse = gson.fromJson(response, LoginResponse::class.java)
                if (loginResponse.status == 1 && loginResponse.user != null) {
                    prefManager.loginData = loginResponse.user
                    val i = Intent(act, DashboardActivity::class.java)
                    startActivity(i)
                    act.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
                    finish()
                } else {
                    PubFun.commonDialog(
                        act,
                        getString(R.string.login),
                        loginResponse.message!!.ifEmpty { getString(R.string.serverErrorMessage) },
                        false,
                        clickListener = {
                            //act.onBackPressed()
                        })
                }
            } catch (e: Exception) {
                print("Exception", e.printStackTrace().toString())
            }
        }
        errorListener = Response.ErrorListener { error: VolleyError -> error.printStackTrace() }
    }
}