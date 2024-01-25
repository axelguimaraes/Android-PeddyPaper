package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import pt.ipp.estg.peddypaper.data.remote.firebase.PlayerRanking

class UserProfileViewModel(application: Application): AndroidViewModel(application) {

    private val db: FirebaseFirestore
    private val collectionPlayers: String
    private val collectionGames: String

    private val _auth: FirebaseAuth

    private var _currentPlayerId: String
    private var _currentPlayer: MutableLiveData<Player?>
    private var _playerDocRef: DocumentReference

    private val _games: MutableLiveData<List<Game>>

    init {
        db = com.google.firebase.ktx.Firebase.firestore
        collectionPlayers = "PLAYERS"
        collectionGames = "GAMES"

        _auth = Firebase.auth

        _currentPlayerId = ""
        getAuthUser()
        getPlayer()
        _currentPlayer = MutableLiveData(Player())
        _playerDocRef = db.collection(collectionPlayers).document(_currentPlayerId)

        _games = MutableLiveData(listOf())
        getGames()
    }

    private fun getAuthUser(){
        viewModelScope.launch {
            _auth.currentUser?.let { user ->
                _currentPlayerId = user.uid
            }
        }
    }

    private fun getPlayer(){
        viewModelScope.launch {
            db.collection(collectionPlayers)
                .document(_currentPlayerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val player = document.toObject(Player::class.java)
                        Log.d(TAG, "player: ${document.id} => ${document.data}")
                        _currentPlayer.postValue(player)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    val player: MutableLiveData<Player?> = _currentPlayer
    val games: MutableLiveData<List<Game>> = _games

    private fun getGames(){
        viewModelScope.launch {
            db.collection(collectionGames)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    val gamesList = mutableListOf<Game>()

                    for (document in documents) {
                        val game = document.toObject(Game::class.java)
                        val playerInGame = game.players.any { playerRanking ->
                            playerRanking.player?.equals(_playerDocRef) ?: false
                        }

                        if (playerInGame) {
                            Log.d(TAG, "game: ${document.id} => ${document.data}")

                            // Construct new game with replaced player list to only include the current player
                            val newGame = game.copy(
                                players = listOf(
                                    PlayerRanking(
                                        player = _playerDocRef,
                                        score = game.players.first { playerRanking ->
                                            playerRanking.player?.equals(_playerDocRef) ?: false
                                        }.score
                                    )
                                )
                            )

                            gamesList.add(newGame)
                        }
                    }

                    _games.postValue(gamesList)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting games: ", exception)
                }
        }
    }

}