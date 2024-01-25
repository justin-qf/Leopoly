package com.app.leopoly.apiHandling

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    var androidViewModel: AndroidViewModel? = null
    var disposable: Disposable? = null
    var isLoading = MutableLiveData<Boolean>()
}