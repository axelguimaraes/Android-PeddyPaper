package pt.ipp.estg.peddypaper.data.local.entities

import androidx.room.*

@Entity(tableName = "options")
data class Option(
    @PrimaryKey val optionId: String,
    val text: String,
    val isCorrect: Boolean
)