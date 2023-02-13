package io.foundy.data.extension

import com.google.gson.Gson
import io.foundy.data.model.common.ResponseBody
import retrofit2.Response

fun <T> Response<ResponseBody<T>>.getErrorMessage(): String? {
    if (isSuccessful) throw IllegalStateException()

    return try {
        message()
        val errorBodyJson = requireNotNull(errorBody()).string()
        val responseBody = Gson().fromJson(errorBodyJson, ResponseBody::class.java)
        responseBody.message
    } catch (e: Exception) {
        e.message
    }
}

fun <T> Response<ResponseBody<T>>.getDataOrThrowMessage(): T {
    if (isSuccessful) {
        return requireNotNull(body()).data
    } else {
        throw Exception(getErrorMessage())
    }
}
