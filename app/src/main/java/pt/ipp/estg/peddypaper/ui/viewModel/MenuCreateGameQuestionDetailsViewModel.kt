package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import pt.ipp.estg.peddypaper.data.remote.firebase.Question

class MenuCreateGameQuestionDetailsViewModel(aplication : Application) : AndroidViewModel(aplication) {

    // Firebase
    val db: FirebaseFirestore
    val collectionName: String

    // Screen variables
    val _question: MutableLiveData<Question?>

    init {
        db = FirebaseFirestore.getInstance()
        collectionName = "QUESTIONS"

        _question = MutableLiveData()
    }

    val question: LiveData<Question?> = _question

    fun setQuestionId(questionId: String) {
        Log.w(TAG, "Question ID: $questionId")
        loadQuestion(questionId)
    }

    private fun loadQuestion(questionId: String) {
        viewModelScope.launch {
            db.collection(collectionName)
                .document(questionId)
                .get()
                .addOnSuccessListener { result ->
                    try {
                        val question = result.toObject(Question::class.java)
                        Log.d(TAG, "Question: $question")
                        if(question != null) {
                            _question.value = question
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error getting data")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting data", exception)
                }
        }
    }


}