package ru.uspehovmax.servicefirst

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.*

/**

 */
class MyJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        log("MyJobService: onStartCommand")
        // достаем из очереди
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()
                while (workItem != null) {
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in 0 until 6 ) {
                        delay(1_000)
                        log("Timer: $i page:$page")
                    }
                    // все работы из очереди выполнены
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                jobFinished(params, false)
            }
        }

        return true // true - обозначает сервис работает, т.к. рабоббтает корутина
    }

    // вызывается когда система грохает сервис
    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("MyJobService: onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("MyJobService: onDestroy")
        coroutineScope.cancel()

    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyJobService: $message")
    }

    companion object {
        const val JOB_ID = 111
        private const val PAGE = "page"

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }

        }

    }
}