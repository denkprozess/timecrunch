package de.timecrunch.timecrunch.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.Task;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskOverviewFragment extends Fragment {
    // temporary counter
    private static int counter = 1;

    ActionBar actionBar;
    RecyclerView taskListView;
    TaskViewModel taskViewModel;
    FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_overview, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
        taskViewModel.getTaskMap().observe(this, new Observer<Map<Category, List<Task>>>() {
            @Override
            public void onChanged(@Nullable final Map<Category, List<Task>> taskMapLiveData) {
                setUpListAdapter(taskMapLiveData);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpActionBar();
        setUpFloatingActionButton(view);
        setUpDataView(view);

    }

    private void setUpActionBar() {
        actionBar.setTitle("Morning routine");
    }

    private void setUpFloatingActionButton(View view) {
        floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Category, List<Task>> taskMap = taskViewModel.getTaskMap().getValue();
                Category morningRoutine = taskMap.keySet().iterator().next();
                taskViewModel.addTask(morningRoutine, new Task("Build more dummy-tasks (" + counter + ")"));
                counter++;
            }
        });

    }

    private void setUpDataView(View view) {
        Map<Category, List<Task>> taskMap = taskViewModel.getTaskMap().getValue();
        taskListView = (RecyclerView) view.findViewById(R.id.task_list);
        setUpListAdapter(taskMap);
    }

    private void setUpListAdapter(Map<Category, List<Task>> taskMapLiveData) {
        Category morningRoutine = taskMapLiveData.keySet().iterator().next();
        List<Task> taskList = taskMapLiveData.get(morningRoutine);
        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskListView.setAdapter(new TaskListAdapter(taskList));
    }

}