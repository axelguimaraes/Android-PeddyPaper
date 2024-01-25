package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import kotlin.math.log

class MenuYourCreatedGamesViewModel(aplication : Application) : AndroidViewModel(aplication) {

    private val db: FirebaseFirestore
    private val collectionName: String

    private val _auth: FirebaseAuth

    private var _currentPlayer: String
    private var _playerDocRef: DocumentReference

    private val _games: MutableLiveData<List<Game>>

    private val _newGame: MutableLiveData<Game>

    init {
        db = Firebase.firestore
        collectionName = "GAMES"

        _auth = Firebase.auth

        _currentPlayer = ""
        getAuthUser()
        _playerDocRef = db.collection("PLAYERS").document(_currentPlayer)

        _games = MutableLiveData(listOf())
        getGames()

        _newGame = MutableLiveData()
    }

    private fun getAuthUser(){
        viewModelScope.launch {
            _auth.currentUser?.let { user ->
                _currentPlayer = user.uid
            }
        }
    }

    val games: LiveData<List<Game>> = _games
    val newGame: LiveData<Game> = _newGame

    private fun getGames(){
        viewModelScope.launch {
            db.collection(collectionName)
                .whereEqualTo("owner", _playerDocRef)
                .get()
                .addOnSuccessListener { documents ->
                    val gamesList = mutableListOf<Game>()
                    for (document in documents) {
                        val game = document.toObject(Game::class.java)
                        Log.d(TAG, "game: ${document.id} => ${document.data}")
                        gamesList.add(game)
                    }
                    _games.postValue(gamesList)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    fun createGame(gameName: String) {
        viewModelScope.launch {
            val game = Game(name = gameName, owner = _playerDocRef)
            db.collection(collectionName)
                .add(game)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    val newGame = game.copy(id = documentReference.id)
                    db.collection(collectionName).document(documentReference.id).set(newGame)
                    _newGame.postValue(newGame)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

}