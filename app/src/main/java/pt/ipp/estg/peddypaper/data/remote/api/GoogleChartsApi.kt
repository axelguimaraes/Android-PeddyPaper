package pt.ipp.estg.peddypaper.data.remote.api

import okhttp3.*
import retrofit2.Call
import retrofit2.http.*

interface GoogleChartsApi {

    @GET("chart")
    fun getQRCode(
        @Query("cht") cht: String, // type = qr
        @Query("chs") chs: String, // size = "500x500"
        @Query("chl") chl: String, // data = "Hello World"
        @Query("choe") choe: String, // encoding = "UTF-8"
    ): Call<ResponseBody>
}