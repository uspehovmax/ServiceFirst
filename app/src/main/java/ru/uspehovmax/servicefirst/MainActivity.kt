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
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
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

        /*
        Создание и запуск сервисов происходит через Intent
        В классе сервиса
        companion object {...
        fun newIntent(context: Context): Intent {
            return Intent(context, MyIntentService::class.java)
        }
         */
        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 15))
        }

        binding.intentService.setOnClickListener {
            startService(MyIntentService.newIntent(this))
        }

        /*
        * Начиная с 8 версии Андроид API>=26 начались проблемы с Сервисами
        * 1. Нужно уведомлять пользователя о запущенных сервисах
        * 2. Нужно создавать каналы
        *
        // foregroundService отличается от Service тем, что API>=26 нужно уведомлять notify
        // юзера внутри класса сервиса о том,что сервис работает и использовать канал для notification
        // запуск через ContextCompat - невозможно смахнуть */

        binding.foregroundService.setOnClickListener {
//            showNotification()
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this))
        }


        binding.jobScheduler.setOnClickListener {
            // 1 указываем какой сервис нужен
            val componentName = ComponentName(this, MyJobService::class.java)

            // 2 установка ограничений,
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
/*                .setExtras(MyJobService.newBundle(page++))
                //.setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                // добавлена в манифес, после перезапуска продолжится выпол-е сервиса
                    // после выключения устройства и включения - сервис перезапуститься - добавление
                    // в манифесте android.permission.RECEIVE_BOOT_COMPLETED
//                .setPersisted(true)       // для очереди не нужен - краш */
                .build()

            // 3 запуск на выполнение
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                // очередь сервисов. выполняются последовательно друг за другом // добавить код для schedule
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            } else {
                startService(MyIntentService2.newIntent(this, page++))
            }
        }

        // Google сделал jobIntentService - совместив проверки на API>=26 и возможность
        // добавления в очередь выполнения работ сервиса
        binding.jobIntentService.setOnClickListener {
            MyJobIntentService.enqueue(this, page++)
        }

        // WorkManager - класс из JetPack
        binding.workManager.setOnClickListener {
            // создаем экз. ч/з getInstance(applicationContext) контекст приложения, чтобы не было
            // утечек памяти
            val workManager = WorkManager.getInstance(applicationContext)
            // работает только один воркер, но можно запустить workManager.enqueue
            // ExistingWorkPolicy.APPEND - действие при
            // запуск стат.метода из MyWorker.makeRequest
            workManager.enqueueUniqueWork(
                MyWorker.WORK_NAME,
                ExistingWorkPolicy.APPEND,
                MyWorker.makeRequest(page++)
            )
        }




    }

    // Показать уведомления - old
/*      * 1. Нужно уведомлять пользователя о запущенных сервисах
        * 2. Нужно создавать каналы
    * */
/*    private fun showNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 2. КАНАЛ. Проверка версии Андроид на возможность создания канала notificationManager.createNotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        // 1. notification пользователя
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()
        // id = 1 - показывает одно уведомление, только обновляет его
        // если сделать var id =0 и id ++ - уведомления будут сыпаться списком
        notificationManager.notify(id++, notification)
    }

    companion object {
        private const val CHANNEL_ID = "channel id"

        // название уведомлений - показываются пользователю - именование!
        private const val CHANNEL_NAME = "channel name"
    }*/
}