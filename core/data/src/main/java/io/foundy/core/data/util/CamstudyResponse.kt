package io.foundy.core.data.util

import io.foundy.core.data.model.ResponseBody
import retrofit2.Response

typealias CamstudyResponse<T> = Response<ResponseBody<T>>
