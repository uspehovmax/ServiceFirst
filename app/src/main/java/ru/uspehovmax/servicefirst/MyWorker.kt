package ru.uspehovmax.servicefirst

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*

class MyWorker(
    context: Context,
    private val workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    // будет выполняться в другом потоке
    override fun doWork(): Result {
        log("MyWorker: doWork: ")
        val page = workerParameters.inputData.getInt(PAGE, 0)
        for (i in 0 until 10) {
            Thread.sleep(1_000)
            log("Timer: $i, page: $page - thread id: ${Thread.currentThread().id}")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("SERVICE_MESSAGE", "MyWorker: $message")
    }

    companion object {
        private const val PAGE = "page"
        const val WORK_NAME = "work name"

        fun makeRequest(page: Int): OneTimeWorkRequest {
            // OneTimeWorkRequest -
            return OneTimeWorkRequestBuilder<MyWorker>().apply {
                setInputData(workDataOf(PAGE to page))
                setConstraints(makeConstraints())
            }.build()
        }

        private fun makeConstraints(): Constraints = Constraints.Builder()
            //.setRequiresCharging(true)
            .build()
    }

}
