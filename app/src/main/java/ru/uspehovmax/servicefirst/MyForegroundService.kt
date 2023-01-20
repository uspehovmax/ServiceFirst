package ru.uspehovmax.servicefirst

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

/**
 * Сервисы работают на главном потоке, если не предусмотреть иное, чтобы не блокировать
 * Гл.поток
 */
class MyForegroundService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("MyForegroundService: onCreate")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    /*
    * Начиная с 8 версии Андроид API>=27 начались проблемы с Сервисами
    * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("MyForegroundService: onStartCommand")
        coroutineScope.launch {
            for (i in 0 until 50) {
                delay(1_000)
                log("Timer: $i")
            }
            stopSelf()
        }
//          Вместо стандартного вызова супер.метода можно сипользовать один из 3-х:
        // START_STICKY  // при перезапуске intent, кот.передаётся onStartCommand = null
        // START_NOT_STICKY // сервис не будет пересоздан
        // START_REDELIVER_INTENT // при перезапуске intent сохранится и начнется с 15
//        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        log("MyForegroundService: onDestroy")
        coroutineScope.cancel()

    }


    private fun createNotificationChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Проверка версии Андроид на возможность создания канала notificationManager.createNotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(message: String) {
        Log.d("SERVICE_MESSAGE", "MyForegroundService: $message")
    }

    companion object {
        private const val CHANNEL_ID = "channel id"
        // название уведомлений - показываются пользователю - именование!
        private const val CHANNEL_NAME = "channel name"
        private const val NOTIFICATION_ID = 1


//        private const val EXTRA_START = "start"

        fun newIntent(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)/*.apply {
//                putExtra(EXTRA_START, start)
            }*/
        }
    }


}