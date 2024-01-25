package pt.ipp.estg.peddypaper.data.local.entities

import androidx.room.*
import com.google.android.gms.maps.model.LatLng

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["gameId"])]
)
data class Question(
    @PrimaryKey val questionId: String,
    val gameId: String,
    @Embedded val location: LatLng,
    val question: String,
    val score: Int,
    val isAnswered: Boolean,
) {
}