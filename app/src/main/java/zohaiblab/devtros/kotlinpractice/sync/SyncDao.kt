package zohaiblab.devtros.kotlinpractice.sync

import androidx.lifecycle.LiveData
import androidx.room.*
import zohaiblab.devtros.kotlinpractice.Airport.Airport

@Dao
interface SyncDao {

    @Query("SELECT * from sync_status")
    fun get(): List<SyncStatus>

    @Insert
    suspend fun insert(sync: SyncStatus)

    @Delete
    suspend fun delete(sync: SyncStatus)

    @Update
    suspend fun update(sync: SyncStatus)
}