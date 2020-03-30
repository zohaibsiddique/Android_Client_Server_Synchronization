package zohaiblab.devtros.kotlinpractice.Airport
import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "airport")
data class Airport(

    @SerializedName("name")
    val name : String,

    @SerializedName("created_at")
    val created_at : String?,

    @SerializedName("updated_at")
    val updated_at : String?,

    @SerializedName("is_synced")
    val is_synced : Int,

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0

){
//    override fun equals(other: Any?): Boolean {
//        Log.d("sfsdfsdf", "equal call")
//       return (other as Airport).id == this.id
//    }
}