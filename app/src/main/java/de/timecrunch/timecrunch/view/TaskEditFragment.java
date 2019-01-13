package de.timecrunch.timecrunch.view;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskEditFragment extends Fragment implements OnMapReadyCallback {

    private final int PERMISSIONS_REQUEST_CODE = 1337;
    private final int DEFAULT_ZOOM = 16;
    private boolean coarseLocationGranted;
    private boolean fineLocationGranted;
    private LatLng defaultLocation = new LatLng(-34, 151);
    private int categoryId;
    private String categoryName;
    private int taskId;
    private String taskText;
    private LatLng taskLocation;

    private TaskViewModel taskViewModel;

    private EditText taskEditText;
    private MapView mapView;
    private GoogleMap map;

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
        categoryId = args.getInt("CATEGORY_ID");
        categoryName = args.getString("CATEGORY_NAME");
        taskId = args.getInt("TASK_ID");
        taskText = args.getString("TASK_TEXT");
        if (args.containsKey("TASK_LAT") && args.containsKey("TASK_LNG")) {
            double lat = args.getDouble("TASK_LAT");
            double lng = args.getDouble("TASK_LNG");
            taskLocation = new LatLng(lat,lng);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        new LoadTasksAsync().execute();
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
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
                TaskModel modifiedTask = new TaskModel(taskId, taskText, taskLocation);
                taskViewModel.changeTask(categoryId, modifiedTask);
                //getActivity().setResult(Activity.RESULT_OK);
                //getActivity().finish();
                TaskOverviewFragment fragment = new TaskOverviewFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("CATEGORY_ID", categoryId);
                bundle.putString("CATEGORY_NAME", categoryName);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
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
        Activity parentActivity = getActivity();
        taskEditText = parentActivity.findViewById(R.id.edittext_task);
        taskEditText.setText(taskText);
        initMap(savedInstanceState);
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
                                LatLng lastKnownLatLng = new LatLng(lastKnownLocation.getLatitude(),
                                        lastKnownLocation.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng
                                        , DEFAULT_ZOOM));
                                putMarkerOnMap(lastKnownLatLng);
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

    // TODO: Remove this in the future and capsulate it
    private class LoadTasksAsync extends AsyncTask<Void, Void, Map<Category, List<TaskModel>>> {

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
