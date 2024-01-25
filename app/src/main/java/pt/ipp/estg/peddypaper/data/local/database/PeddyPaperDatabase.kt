package pt.ipp.estg.peddypaper.data.local.database

import android.content.Context
import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*
import pt.ipp.estg.peddypaper.data.local.dao.*

/**
 * Database class for Room.
 */
@Database(
    entities = [
        Game::class,
        Option::class,
        Question::class,
        OptionQuestionCrossRef::class,
    ],
    version = 1
)
abstract class PeddyPaperDatabase : RoomDatabase() {

    /**
     * Returns the [GameDao] object.
     */
    abstract fun gameDao(): GameDao

    /**
     * Returns the [OptionDao] object.
     */
    abstract fun optionDao(): OptionDao

    /**
     * Returns the [QuestionDao] object.
     */
    abstract fun questionDao(): QuestionDao

    /**
     * Returns the [OptionQuestionCrossRefDao] object.
     */
    abstract fun optionQuestionCrossRefDao(): OptionQuestionCrossRefDao

    companion object {
        @Volatile
        private var INSTANCE: PeddyPaperDatabase? = null

        /**
         * Returns the [PeddyPaperDatabase] object.
         */
        fun getDatabase(context: Context): PeddyPaperDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PeddyPaperDatabase::class.java,
                    "peddypaper_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}