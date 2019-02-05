package de.timecrunch.timecrunch.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskOverviewFragment extends Fragment {

    private final int REQUEST_EDIT_TASK=1;

    private String categoryId;
    private String categeoryName;

    ActionBar actionBar;
    RecyclerView taskListView;
    TaskViewModel taskViewModel;
    FloatingActionButton floatingActionButton;
    ProgressBar progressBar;
    ItemTouchHelper itemTouchHelper;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        categoryId = args.getString("CATEGORY_ID");
        categeoryName = args.getString("CATEGORY_NAME");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_overview, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setRetainInstance(true);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
        taskViewModel.setUpLiveData(categoryId, progressBar);
        taskViewModel.getTaskMapLiveData().observe(this, new Observer<Map<String, TaskModel>>() {
            @Override
            public void onChanged(@Nullable final Map<String, TaskModel> taskMapLiveData) {
                setUpListAdapter(taskMapLiveData);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = getActivity().findViewById(R.id.tasks_progress_bar);
        setUpActionBar();
        setUpFloatingActionButton(view);
        setUpDataView(view);

    }

    private void setUpActionBar() {
        actionBar.setTitle(categeoryName);
    }

    private void setUpFloatingActionButton(View view) {
        floatingActionButton = view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTaskInputDialog();
            }
        });

    }

    private void setUpDataView(View view) {

        taskListView = (RecyclerView) view.findViewById(R.id.task_list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskListView.getContext(),
                1);
        taskListView.addItemDecoration(dividerItemDecoration);
        taskListView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), taskListView));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_EDIT_TASK:
                taskViewModel.invalidate();
                taskViewModel.setUpLiveData(categoryId, progressBar);;
        }
    }

    private void setUpListAdapter(Map<String, TaskModel> taskMap) {
        if (taskMap != null) {
            TaskListAdapter adapter = new TaskListAdapter(new ArrayList<>(taskMap.values()));
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);
            if(itemTouchHelper!=null){
                itemTouchHelper.attachToRecyclerView(null);
            }
            itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, this.getContext()));
            itemTouchHelper.attachToRecyclerView(taskListView);
        }
    }

    private void showTaskInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this.getContext(), R.style.AlertDialogCustom));
        builder.setTitle(R.string.new_task);

        // Set up the input
        final EditText input = new EditText(this.getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
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
        taskViewModel.addTask(categoryId, new TaskModel(categoryId,text), progressBar);
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private TaskListAdapter adapter;
        private Drawable icon;
        private ColorDrawable background;

        public SwipeToDeleteCallback(TaskListAdapter adapter, Context context) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
            this.icon = ContextCompat.getDrawable(context,
                    R.drawable.ic_delete_white_24dp);
            this.background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = viewHolder.getAdapterPosition();
            TaskModel deletedTask = adapter.deleteItem(position);
           taskViewModel.removeTask(deletedTask, progressBar);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;


            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
                icon.setBounds(0,0,0,0);
            }
            background.draw(c);
            icon.draw(c);
        }
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView) {
            this.gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {}
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
            if(child != null && gestureDetector.onTouchEvent(motionEvent)) {
                int position = recyclerView.getChildAdapterPosition(child);
                TaskModel task = taskViewModel.getTaskAtPosition(position);
                Intent intent = new Intent(getContext(), TaskEditActivity.class);
                intent.putExtra("CATEGORY_ID", categoryId);
                intent.putExtra("CATEGORY_NAME", categeoryName);
                intent.putExtra("TASK_ID", task.getId());
                TaskEditFragment fragment = new TaskEditFragment();
                fragment.setArguments(intent.getExtras());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
                //startActivityForResult(intent,1);
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }
}
