package com.udacity

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.green
import android.graphics.Color.red
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.FILE_EXTRA
import com.udacity.MainActivity.Companion.STATUS_EXTRA
import com.udacity.MainActivity.Companion.SUCCESS
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.button_ok
import kotlinx.android.synthetic.main.content_detail.tv_file_name
import kotlinx.android.synthetic.main.content_detail.tv_status

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        setupDownloadFileTexts()

        button_ok.setOnClickListener {
            finish()
        }
    }

    private fun setupDownloadFileTexts(){
        val fileName = intent.getStringExtra(FILE_EXTRA)
        val status = intent.getStringExtra(STATUS_EXTRA)

        tv_file_name.text = fileName.toString()
        tv_status.text = status.toString()

        if (status == SUCCESS) {
            tv_status.setTextColor(Color.GREEN)
        } else {
            tv_status.setTextColor(Color.RED)
        }
    }

}
