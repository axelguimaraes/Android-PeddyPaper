package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.PlayerRanking

class MenuJoinGameViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionName = "GAMES"

    private val _auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> = _games

    private val _showJoinGameDialog = MutableLiveData<Boolean>()
    val showJoinGameDialog: LiveData<Boolean> = _showJoinGameDialog

    private val _navigateToGame = MutableLiveData<String?>()
    val navigateToGame: LiveData<String?> = _navigateToGame

    init {
        getGames()
    }

    fun getGames() {
        val userId = _auth.currentUser?.uid

        viewModelScope.launch {
            db.collection(collectionName)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener { gameDocuments ->
                    val games = gameDocuments.toObjects(Game::class.java)
                    val filteredGames = if (userId != null) {
                        games.filter { it.owner?.id != userId }
                    } else {
                        games
                    }
                    _games.postValue(filteredGames)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    fun checkIfUserInGameAndJoin(game: Game) {
        val userId = _auth.currentUser?.uid ?: return

        if (game.players.any { it.player?.id == userId }) {
            // Usuário já está no jogo, defina o valor para navegação direta
            _navigateToGame.value = game.id
        } else {
            // Usuário não está no jogo, exiba o diálogo
            _showJoinGameDialog.value = true
        }
    }

    fun resetNavigation() {
        _navigateToGame.value = null
    }

    fun resetShowJoinGameDialog() {
        _showJoinGameDialog.value = false
    }

    fun addUserToGame(gameId: String) {
        val userId = _auth.currentUser?.uid ?: return
        val userRef = db.collection("PLAYERS").document(userId)

        viewModelScope.launch {
            val gameRef = db.collection(collectionName).document(gameId)

            db.runTransaction { transaction ->
                val gameSnapshot = transaction.get(gameRef)
                val game = gameSnapshot.toObject(Game::class.java)

                game?.let {
                    // Cria uma nova lista incluindo todos os jogadores existentes mais o novo jogador
                    val updatedPlayers = it.players.toMutableList()
                    if (updatedPlayers.none { player -> player.player?.id == userId }) {
                        val newPlayerRanking = PlayerRanking(player = userRef)
                        updatedPlayers.add(newPlayerRanking)

                        // Atualiza o documento do jogo com a nova lista de jogadores
                        transaction.update(gameRef, "players", updatedPlayers)
                    }
                }
            }.addOnSuccessListener {
                Log.d(TAG, "User added to game successfully")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to game", e)
            }
        }
    }
}
