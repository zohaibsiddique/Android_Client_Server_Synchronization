package zohaiblab.devtros.kotlinpractice

import java.text.SimpleDateFormat
import java.util.*

class Util {
    companion object{

        val token = "UhqGXlZunDru07jpDwsIgA4uZmZnSN4ZkNY90cnnHtDetcJCCsstHqkEEXyA";

        fun formatDate() : String{
            val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
            val dateFormat = SimpleDateFormat(DATE_FORMAT)
            val today = Calendar.getInstance().time
            return dateFormat.format(today)
        }
    }

}