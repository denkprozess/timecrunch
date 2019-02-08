package de.timecrunch.timecrunch.fragments;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskAlarm;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.utilities.AlarmReceiver;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

/*
    Fragment is responsible for adding reminders to tasks
 */
public class TaskAddReminderFragment extends Fragment {

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private Switch repeatSwitch;
    private Button deleteButton;
    private TextView dateText, timeText, repeatText, repeatNoText, repeatTypeText;
    private Calendar calendar;
    private ProgressBar progressBar;
    private int year, month, hour, mMinute, day, mRepeatType, mRepeatNo;
    private String time, date;
    private long repeatTime;
    private boolean mRepeat;
    private String categoryId, taskId;
    private String categoryName, taskText;
    private LatLng taskLocation;
    private TaskViewModel taskViewModel;
    private TaskAlarm alarmData;
    private TaskModel task;
    private Resources resources;

    public TaskAddReminderFragment() {

    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        categoryId = args.getString("CATEGORY_ID");
        categoryName = args.getString("CATEGORY_NAME");
        taskId = args.getString("TASK_ID");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        resources = getResources();
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
        LiveData<Map<String, TaskModel>> taskMapLiveData = taskViewModel.getTaskMapLiveData();
        if (taskMapLiveData.hasObservers()) {
            taskMapLiveData.removeObservers(this);
        }
        taskMapLiveData.observe(this, new Observer<Map<String, TaskModel>>() {
            @Override
            public void onChanged(@Nullable final Map<String, TaskModel> taskMapLiveData) {
                task = taskViewModel.getTask(taskId);
                if (task != null) {
                    taskText = task.getText();
                    AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
                    parentActivity.getSupportActionBar().setTitle(taskText);
                    taskLocation = task.getLocation();
                    alarmData = task.getAlarm();
                    setUpDataView();
                }
            }
        });
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
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        progressBar = parentActivity.findViewById(R.id.task_reminder_progress_bar);
        taskViewModel.setUpLiveData(categoryId, progressBar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // save made changes before configuration changes
        if (alarmData == null) {
            alarmData = new TaskAlarm(year, month, hour, mMinute, day, mRepeat, mRepeatNo, getRepeatTypeStringFromResId(mRepeatType));
        } else {
            alarmData.setYear(year);
            alarmData.setMonth(month);
            alarmData.setMinute(mMinute);
            alarmData.setHour(hour);
            alarmData.setDay(day);
            alarmData.setRepeat(mRepeat);
            alarmData.setRepeatNo(mRepeatNo);
            alarmData.setRepeatType(getRepeatTypeStringFromResId(mRepeatType));
        }
        task.setAlarm(alarmData);
        taskViewModel.changeTaskToSurviveConfigChange(task);
    }

    private void setUpDataView() {
        Activity parentActivity = getActivity();
        RelativeLayout dateLayout = parentActivity.findViewById(R.id.date_layout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        RelativeLayout timeLayout = parentActivity.findViewById(R.id.time_layout);
        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

        RelativeLayout repeatNoLayout = parentActivity.findViewById(R.id.repeat_no_layout);
        repeatNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeatNo(v);
            }
        });

