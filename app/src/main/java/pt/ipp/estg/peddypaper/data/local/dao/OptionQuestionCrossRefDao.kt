package pt.ipp.estg.peddypaper.data.local.dao

import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Data access object for [OptionQuestionCrossRef] entity.
 */
@Dao
interface OptionQuestionCrossRefDao {

    /**
     * Returns a list of all option question cross references in the database.
     *
     * @return a list of all option question cross references in the database.
     */
    @Query("SELECT * FROM option_question_cross_ref")
    suspend fun getOptionQuestionCrossRefs(): List<OptionQuestionCrossRef>

    /**
     * Returns a option question cross reference with a specific option id.
     *
     * @param optionId the id of the option.
     * @return a option question cross reference with a specific option id.
     */
    @Query("SELECT * FROM option_question_cross_ref WHERE optionId = :optionId")
    suspend fun getOptionQuestionCrossRefByOptionId(optionId: String): OptionQuestionCrossRef

    /**
     * Returns a option question cross reference with a specific question id.
     *
     * @param questionId the id of the question.
     * @return a option question cross reference with a specific question id.
     */
    @Query("SELECT * FROM option_question_cross_ref WHERE questionId = :questionId")
    suspend fun getOptionQuestionCrossRefByQuestionId(questionId: String): OptionQuestionCrossRef

    /**
     * Inserts a option question cross reference in the database.
     *
     * @param optionQuestionCrossRef the option question cross reference to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionQuestionCrossRef(optionQuestionCrossRef: OptionQuestionCrossRef)

    /**
     * Deletes a option question cross reference from the database.
     *
     * @param optionQuestionCrossRef the option question cross reference to be deleted.
     */
    @Delete
    suspend fun deleteOptionQuestionCrossRef(optionQuestionCrossRef: OptionQuestionCrossRef)

    /**
     * Deletes a option question cross reference from the database.
     *
     * @param optionId the id of the option.
     * @param questionId the id of the question.
     */
    @Query("DELETE FROM option_question_cross_ref WHERE optionId = :optionId AND questionId = :questionId")
    suspend fun deleteOptionQuestionCrossRef(optionId: String, questionId: String)

    /**
     * Deletes a option question cross reference from the database.
     *
     * @param optionId the id of the option.
     */
    @Query("DELETE FROM option_question_cross_ref WHERE optionId = :optionId")
    suspend fun deleteOptionQuestionCrossRefByOptionId(optionId: String)

    /**
     * Deletes a option question cross reference from the database.
     *
     * @param questionId the id of the question.
     */
    @Query("DELETE FROM option_question_cross_ref WHERE questionId = :questionId")
    suspend fun deleteOptionQuestionCrossRefByQuestionId(questionId: String)

}