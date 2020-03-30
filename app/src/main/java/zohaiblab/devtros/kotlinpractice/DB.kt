package zohaiblab.devtros.kotlinpractice

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zohaiblab.devtros.kotlinpractice.Airport.Airport
import zohaiblab.devtros.kotlinpractice.Airport.AirportDao
import zohaiblab.devtros.kotlinpractice.sync.SyncDao
import zohaiblab.devtros.kotlinpractice.sync.SyncStatus

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Airport::class, SyncStatus::class), version = 8, exportSchema = false)
public abstract class DB : RoomDatabase() {

    abstract fun airportDao(): AirportDao
    abstract fun syncDao(): SyncDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: DB? = null

        fun getDatabase(
            context: Context
        ): DB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DB::class.java,
                    "restful_api"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class PopulateDatabase(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.airportDao())
                    }
                }
            }


            /**
             * Populate the database in a new coroutine.
             * If you want to start with more words, just add them.
             */
            suspend fun populateDatabase(wordDao: AirportDao) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.

//                var word = Airport(1, "RYK", "", "", "0")
//                wordDao.insert(word)
            }
        }

    }
}