        RelativeLayout repeatTypeLayout = parentActivity.findViewById(R.id.RepeatType_layout);
        repeatTypeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRepeatType(v);
            }
        });

        deleteButton = parentActivity.findViewById(R.id.delete_reminder_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeReminder();
            }
        });

        dateText = (TextView) parentActivity.findViewById(R.id.set_date);
        timeText = (TextView) parentActivity.findViewById(R.id.set_time);
        repeatText = (TextView) parentActivity.findViewById(R.id.set_repeat);
        repeatNoText = (TextView) parentActivity.findViewById(R.id.set_repeat_no);
        repeatTypeText = (TextView) parentActivity.findViewById(R.id.set_repeat_type);
        repeatSwitch = (Switch) parentActivity.findViewById(R.id.repeat_switch);

        repeatSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchRepeat(v);
            }
        });

        calendar = Calendar.getInstance();
        if (alarmData != null) {
            mRepeat = alarmData.isRepeat();
            mRepeatNo = alarmData.getRepeatNo();
            mRepeatType = getResIdFromRepeatTypeString(alarmData.getRepeatType());

            hour = alarmData.getHour();
            mMinute = alarmData.getMinute();
            year = alarmData.getYear();
            month = alarmData.getMonth();
            day = alarmData.getDay();
        } else {
            initTexts();
        }
        if (mRepeat) {
            repeatSwitch.setChecked(true);
        }
        onSwitchRepeat(repeatSwitch);

        date = day + "." + (month) + "." + year;
        if (mMinute < 10) {
            time = hour + ":" + "0" + mMinute;
        } else {
            time = hour + ":" + mMinute;
        }

        dateText.setText(date);
        timeText.setText(time);
        repeatNoText.setText(Integer.toString(mRepeatNo));
        repeatTypeText.setText(resources.getQuantityString(mRepeatType, 1));

    }

    private void initTexts() {
        mRepeat = false;
        mRepeatNo = 1;
        mRepeatType = getResIdFromRepeatTypeString("Day");

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
                task.setAlarm(alarmData);
                taskViewModel.changeTask(task, progressBar);
                getActivity().finish();
                return true;

            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(getActivity())
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(getActivity(), upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setTime(View v) {
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
        mTimePicker.setTitle(R.string.select_time);
        mTimePicker.show();
    }

    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int year, int monthOfYear, int dayOfMonth) {
                day = dayOfMonth;
                month = monthOfYear + 1;
                year = year;
                date = day + "." + month + "." + year;
                dateText.setText(date);
            }
        }, year, month - 1, day);
        mDatePicker.setTitle(getString(R.string.select_date));
        mDatePicker.show();
    }

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = true;
            repeatText.setText(buildRepeatText(mRepeatNo, mRepeatType));
        } else {
            mRepeat = false;
            repeatText.setText(getString(R.string.off));
        }
    }

    public void selectRepeatType(View v) {
        final String[] items = new String[5];
        final int[] resIds = new int[5];

        resIds[0] = R.plurals.minute_plural;
        items[0] = resources.getQuantityString(resIds[0], 1);
        resIds[1] = R.plurals.hour_plural;
        items[1] = resources.getQuantityString(resIds[1], 1);
        resIds[2] = R.plurals.day_plural;
        items[2] = resources.getQuantityString(resIds[2], 1);
        resIds[3] = R.plurals.week_plural;
        items[3] = resources.getQuantityString(resIds[3], 1);
        resIds[4] = R.plurals.month_plural;
        items[4] = resources.getQuantityString(resIds[4], 1);

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.select_type));
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = resIds[item];
                repeatTypeText.setText(resources.getQuantityString(mRepeatType, 1));
                repeatText.setText(buildRepeatText(mRepeatNo, mRepeatType));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getString(R.string.prompt_enter_number));

        // Create EditText box to input repeat number
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = 1;
                            repeatNoText.setText(Integer.toString(mRepeatNo));
                            repeatText.setText(buildRepeatText(mRepeatNo, mRepeatType));
                        } else {
                            mRepeatNo = Integer.parseInt(input.getText().toString().trim());
                            repeatNoText.setText(Integer.toString(mRepeatNo));
                            repeatText.setText(buildRepeatText(mRepeatNo, mRepeatType));
                        }
                    }
                });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    public void saveReminder() {
        String repeatTypeString = getRepeatTypeStringFromResId(mRepeatType);
        if (alarmData == null) {
            alarmData = new TaskAlarm(year, month, hour, mMinute, day, mRepeat, mRepeatNo, repeatTypeString);
        } else {
            alarmData.setYear(year);
            alarmData.setMonth(month);
            alarmData.setMinute(mMinute);
            alarmData.setHour(hour);
            alarmData.setDay(day);
            alarmData.setRepeat(mRepeat);
            alarmData.setRepeatNo(mRepeatNo);
            alarmData.setRepeatType(repeatTypeString);
        }

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, month);//--month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);

        long selectedTimestamp = calendar.getTimeInMillis();

        // Check repeat type
        if (mRepeatType == R.plurals.minute_plural) {
            repeatTime = mRepeatNo * milMinute;
        } else if (mRepeatType == R.plurals.hour_plural) {
            repeatTime = mRepeatNo * milHour;
        } else if (mRepeatType == R.plurals.day_plural) {
            repeatTime = mRepeatNo * milDay;
        } else if (mRepeatType == R.plurals.week_plural) {
            repeatTime = mRepeatNo * milWeek;
        } else if (mRepeatType == R.plurals.month_plural) {
            repeatTime = mRepeatNo * milMonth;
        }

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CATEGORY_ID", categoryId);
        intent.putExtra("CATEGORY_NAME", categoryName);
        intent.putExtra("TASK_ID", taskId);
        alarmIntent = PendingIntent.getBroadcast(getContext(), taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                selectedTimestamp, repeatTime, alarmIntent);

    }

    private void removeReminder() {

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent = PendingIntent.getBroadcast(getContext(), taskId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.cancel(alarmIntent);

        Toast.makeText(getActivity(), getString(R.string.reminder_removed),
                Toast.LENGTH_SHORT).show();

        task.setAlarm(null);
        taskViewModel.changeTask(task, progressBar);
        getActivity().finish();

    }

    private String buildRepeatText(int repeatNo, int repeatTypeResId) {
        String repeatText = getString(R.string.every);
        repeatText += " " + repeatNo + " ";
        repeatText += resources.getQuantityString(repeatTypeResId, repeatNo);
        return repeatText;
    }

    private int getResIdFromRepeatTypeString(String repeatType) {
        switch (repeatType) {
            case "Minute":
                return R.plurals.minute_plural;
            case "Hour":
                return R.plurals.hour_plural;
            case "Day":
                return R.plurals.day_plural;
            case "Week":
                return R.plurals.week_plural;
            case "Month":
                return R.plurals.month_plural;
            default:
                return 0;
        }
    }

    private String getRepeatTypeStringFromResId(int resId) {
        switch (resId) {
            case R.plurals.minute_plural:
                return "Minute";
            case R.plurals.hour_plural:
                return "Hour";
            case R.plurals.day_plural:
                return "Day";
            case R.plurals.week_plural:
                return "Week";
            case R.plurals.month_plural:
                return "Month";
            default:
                return null;
        }
    }
}
