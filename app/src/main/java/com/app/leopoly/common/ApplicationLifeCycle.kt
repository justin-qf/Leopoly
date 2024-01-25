package com.app.leopoly.common

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.util.Log
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class ApplicationLifeCycle : ActivityLifecycleCallbacks {
    var networkChangeReceiver: NetworkUtil? = null
    var dtLastDate: Date? = null
    private var refs = 0
    private val listeners: MutableList<Listener> = CopyOnWriteArrayList()
    val isForeground: Boolean
        get() = refs > 0
    val isBackground: Boolean
        get() = refs == 0

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    override fun onActivityStarted(activity: Activity) {
        if (++refs == 1) {
            for (l in listeners) {
                try {
                    l.onBecameForeground()
                } catch (exc: Exception) {
                    Log.e(TAG, "Listener threw exception!", exc)
                }
            }
            Log.i(TAG, "Enter foreground")
        } else {
            Log.i(TAG, "still foreground")
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (--refs == 0) {
            for (l in listeners) {
                try {
                    l.onBecameBackground()
                } catch (exc: Exception) {
                    Log.e(TAG, "Listener threw exception!", exc)
                }
            }
            Log.i(TAG, "Enter background")
        } else {
            Log.i(TAG, "still background")
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    interface Listener {
        fun onBecameForeground()
        fun onBecameBackground()
    }

    companion object {
        private val TAG = ApplicationLifeCycle::class.java.name
        private var instance: ApplicationLifeCycle? = null

        /**
         * Its not strictly necessary to use this method - _usually_ invoking
         * get with a Context gives us a path to retrieve the Application and
         * initialise, but sometimes (e.g. in test harness) the ApplicationContext
         * is != the Application, and the docs make no guarantees.
         *
         * @param application
         */
        fun init(application: Application?) {
            if (instance == null) {
                instance = ApplicationLifeCycle()
                application!!.registerActivityLifecycleCallbacks(instance)
            }
        }

        operator fun get(application: Application?): ApplicationLifeCycle? {
            if (instance == null) {
                init(application)
            }
            return instance
        }

        operator fun get(ctx: Context): ApplicationLifeCycle? {
            if (instance == null) {
                val appCtx = ctx.applicationContext
                if (appCtx is Application) {
                    init(appCtx)
                }
                throw IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object"
                )
            }
            return instance
        }

        fun get(): ApplicationLifeCycle? {
            checkNotNull(instance) {
                "Foreground is not initialised - invoke " +
                        "at least once with parametrised init/get"
            }
            return instance
        }
    }
}