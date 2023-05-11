
import android.app.Activity
import android.widget.Toast
import com.talkai.chatgpt.ai.app.R
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor(private val mContext: Activity) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!NetworkUtil.isInternetAvailable(mContext)) {
            mContext.runOnUiThread {
                Toast.makeText(mContext, "" + mContext.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
            }
            throw NetworkUtil.NoConnectivityException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

}

