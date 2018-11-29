package de.timecrunch.timecrunch.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

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
                showTaskInputDialog();
//                Map<Category, List<Task>> taskMap = taskViewModel.getTaskMap().getValue();
//                Category morningRoutine = taskMap.keySet().iterator().next();
//                taskViewModel.addTask(morningRoutine, new Task("Build more dummy-tasks (" + counter + ")"));
//                counter++;
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

    private void showTaskInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.new_task);

        // Set up the input
        final EditText input = new EditText(this.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                addNewTask(text);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void addNewTask(String text) {
        Map<Category, List<Task>> taskMap = taskViewModel.getTaskMap().getValue();
        Category morningRoutine = taskMap.keySet().iterator().next();
        taskViewModel.addTask(morningRoutine, new Task(text));
    }

}
