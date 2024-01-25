package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GameMapViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase
    val db: FirebaseFirestore
    val collectionGame: String
    val collectionQuestion: String
    val collectionPlayer: String

    val _game: MutableLiveData<Game?>
    val _questions: MutableLiveData<List<Question>>
    val _players: MutableLiveData<List<Player>>

    init {
        db = Firebase.firestore
        collectionGame = "GAMES"
        collectionQuestion = "QUESTIONS"
        collectionPlayer = "PLAYERS"

        _game = MutableLiveData()
        _questions = MutableLiveData()
        _players = MutableLiveData()
    }

    val game: LiveData<Game?> = _game
    val questions: LiveData<List<Question>> = _questions
    val players: LiveData<List<Player>> = _players

    fun loadGame(gameId: String) {
        db.collection(collectionGame)
            .document(gameId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val game = document.toObject(Game::class.java)
                    Log.w(TAG, "GameMapViewModel : Game load: $game")
                    _game.postValue(game)
                    loadQuestions()
                    loadPlayers()
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    private suspend fun getQuestionById(questionId: String): Question? {
        return suspendCoroutine { continuation ->
            db.collection(collectionQuestion)
                .document(questionId)
                .get()
                .addOnSuccessListener { document ->
                    val question = document?.toObject(Question::class.java)
                    continuation.resume(question)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val questionsList = mutableListOf<Question>()

            _game.value?.questions?.forEach { questionId ->
                val question = getQuestionById(questionId.id)
                question?.let {
                    questionsList.add(it)
                }
            }

            _questions.postValue(questionsList)
        }
    }

    private fun loadPlayers() {
        _game.value?.players?.forEach { playerId ->
            val playersList = mutableListOf<Player>()

            playerId.player?.let {
                db.collection(collectionPlayer)
                    .document(it.id)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val playerDb = document.toObject(Player::class.java)

                            // Player contains the score in the current game
                            val player = playerDb?.copy(score = playerId.score)
                            Log.e(TAG, "Player: $player")

                            player?.let { playersList.add(it) }
                        }
                        _players.postValue(playersList)
                    }
                    .addOnFailureListener { exception ->
                        println("Error getting documents: $exception")
                    }
            }
        }
    }
}
