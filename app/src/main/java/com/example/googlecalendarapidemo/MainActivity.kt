package com.example.googlecalendarapidemo

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Reminders
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnCreateReminder.setOnClickListener {
            openPermissionDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            populateCalendarData()
        } else {
            Toast.makeText(this, "Need to provide permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun openPermissionDialog() {
        if (!isFinishing && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR), 101)
        } else {
            populateCalendarData()
        }
    }

    private fun populateCalendarData() {
        val date = "2021-03-21T06:51:51.542Z"
        val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'")
        val dateTimeLaps : Long = formatter.parse(date).time
        val concern = etEnterConcern.text.toString()
        val calendarEventID = addEventToCalendar(this, concern, concern, "Bangalore",
            0, dateTimeLaps, true, isMailService = true)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(
            "content://com.android.calendar/events/" + java.lang.String.valueOf(
                calendarEventID
            )
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun addEventToCalendar(
        context: Context, title: String?,
        addInfo: String?, place: String?, status: Int, startDate: Long,
        isRemind: Boolean, isMailService: Boolean
    ): Long {
        val cr: ContentResolver = context.getContentResolver()
        val eventUriStr = "content://com.android.calendar/events"
        val event = ContentValues()
        // id, We need to choose from our mobile foEr primary its 1
        event.put(Events.CALENDAR_ID, 1)
        event.put(Events.TITLE, title)
        event.put(Events.DESCRIPTION, addInfo)
        event.put(Events.EVENT_LOCATION, place)
        event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID())
        // For next 1hr
        val endDate = startDate + 1000 * 60 * 60
        event.put(Events.DTSTART, startDate)
        event.put(Events.DTEND, endDate)
        //If it is bithday alarm or such kind (which should remind me for whole day) 0 for false, 1 for true
        // values.put("allDay", 1);
        //status =  CalendarContract.Events.AVAILABILITY_TENTATIVE;
        event.put(Events.STATUS, status) //CalendarContract.Events.AVAILABILITY_BUSY
        event.put(Events.HAS_ALARM, 1)
        val eventUri = cr.insert(Uri.parse(eventUriStr), event)
        val eventID = eventUri!!.lastPathSegment!!.toLong()
        if (isRemind) {
            val method = 1
            val minutes = 60
            addAlarms(cr, eventID, minutes, method)
            //        String reminderUriString = "content://com.android.calendar/reminders";
            //        ContentValues reminderValues = new ContentValues();
            //        reminderValues.put(Reminders.EVENT_ID, eventID);
            //        // Default value of the system. Minutes is a integer
            //        reminderValues.put(Reminders.MINUTES, 15);
            //        // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
            //        reminderValues.put(Reminders.METHOD,Reminders.METHOD_ALARM);
            //
            //        cr.insert(Uri.parse(reminderUriString), reminderValues);
        }
        if (isMailService) {
            var attendeeName = "Rick"
            var attendeeEmail = "rbarnes23@gmail.com"
            var attendeeRelationship = 2
            var attendeeType = 2
            var attendeeStatus = 1
            addAttendees(
                cr, eventID, attendeeName, attendeeEmail,
                attendeeRelationship, attendeeType, attendeeStatus
            )
            attendeeName = "Marion"
            attendeeEmail = "marion.a.barnes@gmail.com"
            attendeeRelationship = 4
            attendeeType = 2
            attendeeStatus = 3
            addAttendees(
                cr, eventID, attendeeName, attendeeEmail,
                attendeeRelationship, attendeeType, attendeeStatus
            )
        }
        return eventID
    }


    private fun addAlarms(
        cr: ContentResolver, eventId: Long,
        minutes: Int, method: Int
    ) {
        val reminderUriString = "content://com.android.calendar/reminders"
        val reminderValues = ContentValues()
        reminderValues.put(Reminders.EVENT_ID, eventId)
        // Default value of the system. Minutes is a integer
        reminderValues.put(Reminders.MINUTES, minutes)
        // Alert Methods: Default(0), Alert(1), Email(2), SMS(3)
        reminderValues.put(Reminders.METHOD, method)
        cr.insert(Uri.parse(reminderUriString), reminderValues)
    }
    private fun addAttendees(
        cr: ContentResolver, eventId: Long,
        attendeeName: String, attendeeEmail: String,
        attendeeRelationship: Int, attendeeType: Int, attendeeStatus: Int
    ) {
        val attendeuesesUriString = "content://com.android.calendar/attendees"

        /********* To add multiple attendees need to insert ContentValues multiple times  */
        val attendeesValues = ContentValues()
        attendeesValues.put(CalendarContract.Attendees.EVENT_ID, eventId)
        // Attendees name
        attendeesValues.put(
            CalendarContract.Attendees.ATTENDEE_NAME,
            attendeeName
        )
        // Attendee email
        attendeesValues.put(
            CalendarContract.Attendees.ATTENDEE_EMAIL,
            attendeeEmail
        )
        // ship_Attendee(1), Relationship_None(0), Organizer(2), Performer(3), Speaker(4)
        attendeesValues.put(
            CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
            attendeeRelationship
        )
        // None(0), Optional(1), Required(2), Resource(3)
        attendeesValues.put(
            CalendarContract.Attendees.ATTENDEE_TYPE,
            attendeeType
        )
        // None(0), Accepted(1), Decline(2), Invited(3), Tentative(4)
        attendeesValues.put(
            CalendarContract.Attendees.ATTENDEE_STATUS,
            attendeeStatus
        )
        cr.insert(
            Uri.parse(attendeuesesUriString),
            attendeesValues
        ) //Uri attendeuesesUri =
    }
}