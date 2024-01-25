package pt.ipp.estg.peddypaper.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Data access object for [Question] entity.
 */
@Dao
interface QuestionDao {

    /**
     * Returns a list of all questions in the database.
     */
    @Query("SELECT * FROM questions")
    suspend fun getQuestions(): List<Question>

    /**
     * Returns a question with a specific id.
     *
     * @param id the id of the question.
     */
    @Query("SELECT * FROM questions WHERE questionId = :id")
    suspend fun getQuestion(id: String): Question

    /**
     * Returns a question with a like specific text.
     *
     * @param text the text of the question.
     */
    @Query("SELECT * FROM questions WHERE question LIKE :text")
    suspend fun getQuestionByText(text: String): Question

    /**
     * Inserts a question in the database.
     *
     * @param question the question to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    /**
     * Updates a question in the database.
     *
     * @param question the question to be updated.
     */
    @Update
    suspend fun updateQuestion(question: Question)

    /**
     * Deletes a question from the database.
     *
     * @param question the question to be deleted.
     */
    @Delete
    suspend fun deleteQuestion(question: Question)

    /**
     * Returns a list of all options with a specific question.
     *
     * @param questionId the id of the question.
     */
    /*
    @Transaction
    @Query("SELECT * FROM options WHERE optionId IN (SELECT optionId FROM option_question_cross_ref WHERE questionId = :questionId)")
    fun getQuestionWithOptions(questionId: String): LiveData<List<QuestionWithOptions>>
    */
}