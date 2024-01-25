package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Question

class MenuCreateGameViewModel(aplication : Application) : AndroidViewModel(aplication) {

    // Firebase
    val db: FirebaseFirestore
    val collectionGame: String
    val collectionQuestion: String

    // Screen variables
    val _game: MutableLiveData<Game?>
    val _questions: MutableLiveData<List<Question>>

    init {
        db = FirebaseFirestore.getInstance()
        collectionGame = "GAMES"
        collectionQuestion = "QUESTIONS"

        _game = MutableLiveData()
        _questions = MutableLiveData()
    }

    // Screen variables
    val game: LiveData<Game?> = _game
    val questions: LiveData<List<Question>> = _questions

    fun loadGame(gameId: String) {
        Log.e(TAG, "GameId: $gameId")
            db.collection(collectionGame)
                .document(gameId)
                .get()
                .addOnSuccessListener { result ->
                    Log.w(TAG, "Game loaded: ${result.data}")
                    val game = result.toObject(Game::class.java)
                    _game.postValue(game)
                    loadQuestions()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        // loadQuestions()
    }

    private fun loadQuestions() {
        _game.value?.let {
            db.collection(collectionGame)
                .document(it.id)
                .get()
                .addOnSuccessListener { results ->
                    val questionsList = mutableListOf<Question>()

                    // store questions in questionsList
                    val questionsRefList = results.toObject(Game::class.java)?.questions
                    questionsRefList?.forEach { questionRef ->
                        db.collection(collectionQuestion)
                            .document(questionRef.id)
                            .get()
                            .addOnSuccessListener { result ->
                                val question = result.toObject(Question::class.java)
                                questionsList.add(question!!)
                                _questions.postValue(questionsList)
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error getting documents: ", exception)
                            }
                    }

                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        }
    }

    fun deleteQuestionsSelected(questionsSelected: List<Question>) {
        // 1. Remove questions from QUESTIONS collection
        _1_DeleteQuestionsFromCollection(questionsSelected)

        // 2. Remove questions from Game array of questions
        _2_RemoveQuestionsFromGame(questionsSelected)

        // 3. Update Game in GAMES collection
        _3_UpdateGameInCollection()
    }

    private fun _1_DeleteQuestionsFromCollection(questionsSelected: List<Question>) {
        Log.w(TAG, "Questions selected: $questionsSelected")

        questionsSelected.forEach { question ->
            db.collection(collectionQuestion)
                .document(question.id)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
        }
    }

    private fun _2_RemoveQuestionsFromGame(questionsSelected: List<Question>) {
        val selectedQuestionIds = questionsSelected.map { it.id }.toSet()
        val currentQuestions = _game.value?.questions ?: return

        val newQuestionRefs = currentQuestions.filterNot { questionRef ->
            selectedQuestionIds.contains(questionRef.id)
        }

        val updatedGame = _game.value?.copy(questions = newQuestionRefs)
        _game.value = updatedGame

        Log.e(TAG, "New questions: $newQuestionRefs")
        Log.w(TAG, "Updated Game in LiveData: ${_game.value}")
    }

    private fun _3_UpdateGameInCollection() {
        // Garante que estamos trabalhando com a versão mais recente de _game
        _game.value?.let { gameToUpdate ->
            db.collection(collectionGame)
                .document(gameToUpdate.id)
                .set(gameToUpdate)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing document", e)
                }
        }
    }

}