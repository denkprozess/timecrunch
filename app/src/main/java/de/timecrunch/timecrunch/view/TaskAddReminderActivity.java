package de.timecrunch.timecrunch.view;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import de.timecrunch.timecrunch.R;

public class TaskAddReminderActivity extends AppCompatActivity {

    private TextView dateText, timeText, repeatText, repeatNoText, repeatTypeText;
    private Calendar calendar;
    private int year, month, hour, mMinute, day;
    private long mRepeatTime;
    private Switch repeatSwitch;
    private String mTitle;
    private String time;
    private String date;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add_reminder);

        Intent intent = getIntent();

        dateText = (TextView) findViewById(R.id.set_date);
        timeText = (TextView) findViewById(R.id.set_time);
        repeatText = (TextView) findViewById(R.id.set_repeat);
        repeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        repeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        repeatSwitch = (Switch) findViewById(R.id.repeat_switch);

        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);

        date = day + "." + month + "." + year;
        time = hour + ":" + mMinute;

        dateText.setText(date);
        timeText.setText(time);
        repeatNoText.setText(mRepeatNo);
        repeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        repeatTypeText.setText(mRepeatType);
    }

    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                mMinute = minute;
                if (minute < 10) {
                    time = hourOfDay + ":" + "0" + minute;
                } else {
                    time = hourOfDay + ":" + minute;
                }
                timeText.setText(time);
            }
        }, hour, mMinute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void setDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear ++;
                day = dayOfMonth;
                month = monthOfYear;
                year = year;
                date = dayOfMonth + "." + monthOfYear + "." + year;
                dateText.setText(date);
            }
        }, year, month, day);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            repeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            // repeatText.setText(R.string.repeat_off);
            repeatText.setText("Off");
        }
    }

    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                repeatTypeText.setText(mRepeatType);
                repeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
