
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.Locale

//Todo Created by Yogesh

inline fun <reified T : Activity> Context.start(vararg params: Pair<String, Any>) {
    val intent = Intent(this, T::class.java).apply {
        params.forEach {
            when (val value = it.second) {
                is Int -> putExtra(it.first, value)
                is String -> putExtra(it.first, value)
                is Double -> putExtra(it.first, value)
                is Float -> putExtra(it.first, value)
                is Boolean -> putExtra(it.first, value)
                is Bundle -> putExtra(it.first, value)
                else -> throw IllegalArgumentException("Wrong param type!")
            }
            return@forEach
        }
    }
    startActivity(intent)
}

class OnDebouncedClickListener(private val delayInMilliSeconds: Long, val action: () -> Unit) :
    View.OnClickListener {
    var enable = true
    override fun onClick(view: View?) {
        if (enable) {
            enable = false
            view?.postDelayed({ enable = true }, delayInMilliSeconds)
            action()
        }
    }
}


fun View.setOnMyClickListener(delayInMilliSeconds: Long = 500, action: () -> Unit) {
    val onDebouncedClickListener = OnDebouncedClickListener(delayInMilliSeconds, action)
    setOnClickListener(onDebouncedClickListener)
}

//Todo For Any View Visible and Gone
fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun String.roundingTwoDigit(random:Double):String{
    val roundoff = String.format("%.2f", random)

    return roundoff
}
fun View.makeInvisible() {
    visibility = View.INVISIBLE
}
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

