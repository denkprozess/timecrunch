package de.timecrunch.timecrunch.view;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.utilities.AlarmReceiver;
import de.timecrunch.timecrunch.utilities.AlarmScheduler;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_finished:
                saveReminder();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            repeatNoText.setText(mRepeatNo);
                            repeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                        else {
                            mRepeatNo = input.getText().toString().trim();
                            repeatNoText.setText(mRepeatNo);
                            repeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void saveReminder(){
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);

        long selectedTimestamp =  calendar.getTimeInMillis();

        // Check repeat type
        if (mRepeatType.equals("Minute")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() +
                        60 * 1000, alarmIntent);

    }
}
