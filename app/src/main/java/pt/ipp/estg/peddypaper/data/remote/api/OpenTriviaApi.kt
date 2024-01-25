package pt.ipp.estg.peddypaper.data.remote.api

import retrofit2.http.*
import pt.ipp.estg.peddypaper.data.remote.model.*
import retrofit2.*

interface OpenTriviaApi {
    @GET("api.php")
    fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String
    ): Call<TriviaResponse>
}
