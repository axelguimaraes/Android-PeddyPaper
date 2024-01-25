package pt.ipp.estg.peddypaper.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Data access object for [Game] entity.
 */
@Dao
interface GameDao {

    /**
     * Returns a list of all games.
     */
    @Query("SELECT * FROM Games")
    suspend fun getGames(): List<Game>

    /**
     * Returns a game with a specific id.
     *
     * @param id the id of the game.
     */
    @Query("SELECT * FROM Games WHERE gameId = :id")
    suspend fun getGame(id: String): Game

    /**
     * Inserts a game in the database.
     *
     * @param game the game to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: Game)

    /**
     * Updates a game in the database.
     *
     * @param game the game to be updated.
     */
    @Update
    suspend fun updateGame(game: Game)

    /**
     * Deletes a game from the database.
     *
     * @param game the game to be deleted.
     */
    @Delete
    suspend fun deleteGame(game: Game)
}