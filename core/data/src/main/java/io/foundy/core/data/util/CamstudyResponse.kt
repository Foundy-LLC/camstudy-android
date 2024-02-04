package io.foundy.core.data.util

import com.skydoves.sandwich.ApiResponse
import io.foundy.core.data.model.ResponseBody
import retrofit2.Response

typealias CamstudyResponse<T> = Response<ResponseBody<T>>

typealias CamstudyApiResponse<T> = ApiResponse<ResponseBody<T>>
