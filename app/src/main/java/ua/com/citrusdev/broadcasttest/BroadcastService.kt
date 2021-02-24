package ua.com.citrusdev.broadcasttest

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BroadcastService : Service() {

    inner class ServiceBinder : Binder() {
        val service: BroadcastService
            get() = this@BroadcastService
    }

    private var count: Long? = null
    private val mBinder: IBinder = ServiceBinder()
    private var handler : Handler? = null

    private val sendBroadcastRunnable: Runnable = Runnable {
        sendCustomBroadcast()
    }

    override fun onCreate() {
        Log.d("BroadcastService", "=========== LiveSocketService START =========")
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
    }

    override fun onDestroy() {
        Log.d("BroadcastService", "=========== LiveSocketService STOP =========")
        super.onDestroy()
        handler?.removeCallbacks(sendBroadcastRunnable)
        handler = null
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        count = intent?.getLongExtra("DELAY", 100L)
        sendCustomBroadcast()
        return START_NOT_STICKY
    }

    private fun sendCustomBroadcast() {
        val intent = Intent("newData")
        intent.putExtra("DELAY", count ?: 100L)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        handler?.postDelayed(sendBroadcastRunnable, count ?: 100L)
    }
}