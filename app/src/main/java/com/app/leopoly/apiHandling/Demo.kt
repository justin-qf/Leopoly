package com.app.leopoly.apiHandling

import android.app.Application

class Demo(application: Application) : BaseViewModel(application) {
    fun makeApiCall() {
        isLoading.value = true
    }
}