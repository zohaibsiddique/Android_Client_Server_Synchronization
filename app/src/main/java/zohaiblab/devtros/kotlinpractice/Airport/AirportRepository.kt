package zohaiblab.devtros.kotlinpractice.Airport

import androidx.lifecycle.LiveData
import zohaiblab.devtros.kotlinpractice.Airport.Airport
import zohaiblab.devtros.kotlinpractice.Airport.AirportDao

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AirportRepository(private val airportDao: AirportDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allAirports: LiveData<List<Airport>> = airportDao.get()

    suspend fun insert(airport: Airport) {
        airportDao.insert(airport)
    }
}