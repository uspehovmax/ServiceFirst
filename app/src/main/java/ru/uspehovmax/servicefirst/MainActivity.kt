package ru.uspehovmax.servicefirst

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.uspehovmax.servicefirst.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var id = 0
    private var page = 0

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 15))
        }

        binding.foregroundService.setOnClickListener {
//            showNotification()
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
        }

        binding.intentService.setOnClickListener {
            startService(MyIntentService.newIntent(this))
        }

        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
//                .setExtras(MyJobService.newBundle(page++))
                //.setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                // добавлена в манифес, после перезапуска продолжится выпол-е сервиса
//                .setPersisted(true)       // для очереди не нужен - краш
                .build()

            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            }
        }


    }

    // Показать уведомления
/*    private fun showNotification() {
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
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        // id = 1 - показывает одно уведомление, только обновляет его
        // если сделать var id =0 и id ++ - уведомления будут сыпаться списком
        notificationManager.notify(id++, notification)
    }*/

    companion object {
        private const val CHANNEL_ID = "channel id"

        // название уведомлений - показываются пользователю - именование!
        private const val CHANNEL_NAME = "channel name"
    }
}