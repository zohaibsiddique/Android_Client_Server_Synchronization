package zohaiblab.devtros.kotlinpractice.Airport

import androidx.lifecycle.LiveData
import androidx.room.*
import zohaiblab.devtros.kotlinpractice.Airport.Airport

@Dao
interface AirportDao {

    @Query("SELECT * from airport ORDER BY id ASC")
    fun get(): LiveData<List<Airport>>

    @Query("SELECT * from airport WHERE updated_at > :date")
    suspend fun getByDate(date : String): List<Airport>

    @Insert
    suspend fun insert(airport: Airport)

    @Delete
    suspend fun delete(airport: Airport)

    @Update
    suspend fun update(airport: Airport)
}