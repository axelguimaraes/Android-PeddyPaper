package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Option
import pt.ipp.estg.peddypaper.data.remote.firebase.Question

class GameQuestionViewModel(application: Application): AndroidViewModel(application) {

    private val db: FirebaseFirestore
    private val collectionName: String

    private val _auth: FirebaseAuth

    var _question: MutableLiveData<Question?>
    var _game: MutableLiveData<Game?>

    var gameId: String
    var questionId: String

    private var _selectedOption: MutableLiveData<Option>
    private var _correctOption: MutableLiveData<Boolean>

    private val _isAnswered: MutableLiveData<Boolean>

    init {
        db = Firebase.firestore
        collectionName = "QUESTIONS"
        _question = MutableLiveData()
        _game = MutableLiveData()

        _auth = Firebase.auth

        questionId = ""
        gameId = ""

        _selectedOption = MutableLiveData()
        _correctOption = MutableLiveData()

        _isAnswered = MutableLiveData(false)
    }

    val selectedOption: LiveData<Option?> = _selectedOption
    val correctAnswer: LiveData<Boolean?> = _correctOption
    val isAnswered: LiveData<Boolean> = _isAnswered

    /**
     * Get the question from the database
     */
    fun getQuestion() {
        viewModelScope.launch {
            db.collection(collectionName)
                .document(questionId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        try {
                            val question = document.toObject(Question::class.java)
                            _question.value = question
                        } catch (e: Exception) {
                            Log.w(TAG, "Erro na desserialização: ", e)
                        }
                    } else {
                        // Trate o caso em que o documento não é encontrado
                        Log.w(TAG, "Document not found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    /**
     * Getter for the selected option
     */
    fun setSelectedOption(option: Option) {
        _selectedOption.value = option
    }

    /**
     * Check if the selected option is correct
     */
    private fun checkAnswer() : Boolean {
        val selectedOption = _selectedOption.value ?: return false
        val correctOption = _selectedOption.value?.correct ?: return false
        return selectedOption.correct == correctOption
    }

    /**
     * Submit the answer
     */
    fun submitAnswer() {
        _isAnswered.postValue(true)

        if(checkAnswer()) {
            val playerId = _auth.currentUser?.uid
            val questionScore = _question.value?.score ?: 0

            // 1. Atualiza o score do player no array de players do game
            _game.value?.players?.find { it.player?.id == playerId }?.let { player ->
                player.score += questionScore

                // 2. Atualiza o game na coleção GAMES
                updateGameInDatabase()

                // 3. Atualiza o score do player na coleção PLAYERS
                if (playerId != null) {
                    updatePlayerScoreInDatabase(playerId, player.score)
                }

                // 4. update question as answered
                updateQuestionAsAnswered()
            }
        }
    }

    private fun updateGameInDatabase() {
        val gameToUpdate = _game.value ?: return
        db.collection("GAMES")
            .document(gameToUpdate.id)
            .set(gameToUpdate)
            .addOnSuccessListener {
                Log.d(TAG, "Game successfully updated in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating game in Firestore", e)
            }
    }

    private fun updatePlayerScoreInDatabase(playerId: String, newScore: Int) {
        db.collection("PLAYERS")
            .document(playerId)
            .update("score", newScore)
            .addOnSuccessListener {
                Log.d(TAG, "Player score successfully updated in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating player score in Firestore", e)
            }
    }

    private fun updateQuestionAsAnswered() {
        val questionToUpdate = _question.value ?: return
        questionToUpdate.awnsred = false
        db.collection("QUESTIONS")
            .document(questionToUpdate.id)
            .set(questionToUpdate)
            .addOnSuccessListener {
                Log.d(TAG, "Question successfully updated in Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating question in Firestore", e)
            }
    }

    fun loadGame() {
        viewModelScope.launch {
            db.collection("GAMES")
                .document(gameId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        try {
                            val game = document.toObject(Game::class.java)
                            _game.value = game
                        } catch (e: Exception) {
                            Log.w(TAG, "Erro na desserialização: ", e)
                        }
                    } else {
                        // Trate o caso em que o documento não é encontrado
                        Log.w(TAG, "Document not found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

}
