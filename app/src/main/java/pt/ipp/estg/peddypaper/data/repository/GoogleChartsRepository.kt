package pt.ipp.estg.peddypaper.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import okhttp3.ResponseBody
import pt.ipp.estg.peddypaper.data.remote.api.GoogleChartsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleChartsRepository {

    private val CHT = "qr"
    private val CHS = "500x500"
    private val CHOE = "UTF-8"

    private val mainHandler = Handler(Looper.getMainLooper())

    private object RetrofitClient {
        private val BASE_URL = "https://chart.googleapis.com/"

        fun retrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    val googleChartsApi: GoogleChartsApi = RetrofitClient.retrofitInstance().create(GoogleChartsApi::class.java)

    fun getQRCode(data: String) {
        val call = googleChartsApi.getQRCode(CHT, CHS, data, CHOE)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()
                processResponseBody(responseBody)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun processResponseBody(responseBody: ResponseBody?): Bitmap? {
        responseBody?.let {
            val byteArray: ByteArray = it.bytes()
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        return null
    }
}
