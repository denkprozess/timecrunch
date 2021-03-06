package de.timecrunch.timecrunch.fragments;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.activities.TaskAddReminderActivity;
import de.timecrunch.timecrunch.model.TaskAlarm;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskEditFragment extends Fragment implements OnMapReadyCallback {

    private final int PERMISSIONS_REQUEST_CODE = 1337;
    private final int DEFAULT_ZOOM = 16;

    private boolean coarseLocationGranted;
    private boolean fineLocationGranted;

    private LatLng defaultLocation = new LatLng(-34, 151);
    private String categoryId;
    private String categoryName;
    private String taskId;
    private String taskText;
    private LatLng taskLocation;
    private RelativeLayout addReminderLayout;
    private TextView reminderText;
    private TaskAlarm alarmData;
    private TaskModel task;

    private TaskViewModel taskViewModel;

    private EditText taskEditText;
    private CheckBox repeatCheckBox;
    private MapView mapView;
    private GoogleMap map;
    private ProgressBar progressBar;

    public TaskEditFragment() {
        // Required empty public constructor
    }

    public static TaskEditFragment newInstance(String param1, String param2) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        categoryId = args.getString("CATEGORY_ID");
        categoryName = args.getString("CATEGORY_NAME");
        taskId = args.getString("TASK_ID");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_finished:
                taskText = taskEditText.getText().toString();
                task.setText(taskText);
                task.setLocation(taskLocation);
                task.setRepeating(repeatCheckBox.isChecked());
                taskViewModel.changeTask(task, progressBar);
                getActivity().finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.getSupportActionBar().setTitle(categoryName);
        progressBar = parentActivity.findViewById(R.id.task_edit_progress_bar);
        initMap(savedInstanceState);
        taskViewModel.setUpLiveData(categoryId, progressBar);
    }

    private void setUpDataView() {
        Activity parentActivity = getActivity();
        taskEditText = parentActivity.findViewById(R.id.edittext_task);
        taskEditText.setText(taskText);
        repeatCheckBox = parentActivity.findViewById(R.id.repeating_task_check);
        repeatCheckBox.setChecked(task.getIsRepeating());
        reminderText = getView().findViewById(R.id.set_task_edit_reminder_text);
        String dateText = getString(R.string.no_reminder_set);
        String time = "";
        if (alarmData != null) {
            if (alarmData.getMinute() < 10) {
                time = alarmData.getHour() + ":" + "0" + alarmData.getMinute();
            } else {
                time = alarmData.getHour() + ":" + alarmData.getMinute();
            }
            dateText = alarmData.getDay() + "." + (alarmData.getMonth()) + "." + alarmData.getYear() +
                    " - " + time;
        }
        reminderText.setText(dateText);

        addReminderLayout = getView().findViewById(R.id.task_edit_reminder_layout);
        addReminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TaskAddReminderActivity.class);
                intent.putExtra("CATEGORY_ID", categoryId);
                intent.putExtra("CATEGORY_NAME", categoryName);
                intent.putExtra("TASK_ID", taskId);
                startActivity(intent);
            }
        });
        if (map != null) {
            putSavedMarkerOnMap();
            setLocationOnMap();
        }
    }

    public void initMap(Bundle savedInstanceState) {
        mapView = (MapView) getActivity().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            coarseLocationGranted = true;
            fineLocationGranted = true;
        }
        if (coarseLocationGranted || fineLocationGranted) {
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }
        MapsInitializer.initialize(this.getActivity());
        putSavedMarkerOnMap();
        setLocationOnMap();
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                putMarkerOnMap(point);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        coarseLocationGranted = false;
        fineLocationGranted = false;
        switch (requestCode) {
            case (PERMISSIONS_REQUEST_CODE): {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationGranted = true;
                }
                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    fineLocationGranted = true;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // save made changes before configuration changes
        taskText = taskEditText.getText().toString();
        task.setText(taskText);
        task.setLocation(taskLocation);
        task.setRepeating(repeatCheckBox.isChecked());
        taskViewModel.changeTaskToSurviveConfigChange(task);
    }

    private void putMarkerOnMap(LatLng position) {
        taskLocation = position;
        map.clear();
        map.addMarker(new MarkerOptions().position(position));
    }

    private void putSavedMarkerOnMap() {
        if (taskLocation != null) {
            putMarkerOnMap(taskLocation);
        }
    }

    private void setLocationOnMap() {
        if (taskLocation != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(taskLocation, DEFAULT_ZOOM));
        } else {
            /*
             * Get the best and most recent location of the device, which may be null in rare
             * cases when a location is not available.
             */
            try {
                if (coarseLocationGranted || fineLocationGranted) {
                    FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
                    Task locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(this.getActivity(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                Location lastKnownLocation = (Location) task.getResult();
                                if (lastKnownLocation != null) {
                                    LatLng lastKnownLatLng = new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude());
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng
                                            , DEFAULT_ZOOM));
                                    putMarkerOnMap(lastKnownLatLng);
                                }
                            } else {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                map.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }

    }

}
