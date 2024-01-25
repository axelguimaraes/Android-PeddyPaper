package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import pt.ipp.estg.peddypaper.data.remote.firebase.PlayerRanking
import pt.ipp.estg.peddypaper.data.remote.firebase.Question

class GameMapAdminViewModel(aplication: Application) : AndroidViewModel(aplication){

    private val _gameId: MutableLiveData<String>

    private val db: FirebaseFirestore
    private val collectionGames: String
    private val collectionPlayers: String
    private val collectionQuestions: String

    private val _game: MutableLiveData<Game?>
    private val _playersRank: MutableLiveData<List<PlayerRanking>>
    private val _players: MutableLiveData<List<Player>>
    private val _questions: MutableLiveData<List<Question>>

    init {
        _gameId = MutableLiveData()

        db = Firebase.firestore
        collectionGames = "GAMES"
        collectionPlayers = "PLAYERS"
        collectionQuestions = "QUESTIONS"

        _game = MutableLiveData()
        _playersRank = MutableLiveData()
        _players = MutableLiveData()
        _questions = MutableLiveData()
    }

    val gameId: LiveData<String> = _gameId

    val game: LiveData<Game?> = _game
    val players: LiveData<List<Player>> = _players
    val questions: LiveData<List<Question>> = _questions

    fun setGameId(gameId: String){
        _gameId.value = gameId
        getGame()
    }

    private fun getGame(){
        viewModelScope.launch {
            db.collection(collectionGames)
                .document(_gameId.value.toString())
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val game = document.toObject(Game::class.java)
                        _game.postValue(game)
                        if (game != null) {
                            _playersRank.postValue(game.players.map { player -> PlayerRanking(player.player, player.score) })
                        }
                    }
                    getPlayers()
                    getQuestions()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error getting game.")

                }
        }
    }

    private fun getPlayers(){
        viewModelScope.launch {
            // for each player in _playersRank, get the player from the database
            // and add it to _players
            val list = mutableListOf<Player>()
            _playersRank.value?.forEach { playerRank ->
                Log.d(TAG, "PlayerRank: ${playerRank.player}")

                val document = playerRank.player?.get()?.await()
                Log.d(TAG, "Document: ${document?.data}")

                val player = document?.toObject(Player::class.java)
                Log.d(TAG, "Player: $player")
                if (player != null) {
                    list.add(player)
                }
            }

            _players.postValue(list)
        }
    }

    private fun getQuestions(){
        viewModelScope.launch {
            val list = mutableListOf<Question>()

            _game.value?.questions?.forEach { question ->
                val document = question.get().await()
                val question = document.toObject(Question::class.java)
                if (question != null) {
                    list.add(question)
                }
            }

            _questions.postValue(list)
        }
    }
    fun endGame(){
        viewModelScope.launch {
            db.collection(collectionGames)
                .document(_gameId.value.toString())
                .update("active", false)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                }
        }
    }
}