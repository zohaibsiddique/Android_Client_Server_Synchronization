package zohaiblab.devtros.kotlinpractice

import com.google.gson.annotations.SerializedName
import zohaiblab.devtros.kotlinpractice.Airport.Airport


class AirportList {
        @SerializedName("list")
        private val sponsors: List<Airport?>? = null

        fun getSponsors(): List<Airport?>? {
                return sponsors
        }

}