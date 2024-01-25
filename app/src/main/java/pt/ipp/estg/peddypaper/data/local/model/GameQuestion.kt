package pt.ipp.estg.peddypaper.data.local.model

import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Represents a game with its questions.
 *
 * @property game The game.
 * @property questions The questions of the game.
 */
data class GameWithQuestions(
    @Embedded val game: Game,
    @Relation(
        parentColumn = "questionId",
        entityColumn = "gameId"
    )
    val questions: List<Question>
)
