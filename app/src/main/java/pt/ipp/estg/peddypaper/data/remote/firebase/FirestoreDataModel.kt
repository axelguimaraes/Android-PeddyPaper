package pt.ipp.estg.peddypaper.data.remote.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Option(
    val text: String = "",
    val correct: Boolean = false
)

data class Question(
    val id: String = "",
    val text: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val score: Int = 0,
    val options: List<Option> = emptyList(),
    var awnsred: Boolean = true,
)

data class Game(
    val id: String = "",
    val name: String = "",
    val questions: List<DocumentReference> = emptyList(),
    val players: List<PlayerRanking> = emptyList(),
    val active: Boolean = true,
    val owner: DocumentReference? = null,
    val date: Timestamp = Timestamp.now()
)

data class Player(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val score: Int = 0,
)

data class PlayerRanking(
    val player: DocumentReference? = null,
    var score: Int = 0
)