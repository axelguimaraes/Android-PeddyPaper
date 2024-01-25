package pt.ipp.estg.peddypaper.data.remote.model

data class TriviaResponse(
    val response_code: Int,
    val results: List<TriviaQuestion>
)