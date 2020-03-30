package zohaiblab.devtros.kotlinpractice.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import zohaiblab.devtros.kotlinpractice.Airport.Airport
import zohaiblab.devtros.kotlinpractice.model.DataModel


interface ApiInterface {

    @GET("flights")
    fun getPhotos(): Call<List<DataModel>>

    @GET("airports")
    fun getAirports(): Call<List<Airport>>

    @FormUrlEncoded
    @POST("airports")
    fun pushAirport(
            @Field("name") name:String,
            @Field("updated_at") updated_at:String,
            @Field("is_synced") is_synced:Int,
            @Header("Authorization") authHeader:String
    ): Call<Airport>

    @Headers("Content-Type: application/json")
    @POST("airports")
    fun pushListAirport(
        @Query("push_list") push: String,
        @Body body : List<Airport>
    ): Call<List<Airport>>

    @Headers("Content-Type: application/json")
    @PUT("airports/1")
    fun updateAirportList(
        @Query("update_list") push: String,
        @Body body : List<Airport>,
        @Header("Authorization") authHeader:String
    ): Call<List<Airport>>

    @FormUrlEncoded
    @POST("airports")
    fun storeAirport(
        @Field("name") name:String,
        @Field("is_synced") is_synced:Int,
        @Header("Authorization") authHeader:String
    ): Call<Airport>

    @FormUrlEncoded
    @PUT("airports/14")
    fun updateAirport(
            @Field("iataCode") iataCode:String,
            @Field("city") city:String,
            @Field("state") state:String,
            @Header("Authorization") authHeader:String
    ): Call<Airport>



    @DELETE("airports/11")
    fun deleteAirport(@Header("Authorization") authHeader:String
    ): Call<ResponseBody>

}