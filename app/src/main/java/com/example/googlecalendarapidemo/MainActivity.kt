package com.example.googlecalendarapidemo

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCreateReminder.setOnClickListener {

            val date = "2021-03-21T06:51:51.542Z"
            val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'")
            val dateTimeLaps : Long = formatter.parse(date).time
            val concern = etEnterConcern.text.toString()
            val intent  = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
            intent.putExtra(CalendarContract.Events.TITLE, concern)
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateTimeLaps)
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateTimeLaps + 600000)
            intent.putExtra(CalendarContract.ACTION_EVENT_REMINDER, dateTimeLaps - 600000)
            startActivity(intent)
        }
    }
}