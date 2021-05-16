package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val URL_UDACITY = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    private lateinit var detailIntent: Intent
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var enqueue: Long? = null
    private lateinit var downloadManager: DownloadManager

    companion object {
        const val FILE_EXTRA = "FILE EXTRA"
        const val STATUS_EXTRA = "STATUS EXTRA"
        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this, NotificationManager::class.java
        ) as NotificationManager
        detailIntent = Intent(applicationContext, DetailActivity::class.java)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        button_download.setOnClickListener {
            setupSelecDownloadUrl()
        }
    }

    private fun setupSelecDownloadUrl() {
        when (rg_download.checkedRadioButtonId) {
            R.id.rb_glide -> {
                download(URL_GLIDE)
                detailIntent.putExtra(
                    FILE_EXTRA,
                    getString(R.string.radio_title_glide)
                )
            }
            R.id.rb_udacity -> {
                download(URL_UDACITY)
                detailIntent.putExtra(
                    FILE_EXTRA,
                    getString(R.string.radio_title_udacity)
                )
            }

            R.id.rb_retrofit -> {
                download(URL_RETROFIT)
                detailIntent.putExtra(
                    FILE_EXTRA,
                    getString(R.string.radio_title_retrofit)
                )
            }
            else -> {
                Toast.makeText(this, getString(R.string.toast_please_select_file_download), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val extraDownloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            button_download.setState(ButtonState.Completed)
            handleDownloadFileStatus(context, extraDownloadId)
            sendNotification()
        }
    }

    private fun handleDownloadFileStatus(context: Context?, extraDownloadId: Long?) {
        val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()) {
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                    if (downloadID == extraDownloadId) {
                        Toast.makeText(context, "${getString(R.string.notification_description)} $uriString", Toast.LENGTH_LONG).show()
                    }
                    detailIntent.putExtra(
                        STATUS_EXTRA,
                        SUCCESS
                    )
                }
                DownloadManager.STATUS_FAILED -> {
                    Toast.makeText(applicationContext, getString(R.string.toast_download_failed), Toast.LENGTH_LONG).show()
                    detailIntent.putExtra(
                        STATUS_EXTRA,
                        FAIL
                    )
                }
            }
        }
    }

    private fun sendNotification() =
        notificationManager.sendNotification(
            getString(R.string.notification_description),
            this@MainActivity,
            detailIntent
        )

    private fun download(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadID = downloadManager.enqueue(request)
        button_download.setState(ButtonState.Loading)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
