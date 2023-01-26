package ru.uspehovmax.servicefirst

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

/**

 */
class MyJobIntentService : JobIntentService() {

    override fun onCreate() {
        super.onCreate()
        log("MyJobIntentService: onCreate")
    }

    //
    override fun onHandleWork(intent: Intent) {
        log("MyJobIntentService: onHandleIntent: ")
        val page = intent.getIntExtra(PAGE, 0) ?: 0
        for (i in 0 until 10) {
            Thread.sleep(1_000)
            log("Timer: $i, page: $page, thread id: ${Thread.currentThread().id}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("MyJobIntentService: onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_MESSAGE", "MyJobIntentService: $message")
    }

    companion object {
        private const val PAGE = "page"
        private const val JOB_ID = 222

        fun enqueue (context: Context, page: Int) {
            JobIntentService.enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                newIntent(context, page)
            )
        }

        fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }


}