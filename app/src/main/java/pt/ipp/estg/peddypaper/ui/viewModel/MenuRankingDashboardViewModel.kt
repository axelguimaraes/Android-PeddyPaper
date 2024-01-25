package pt.ipp.estg.peddypaper.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import pt.ipp.estg.peddypaper.data.remote.firebase.Player

class MenuRankingDashboardViewModel(aplication : Application) : AndroidViewModel(aplication) {

    private val db: FirebaseFirestore
    private val collectionName: String

    private val _players: MutableLiveData<List<Player>>

    init {
        db = Firebase.firestore
        collectionName = "PLAYERS"

        _players = MutableLiveData(listOf())

        loadPlayerRanking()
    }

    val players: LiveData<List<Player>> = _players

    private fun loadPlayerRanking() {
        db.collection(collectionName)
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                try {
                    val players = result.toObjects(Player::class.java)
                    _players.value = players
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }
}