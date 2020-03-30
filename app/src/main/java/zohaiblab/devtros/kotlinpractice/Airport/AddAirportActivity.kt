package zohaiblab.devtros.kotlinpractice.Airport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_airport.*
import zohaiblab.devtros.kotlinpractice.R
import zohaiblab.devtros.kotlinpractice.Util
import java.util.*


class AddAirportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_airport)



        button_save.setOnClickListener{

            val name = airport.text.toString()
            val createdAt = Util.formatDate()
            val updatedAt = Util.formatDate()
            val isSynced = 0

            val word = Airport(name, createdAt, updatedAt, isSynced)
            val gson = Gson()
            val obj = gson.toJson(word)

            val replyIntent = Intent()
            if(TextUtils.isEmpty(name)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }else{
                replyIntent.putExtra(EXTRA_REPLY,obj)
                setResult(Activity.RESULT_OK, replyIntent)
            }
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}
