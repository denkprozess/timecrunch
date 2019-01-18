package de.timecrunch.timecrunch.view;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskAlarm;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.utilities.AlarmReceiver;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskAddReminderFragment extends Fragment {

    private Switch repeatSwitch;
    private Button deleteButton;
    private TextView dateText, timeText, repeatText, repeatNoText, repeatTypeText;
    private Calendar calendar;

    private int year, month, hour, mMinute, day;
    private String time, date, mRepeat, mRepeatNo, mRepeatType;
    private long repeatTime;

    private int categoryId, taskId;
    private String categoryName, taskText;
    private LatLng taskLocation;
    private TaskViewModel taskViewModel;
    private TaskAlarm alarmData;

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    public TaskAddReminderFragment() {

    }

    public static TaskAddReminderFragment newInstance(String param1, String param2) {
        TaskAddReminderFragment fragment = new TaskAddReminderFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        categoryId = args.getInt("CATEGORY_ID");
        categoryName = args.getString("CATEGORY_NAME");
        taskId = args.getInt("TASK_ID");
        taskText = args.getString("TASK_TEXT");
        if (args.containsKey("TASK_LAT") && args.containsKey("TASK_LNG")) {
            double lat = args.getDouble("TASK_LAT");
            double lng = args.getDouble("TASK_LNG");
            taskLocation = new LatLng(lat,lng);
        }
        if (args.containsKey("ALARM_YEAR") && args.containsKey("ALARM_MONTH") &&
                args.containsKey("ALARM_HOUR") && args.containsKey("ALARM_MINUTE") &&
                args.containsKey("ALARM_DAY") && args.containsKey("ALARM_REPEAT") &&
                args.containsKey("ALARM_REPEATNO") && args.containsKey("ALARM_REPEATTYPE")) {
            this.alarmData = new TaskAlarm(args.getInt("ALARM_YEAR"), args.getInt("ALARM_MONTH"),
                    args.getInt("ALARM_HOUR"), args.getInt("ALARM_MINUTE"), args.getInt("ALARM_DAY"),
                    args.getBoolean("ALARM_REPEAT"), args.getInt("ALARM_REPEATNO"), args.getString("ALARM_REPEATTYPE"));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        new LoadTasksAsync2().execute();
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_edit_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_add_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout dateLayout = getActivity().findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        RelativeLayout timeLayout = getActivity().findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

        RelativeLayout repeatNoLayout = getActivity().findViewById(R.id.repeat_no_layout);
        repeatNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatNo(v);
            }
        });

        RelativeLayout repeatTypeLayout = getActivity().findViewById(R.id.RepeatType_layout);
        repeatTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRepeatType(v);
            }
        });

        deleteButton = getActivity().findViewById(R.id.delete_reminder_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeReminder();
            }
        });

        dateText = (TextView) getActivity().findViewById(R.id.set_date);
        timeText = (TextView) getActivity().findViewById(R.id.set_time);
        repeatText = (TextView) getActivity().findViewById(R.id.set_repeat);
        repeatNoText = (TextView) getActivity().findViewById(R.id.set_repeat_no);
        repeatTypeText = (TextView) getActivity().findViewById(R.id.set_repeat_type);
        repeatSwitch = (Switch) getActivity().findViewById(R.id.repeat_switch);

        repeatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchRepeat(v);
            }
        });

        calendar = Calendar.getInstance();
        if(alarmData != null) {
            mRepeat = Boolean.toString(alarmData.isRepeat());
            mRepeatNo = Integer.toString(alarmData.getRepeatNo());
            mRepeatType = alarmData.getRepeatType();

            hour = alarmData.getHour();
            mMinute = alarmData.getMinute();
            year = alarmData.getYear();
            month = alarmData.getMonth();
            day = alarmData.getDay();
        } else {
            initTexts();
        }
        if(mRepeat.equals("true")) {
            repeatSwitch.setChecked(true);
        } onSwitchRepeat(repeatSwitch);

        date = day + "." + (month + 1) + "." + year;
        if (mMinute < 10) {
            time = hour + ":" + "0" + mMinute;
        } else {
            time = hour + ":" + mMinute;
        }

        dateText.setText(date);
        timeText.setText(time);
        repeatNoText.setText(mRepeatNo);
        repeatTypeText.setText(mRepeatType);

    }

    private void initTexts() {
        mRepeat = "false";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Day";

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_finished:
                saveReminder();
                TaskModel modifiedTask = new TaskModel(taskId, taskText, taskLocation, alarmData);
                taskViewModel.changeTask(categoryId, modifiedTask);

                TaskOverviewFragment fragment = new TaskOverviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("CATEGORY_ID", categoryId);
                bundle.putString("CATEGORY_NAME", categoryName);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTime(View v){
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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
        mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(getContext());
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

        if(alarmData == null) {
            alarmData = new TaskAlarm(year, month, hour, mMinute, day, Boolean.parseBoolean(mRepeat), Integer.parseInt(mRepeatNo), mRepeatType);
        } else {
            alarmData.setYear(year);
            alarmData.setMonth(month);
            alarmData.setMinute(mMinute);
            alarmData.setHour(hour);
            alarmData.setDay(day);
            alarmData.setRepeat(Boolean.parseBoolean(mRepeat));
            alarmData.setRepeatNo(Integer.parseInt(mRepeatNo));
            alarmData.setRepeatType(mRepeatType);
        }

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, --month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);

        long selectedTimestamp =  calendar.getTimeInMillis();

        // Check repeat type
        if (mRepeatType.equals("Minute")) {
            repeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals("Hour")) {
            repeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            repeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            repeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            repeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getContext(), taskId, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                selectedTimestamp, repeatTime, alarmIntent);

    }

    private void removeReminder() {

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getContext(), taskId, intent, 0);

        alarmMgr.cancel(alarmIntent);

        Toast.makeText(getActivity(), "Alarm was removed!",
                Toast.LENGTH_SHORT).show();

        TaskModel modifiedTask = new TaskModel(taskId, taskText, taskLocation, null);
        taskViewModel.changeTask(categoryId, modifiedTask);

        TaskOverviewFragment fragment = new TaskOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("CATEGORY_ID", categoryId);
        bundle.putString("CATEGORY_NAME", categoryName);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();

    }

    // TODO: Remove this in the future and capsulate it
    private class LoadTasksAsync2 extends AsyncTask<Void, Void, Map<Category, List<TaskModel>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Map<Category, List<TaskModel>> doInBackground(Void... voids) {
            return taskViewModel.getTaskMap();
        }

        @Override
        protected void onPostExecute(Map<Category, List<TaskModel>> categoryListMap) {
            super.onPostExecute(categoryListMap);
        }
    }
}
