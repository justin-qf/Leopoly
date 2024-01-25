package com.app.leopoly.activity.SplashScreen

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.app.leopoly.BaseActivity
import com.app.leopoly.R
import com.app.leopoly.Utils.Utils.makeStatusBarTransparent
import com.app.leopoly.activity.HomeActivity.DashboardActivity
import com.app.leopoly.common.Constant.SPLASH_SCREEN_TIME_OUT
import com.app.leopoly.databinding.ActivitySplashBinding
import com.app.leopoly.activity.LoginActivity.LoginActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SplashScreen : BaseActivity() {
    private var binding: ActivitySplashBinding? = null
    private var appUpdateManager: AppUpdateManager? = null
    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = this
        binding = DataBindingUtil.setContentView(act, R.layout.activity_splash)
        makeStatusBarTransparent(this)
        checkForUpdates()
        handleActivity()
        // Initialize Crashlytics
        FirebaseApp.initializeApp(this)
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    private fun checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this@SplashScreen)
        appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        appUpdateInfoTask!!.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startAppUpdates(appUpdateInfo)
            }
        }
    }

    private fun startAppUpdates(appUpdateInfo: AppUpdateInfo?) {
        try {
            appUpdateManager!!.startUpdateFlowForResult(
                appUpdateInfo!!,
                AppUpdateType.IMMEDIATE,
                this@SplashScreen,
                APP_UPDATES
            )
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == APP_UPDATES) {
            if (resultCode == RESULT_CANCELED) {
                checkForUpdates()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        if (appUpdateManager != null) {
            appUpdateManager!!
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        // If an in-app update is already running, resume the update.
                        try {
                            appUpdateManager!!.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this@SplashScreen,
                                APP_UPDATES
                            )
                        } catch (e: SendIntentException) {
                            e.printStackTrace()
                        }
                    }
                }
        }
        super.onResume()
    }

    private fun handleActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefManager.loginData != null) {
                val i = Intent(act, DashboardActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.right_enter, R.anim.left_out)
                finish()
            } else {
                val i = Intent(act, LoginActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.right_enter, R.anim.left_out)
                finish()
            }
        }, SPLASH_SCREEN_TIME_OUT.toLong())
    }

    companion object {
        const val APP_UPDATES = 1001
    }
}