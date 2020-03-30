package zohaiblab.devtros.kotlinpractice.sync

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sync_status")
data class SyncStatus(

    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,

    @SerializedName("last_sync")
    @ColumnInfo(name = "last_sync")
    val lastSync : String

){

}