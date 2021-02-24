package ua.com.citrusdev.broadcasttest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ua.com.citrusdev.broadcasttest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mScannerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val delayInfo: Long = intent.getLongExtra("DELAY", 200L)
            processDevice(delayInfo)
        }
    }

    private var counter: Long = 0
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.button.setOnClickListener {
            if (binding.editTextNumberDecimal.text.isNotBlank()) {
                it.visibility = View.GONE
                val delay = binding.editTextNumberDecimal.text.toString().toLong()
                startBroadcastService(delay)
                binding.btnStop.visibility = View.VISIBLE
            }
        }
        binding.btnStop.setOnClickListener {
            it.visibility = View.GONE
            stopBroadcastService()
            binding.button.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mScannerReceiver,
            IntentFilter("newData")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mScannerReceiver)
    }

    private fun startBroadcastService(delayMs: Long) {
        val intent = Intent(this, BroadcastService::class.java)
        intent.putExtra("DELAY", delayMs)
        try {
            startService(intent)
            /*if (bindService(intent, mConnection, BIND_AUTO_CREATE)) mShouldUnbind = true*/
        } catch (e: Exception) {
            Log.e("MainActivity", null, e)
        }
    }


    private fun stopBroadcastService() {
        /*if (mShouldUnbind) {
            unbindService(mConnection)
            mShouldUnbind = false
        }*/
        stopService(Intent(this, BroadcastService::class.java))
        counter = 0L
    }

    private fun processDevice(delay: Long) {
        val d = delay + counter
        val sb = StringBuilder().append(d.toString()).append("\n")
        sb.append(binding.tvLog.text)
        binding.tvLog.text = sb.toString()
        counter++
    }
}