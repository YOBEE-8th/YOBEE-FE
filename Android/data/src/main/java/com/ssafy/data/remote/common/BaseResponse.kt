package com.ssafy.data.remote.common

import com.google.gson.annotations.SerializedName

internal data class BaseResponse<T>(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?,
)