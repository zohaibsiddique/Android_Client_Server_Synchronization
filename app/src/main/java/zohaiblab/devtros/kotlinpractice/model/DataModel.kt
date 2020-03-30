package zohaiblab.devtros.kotlinpractice.model

import com.google.gson.annotations.SerializedName


data class DataModel(


//        @SerializedName("albumId")
//        var albumId: kotlin.Int,
//        @SerializedName("id")
//        var id: kotlin.Int,
//        @SerializedName("title")
//        val title: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("flightNumber")
        val flight: String)