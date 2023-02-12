package io.foundy.data

import com.google.gson.Gson
import io.foundy.data.extension.getDataOrThrowMessage
import io.foundy.data.model.common.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response

class ResponseExtensionTest {

    @Test
    fun `should have data when calling getDataOrThrowMessage from successful response`() {
        // given
        val response = Response.success(ResponseBody(data = 10, message = ""))

        // when
        val data = response.getDataOrThrowMessage()

        // then
        assertEquals(10, data)
    }

    @Test
    fun `should throw error message when calling getDataOrThrowMessage from failure response`() {
        // given
        val message = "error message"
        val response = Response.error<ResponseBody<Int>>(
            404,
            Gson().toJson(ResponseBody(data = null, message = message)).toResponseBody(null)
        )

        // when, then
        assertThrows(message, Exception::class.java) { response.getDataOrThrowMessage() }
    }
}
