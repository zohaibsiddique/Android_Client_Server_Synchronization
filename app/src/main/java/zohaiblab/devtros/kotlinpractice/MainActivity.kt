package zohaiblab.devtros.kotlinpractice

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.work.*
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlincodes.com.retrofitwithkotlin.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zohaiblab.devtros.kotlinpractice.Airport.*
import zohaiblab.devtros.kotlinpractice.sync.SyncDao
import zohaiblab.devtros.kotlinpractice.sync.SyncStatus
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: AirportViewModel
    val ADD_CODE = 1
    lateinit var progerssProgressDialog: ProgressDialog
    var list:List<Airport>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidThreeTen.init(this);


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter =
            AirportListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get a new or existing ViewModel from the ViewModelProvider.
        viewModel = ViewModelProvider(this).get(AirportViewModel::class.java)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        viewModel.allAirports.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.set(it) }
        })

        fab.setOnClickListener{
            val intent  = Intent(applicationContext, AddAirportActivity::class.java);
            startActivityForResult(intent,ADD_CODE)
        }

       getAirports()




//        sync()


    }

    private fun sync() {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)

    }
    private fun getAirports() {
        progerssProgressDialog=ProgressDialog(this)
        progerssProgressDialog.setTitle("Loading")
        progerssProgressDialog.setCancelable(false)
        progerssProgressDialog.show()

        val call: Call<List<Airport>> = ApiClient.getClient.getAirports()
        call.enqueue(object : Callback<List<Airport>> {

            override fun onResponse(call: Call<List<Airport>>?, response: Response<List<Airport>>?) {
                progerssProgressDialog.dismiss()

                val remoteList = response?.body()

                lifecycleScope.launch {

                    val database = DB.getDatabase(applicationContext)
                    val localList = database.airportDao().getByDate("2020-03-27 17:45:19")

                    // get ids
                    val idsLocalList = localList.map {
                        it.id
                    }
                    val idsRemoteList = remoteList?.map {
                        it.id
                    }

                    // get list of remote ids, which differ from local
                    val remoteListDiff = remoteList?.filter{
                        it.id !in idsLocalList
                    }
                    if(remoteListDiff?.isNotEmpty()!!){ // pull to local
                        Log.d("sfsdfsdf", "pull will be performed")

                        for(obj in remoteListDiff){
                            database.airportDao().insert(obj)
                        }
                    }

                    // get list of local ids, which differ from remote
                    val localListDiff = localList.filter{
                        !idsRemoteList?.contains(it.id)!!
                    }
                    if(localListDiff.isNotEmpty()){//push to remote
                        Log.d("sfsdfsdf", "push will be performed")

                        val call: Call<List<Airport>>? = localListDiff.let {
                            ApiClient.getClient.pushListAirport(
                                "push_list",it
                            )
                        }
                        call?.enqueue(object : Callback<List<Airport>> {

                            override fun onResponse(call: Call<List<Airport>>?, response: Response<List<Airport>>?) {

                                Log.d("sfsdfsdf", Gson().toJson(response?.body()))
//                                Toast.makeText(applicationContext, response?.body()?.name,Toast.LENGTH_LONG).show()
                            }

                            override fun onFailure(call: Call<List<Airport>>?, t: Throwable?) {
                                Toast.makeText(applicationContext, t?.message,Toast.LENGTH_LONG).show()
                            }
                        })
                    }

                    // get list of local and remote ids which are equals
                    val equalIdsList = localList.filter{
                        idsRemoteList?.contains(it.id)!!
                    }
                    if(equalIdsList.isNotEmpty()){
                        Log.d("sfsdfsdf", "update will be performed")

                        //case 1:
                            // push
                                // get and push records from local list which updated_at field greater than remote list


                        var filteredRemoteList : MutableList<Airport> = mutableListOf()
                        var filteredLocalList : MutableList<Airport> = mutableListOf()

                        localList.forEachIndexed{index,obj->
                            val remoteUpdatedAt = Timestamp.valueOf(obj.updated_at)
                            val localUpdatedAt = Timestamp.valueOf(remoteList.get(index).updated_at)

                            if(!remoteUpdatedAt.equals(localUpdatedAt)){
                                if (remoteUpdatedAt.before(localUpdatedAt)){
                                    filteredRemoteList.add(remoteList.get(index))
                                    Log.d("sfsdfsdf", remoteList.get(index).updated_at+" remote")
                                }else{
                                    filteredLocalList.add(obj)
                                    Log.d("sfsdfsdf", obj.updated_at+" local")
                                }
                            }
                        }

                        println(filteredRemoteList)
                        println(filteredLocalList)

                        if(filteredLocalList.isNotEmpty()){

                            Log.d("sfsdfsdf", "updates push to remote")

                            val call: Call<List<Airport>>? = ApiClient.getClient.updateAirportList("update_list",
                                filteredLocalList, Util.token)
                            call?.enqueue(object : Callback<List<Airport>> {

                                override fun onResponse(call: Call<List<Airport>>?, response: Response<List<Airport>>?) {

                                    Log.d("sfsdfsdf", Gson().toJson(response?.body()))
//                                Toast.makeText(applicationContext, response?.body()?.name,Toast.LENGTH_LONG).show()
                                }

                                override fun onFailure(call: Call<List<Airport>>?, t: Throwable?) {
                                    Toast.makeText(applicationContext, t?.message,Toast.LENGTH_LONG).show()
                                }
                            })
                        }

                        if(filteredRemoteList.isNotEmpty()){

                            Log.d("sfsdfsdf", "updates pull to local")

                            for (obj in filteredRemoteList){
                                database.airportDao().update(obj)
                            }
                        }


                        for(diff in filteredRemoteList){
                            Log.d("sfsdfsdf", diff.id.toString()+" filtered from remote")
                        }

                        for(diff in filteredLocalList){
                            Log.d("sfsdfsdf", diff.id.toString()+" filtered from local")
                        }


                        // case 2:
                            //pull
                                // get and push records from remote list which updated_at field greater than remote list

                        // case 3:
                            // no change

//                        val call: Call<List<Airport>> = ApiClient.getClient.getAirports()
//                        call.enqueue(object : Callback<List<Airport>> {
//
//                            override fun onResponse(call: Call<List<Airport>>?, response: Response<List<Airport>>?) {
//
//                            }
//
//                            override fun onFailure(call: Call<List<Airport>>?, t: Throwable?) {
//                            }
//                        })
                    }




//                    for(diff in remoteListDiff){
//                        Log.d("sfsdfsdf", diff.id.toString()+" from remote")
//                    }
//
//                    for(diff in localListDiff){
//                        Log.d("sfsdfsdf", diff.id.toString()+" from local")
//                    }
//
//                    for(diff in equalIdsList){
//                        Log.d("sfsdfsdf", diff.id.toString()+" from equal list")
//                    }
                }
            }

            override fun onFailure(call: Call<List<Airport>>?, t: Throwable?) {
                progerssProgressDialog.dismiss()
            }
        })

        progerssProgressDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == ADD_CODE && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(AddAirportActivity.EXTRA_REPLY)?.let {
                val gson = Gson()
                val obj = gson.fromJson(it, Airport::class.java)

                viewModel.insert(obj)

                val call: Call<Airport> = ApiClient.getClient.storeAirport(
                    obj.name, 0, "Bearer "+Util.token)
                call.enqueue(object : Callback<Airport> {

                    override fun onResponse(call: Call<Airport>?, response: Response<Airport>?) {
                        Toast.makeText(applicationContext, response?.body()?.name,Toast.LENGTH_LONG).show()
                    }

                    override fun onFailure(call: Call<Airport>?, t: Throwable?) {
                        Toast.makeText(applicationContext, t?.message,Toast.LENGTH_LONG).show()
                    }

                })

                Unit
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name empty! not saved!",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    inner class UploadWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

        @SuppressLint("SimpleDateFormat")
        override suspend fun doWork(): Result {



//            val database = DB.getDatabase(applicationContext)
//
////            val obj = SyncStatus(3, "2020-03-28")
////            database.syncDao().insert(obj)
//
//            val lastSync = database.syncDao().get()[1].lastSync
//
//            val airportList = database.airportDao().getByDate("2020-03-27 17:45:19")
//            for(airport in airportList){
//
//
//                Log.d("airportlist", airport.created_at.toString())
//
//                val call: Call<Airport> = ApiClient.getClient.pushAirport(
//                    airport.name, Util.formatDate(),1,"Bearer "+Util.token)
//                call.enqueue(object : Callback<Airport> {
//
//                    override fun onResponse(call: Call<Airport>?, response: Response<Airport>?) {
//                        Toast.makeText(applicationContext, response?.body()?.name,Toast.LENGTH_LONG).show()
//                    }
//
//                    override fun onFailure(call: Call<Airport>?, t: Throwable?) {
//                        Toast.makeText(applicationContext, t?.message,Toast.LENGTH_LONG).show()
//                    }
//
//                })
//
//            }

            return Result.success()
        }
    }
}
