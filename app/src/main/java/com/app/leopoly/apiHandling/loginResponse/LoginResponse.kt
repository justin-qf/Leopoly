package com.app.leopoly.apiHandling.loginResponse

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("status")
    var status = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("user")
    var user: User? = null
}