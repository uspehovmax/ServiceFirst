package ru.uspehovmax.servicefirst

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

/**
 * Является устаревшим Deprecated
 * в отличие от service:
 * 1. благодаря onHandleIntent - не в гл.потоке
 * 2. не нужно останавливать - сам
 * 3. работает только один экземпляр сервиса, последовательно, если вызвали несколько раз
 * 4. не возвращает ничего
 * ! Нужно создавать NotificationChannel и Notification при API>=26
 */
class MyIntentService2 : IntentService(NAME) {

    override fun onCreate() {
        super.onCreate()
        log("MyIntentService2: onCreate")
        setIntentRedelivery(true)  // тоже самое что START_REDELIVER_INTENT, если false -START_STICKY
    }

    // Выполняется в другом потоке
    override fun onHandleIntent(intent: Intent?) {
        log("MyIntentService2: onHandleIntent: ")
        val page = intent?.getIntExtra(PAGE,0) ?: 0
        for (i in 0 until 10) {
            Thread.sleep(1_000)
            log("Timer: $i, page: $page, thread id: ${Thread.currentThread().id}")
        }
    }

/*    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        log("MyIntentService: onStartCommand")
//        coroutineScope.launch {
//            for (i in 0 until 50) {
//                delay(1_000)
//                log("Timer: $i")
//            }
//            stopSelf()
//        }
///*         Вместо стандартного вызова супер.метода можно сипользовать один из 3-х:
//        // START_STICKY  // при перезапуске intent, кот.передаётся onStartCommand = null
//        // START_NOT_STICKY // сервис не будет пересоздан
//        // START_REDELIVER_INTENT // при перезапуске intent сохранится и начнется с 15
//        // return super.onStartCommand(intent, flags, startId)*/
//        return START_STICKY
//    }
*/
    override fun onDestroy() {
        super.onDestroy()
        log("MyIntentService2: onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE_MESSAGE", "MyIntentService2: $message")
    }

    companion object {

        private const val NAME = "MyIntentService2"
        private const val PAGE = "page"
        fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }


}