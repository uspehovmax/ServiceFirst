package ru.uspehovmax.servicefirst

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

/**
 * Сервисы работают на главном потоке, если не предусмотреть иное, чтобы не блокировать Гл.поток
 *
 */
class MyService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        val start = intent?.getIntExtra(EXTRA_START, 0) ?:0
        coroutineScope.launch {
            for (i in start until start+10) {
                delay(1_000)
                log("Timer: $i - thread id: ${Thread.currentThread().id}")
            }
        }

/*      ПЕРЕЗАПУСК Services после их "уничтожения"
        Вместо стандартного вызова супер.метода можно сипользовать один из 3-х:
        // START_STICKY  // при перезапуске intent, кот.передаётся onStartCommand = null
        // START_NOT_STICKY // сервис не будет пересоздан
        // START_REDELIVER_INTENT // при перезапуске intent сохранится и начнется с 15
      return super.onStartCommand(intent, flags, startId)
*/
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        coroutineScope.cancel()

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(message: String) {
        Log.d("SERVICE_MESSAGE", "My Service: $message")
    }

    companion object {
        private const val EXTRA_START = "start"

        fun newIntent(context: Context, start: Int): Intent {
            return Intent(context, MyService::class.java).apply {
                putExtra(EXTRA_START, start)
            }
        }
    }
}