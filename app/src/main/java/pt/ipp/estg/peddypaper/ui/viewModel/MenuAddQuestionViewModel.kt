package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.data.remote.firebase.Option
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import pt.ipp.estg.peddypaper.data.remote.model.TriviaResponse
import pt.ipp.estg.peddypaper.data.repository.OpenTriviaRepository
import retrofit2.Response
import kotlin.random.Random
import kotlin.random.nextInt

class MenuAddQuestionViewModel(application: Application) : AndroidViewModel(application) {

    // Firebase
    val db: FirebaseFirestore
    val collectionName: String

    private var _question: MutableLiveData<Question>

    private val _game: MutableLiveData<Game?>

    init {

        db = Firebase.firestore
        collectionName = "QUESTIONS"

        _game = MutableLiveData()
        _question = MutableLiveData()
    }

    val question: LiveData<Question> = _question

    fun loadGame(gameId: String) {
        viewModelScope.launch {
            db.collection("GAMES")
                .document(gameId)
                .get()
                .addOnSuccessListener { result ->
                    val game = result.toObject(Game::class.java)
                    _game.postValue(game)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Error getting documents: ", it)
                }
        }
    }

    private fun storeQuestion(question: Question) {
        viewModelScope.launch {
            db.collection(collectionName)
                .add(question)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    db.collection(collectionName).document(documentReference.id).update("id", documentReference.id)
                    addQuestionToGame(documentReference)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    private fun addQuestionToGame(question: DocumentReference) {
        Log.w(TAG, "QuestionRef: $question")
        viewModelScope.launch {
            db.collection("GAMES")
                .document(_game.value!!.id)
                .update("questions", FieldValue.arrayUnion(question))
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }
    }

    fun createQuestion(text : String, score: Int, location : LatLng, options : List<String>, indexCorrectOption : Int){
        viewModelScope.launch {

            val optionsList = mutableListOf<Option>()
            for (option in options) {
                optionsList.add(Option(text = option, correct = options.indexOf(option) == (indexCorrectOption - 1)))
            }

            val question = Question(
                text = text,
                score = score,
                location = com.google.firebase.firestore.GeoPoint(location.latitude, location.longitude),
                options = optionsList
            )

            _question.postValue(question)
            storeQuestion(question)
        }
    }

    fun loadRandomQuestion() {
        viewModelScope.launch {
            try {
                val question = OpenTriviaRepository().getQuestion()

                question?.let {
                    _question.postValue(
                        Question(
                            text = it.question,
                            score = Random.nextInt(1..100) ,
                            options = listOf(
                                Option(text = it.correct_answer, correct = true),
                                Option(text = it.incorrect_answers[0], correct = false),
                                Option(text = it.incorrect_answers[1], correct = false),
                                Option(text = it.incorrect_answers[2], correct = false)
                            )
                        )
                    )
                } ?: run {
                    Log.w(TAG, "No question was loaded")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading question", e)
            }
        }
    }
}