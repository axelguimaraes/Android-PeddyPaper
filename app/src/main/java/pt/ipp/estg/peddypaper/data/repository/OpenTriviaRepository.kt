package pt.ipp.estg.peddypaper.data.repository

import android.util.Log
import pt.ipp.estg.peddypaper.data.remote.api.*
import pt.ipp.estg.peddypaper.data.remote.model.TriviaQuestion
import pt.ipp.estg.peddypaper.data.remote.model.TriviaResponse
import retrofit2.*
import retrofit2.converter.gson.*

class OpenTriviaRepository {

    private var AMOUNT = 1
    private var CATEGORY = 9
    private var DIFFICULTY = "easy"
    private var TYPE = "multiple"

    private object RetrofitClient {
        private val base_url = "https://opentdb.com/"

        fun retrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    val openTriviaApi: OpenTriviaApi = RetrofitClient.retrofitInstance().create(OpenTriviaApi::class.java)

    suspend fun getQuestion(): TriviaQuestion? {
        return try {
            val response = openTriviaApi.getQuestions(AMOUNT, CATEGORY, DIFFICULTY, TYPE).awaitResponse()
            if (response.isSuccessful) {
                response.body()?.results?.get(0)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("OpenTriviaRepository", "Error getting question", e)
            null
        }
    }

}
