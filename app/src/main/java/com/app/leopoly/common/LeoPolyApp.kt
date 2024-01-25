package com.app.leopoly.common

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.util.Base64
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LeoPolyApp : MultiDexApplication() {
    private var sharedPreferences: SharedPreferences? = null
    var observer: AppObserver? = null
        private set
    private var mRequestQueue: RequestQueue? = null
    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        ApplicationLifeCycle.Companion.init(instance)
        observer = AppObserver(applicationContext)
        mRequestQueue = Volley.newRequestQueue(instance)

        //initializeFonts();
        printHashKey()
    }

    private fun initializeFonts() {
        proximanovaAltBold = Typeface.createFromAsset(assets, "fonts/proximanova_altbold.otf")
        proximanovaBlack = Typeface.createFromAsset(assets, "fonts/proximanova_black.otf")
        proximanovaBold = Typeface.createFromAsset(assets, "fonts/proximanova_bold.otf")
        proximanovaRegular = Typeface.createFromAsset(assets, "fonts/proximanova_regular.otf")
    }

    private fun printHashKey() {
        // Add code to print out the key hash
        try {
            @SuppressLint("PackageManagerGetSignatures") val info = packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        } catch (ignored: NoSuchAlgorithmException) {
        }
    }

    private val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(applicationContext)
            }
            return mRequestQueue!!
        }

    fun <T> addToRequestQueue(req: Request<T>, tag: Int) {
        req.setShouldCache(false)
        req.retryPolicy = DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        req.tag = if (tag == 0) 111 else tag
        requestQueue.add(req)
    }

    fun cancelPendingRequests(tag: Any?) {
        if (mRequestQueue != null) {
            mRequestQueue!!.cancelAll(tag)
        }
    }

    private val preferences: SharedPreferences
        get() = getSharedPreferences("StoreCookie", MODE_PRIVATE).also {
            sharedPreferences = it
        }

    fun saveCookie(cookie: String?) {
        if (cookie == null) {
            return
        }
        val prefs = preferences ?: return
        val editor = prefs.edit()
        editor.putString("cookie", cookie)
        editor.apply()
    }

    val cookie: String?
        get() {
            val prefs = preferences
            return prefs.getString("cookie", "")
        }

    fun removeCookie() {
        val prefs = preferences
        val editor = prefs.edit()
        editor.remove("cookie")
        editor.apply()
    } /*   public HashMap getHeader() {
        HashMap headers = new HashMap();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        if(prefManager.getUserToken() != null) {
            headers.put("Authorization", "Bearer "+prefManager.getUserToken());
        }
        return headers;
    }*/

    companion object {
        private var proximanovaAltBold: Typeface? = null
        private var proximanovaBlack: Typeface? = null
        private var proximanovaBold: Typeface? = null
        private var proximanovaRegular: Typeface? = null

        @get:Synchronized
        var instance: LeoPolyApp? = null
            private set

        /*   private StoreUserData storeUserData;
    private ProfileObject profileObject;
    private PrefManager prefManager;*/
        fun getsInstance(): LeoPolyApp? {
            return instance
        }
    }
}