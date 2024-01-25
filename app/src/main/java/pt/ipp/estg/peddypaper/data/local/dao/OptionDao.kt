package pt.ipp.estg.peddypaper.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Data access object for [Option] entity.
 */
@Dao
interface OptionDao {

    /**
     * Returns a list of all options in the database.
     */
    @Query("SELECT * FROM options")
    suspend fun getOptions(): List<Option>

    /**
     * Returns a option with a specific id.
     *
     * @param id the id of the option.
     */
    @Query("SELECT * FROM options WHERE optionId = :id")
    suspend fun getOption(id: String): Option

    /**
     * Returns a option with a like specific text.
     *
     * @param text the text of the option.
     */
    @Query("SELECT * FROM options WHERE text LIKE :text")
    suspend fun getOptionByText(text: String): Option

    /**
     * Inserts a option in the database.
     *
     * @param option the option to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(option: Option)

    /**
     * Updates a option in the database.
     *
     * @param option the option to be updated.
     */
    @Update
    suspend fun updateOption(option: Option)

    /**
     * Deletes a option from the database.
     *
     * @param option the option to be deleted.
     */
    @Delete
    suspend fun deleteOption(option: Option)

    /**
     * Returns a list of all questions with a specific option.
     *
     * @param optionId the id of the option.
     */
    /*
    @Transaction
    @Query("SELECT * FROM questions WHERE questionId IN (SELECT questionId FROM option_question_cross_ref WHERE optionId = :optionId)")
    fun getOptionWithQuestions(optionId: String): LiveData<List<OptionWithQuestions>>
    */

}