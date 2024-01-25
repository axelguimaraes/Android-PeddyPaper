package pt.ipp.estg.peddypaper.data.local.entities

import androidx.room.*

@Entity(
    tableName = "games",
)
data class Game(
    @PrimaryKey var gameId: String,
    var ownerId: String,
    var isActive: Boolean,
)