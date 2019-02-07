package de.timecrunch.timecrunch.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;
import de.timecrunch.timecrunch.viewModel.TaskSelectionViewModel;

public class EditBlockTasksDialogFragment extends DialogFragment {

    RecyclerView taskListView;
    ItemTouchHelper itemTouchHelper;
    private Button addTasksBtn;
    private TaskSelectionViewModel taskSelectionViewModel;
    private ProgressBar progressBar;
    private int year;
    private int month;
    private int day;
    private String timeblockId;
    private boolean editMode = false;
    private boolean taskListInitialized = false;
    private boolean categoryListInitialized = false;
    private EditBlockCategoryListRecyclerTouchListener categoryListRecyclerTouchListener;
    private EditBlockRecyclerTouchListener recyclerTouchListener;

    public EditBlockTasksDialogFragment() {

    }

    @SuppressLint("ValidFragment")
    public EditBlockTasksDialogFragment(int year, int month, int day, String timeblockId) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.timeblockId = timeblockId;
    }

    public static EditBlockTasksDialogFragment newInstance(String title, int year, int month, int day, String timeblockId) {
        EditBlockTasksDialogFragment fragment = new EditBlockTasksDialogFragment(year, month, day, timeblockId);
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        taskSelectionViewModel = ViewModelProviders.of(getActivity()).get(TaskSelectionViewModel.class);
        progressBar = getActivity().findViewById(R.id.tasks_progress_bar);
        taskSelectionViewModel.setUpLiveData(year, month, day, timeblockId, progressBar);
        return inflater.inflate(R.layout.fragment_edit_block_tasks, container);
    }

    private void initializeSelectedTaskList() {

        taskSelectionViewModel.getTaskSelectionLiveData().observe(this, new Observer<Map<TaskModel, Boolean>>() {
            @Override
            public void onChanged(@Nullable Map<TaskModel, Boolean> taskModelBooleanMap) {
                if(!editMode) {
                    if(!taskListInitialized) {
                        taskListInitialized = true;
                        setUpTimeBlockTaskListAdapter();
                    } else {
                        updateTimeBlockTaskListAdapter();
                    }
                }
            }
        });
    }

    private void initializeCategoryList() {

        taskSelectionViewModel.getTaskSelectionLiveData().observe(this, new Observer<Map<TaskModel, Boolean>>() {
            @Override
            public void onChanged(@Nullable Map<TaskModel, Boolean> taskModelBooleanMap) {
                if(editMode) {
                    if(!categoryListInitialized) {
                        categoryListInitialized = true;
                        setUpCategoryListAdapter();
                    } else {
                        updateCategoryListAdapter();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTasksBtn = view.findViewById(R.id.add_tasks_button);
        final View tempView = view;
            addTasksBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToTaskListEditMode(tempView);
                }
            });
        setUpDataView(view);
        initializeSelectedTaskList();
    }

    private void switchToTaskListEditMode(View v) {
        final View tempView = v;
        editMode = true;

        TextView blockDialogTitle = v.findViewById(R.id.time_block_dialog_title);
        blockDialogTitle.setText("Add more tasks");

        setUpCategoryListAdapter();

        Button deleteButton = v.findViewById(R.id.delete_block_button);
        deleteButton.setBackgroundTintList(v.getResources().getColorStateList(R.color.common_google_signin_btn_text_light_default));
        deleteButton.setText("BACK");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTaskListViewMode(tempView);
            }
        });

        Button addTasksButton = v.findViewById(R.id.add_tasks_button);
        addTasksButton.setText("SAVE TASKS");
        addTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(TaskModel t : categoryListRecyclerTouchListener.getSelectedTasks()) {
                    taskSelectionViewModel.addTaskToTimeBlock(
                            timeblockId,
                            new TimeBlockTaskModel(t, false),
                            progressBar);
                }
                switchToTaskListViewMode(tempView);
            }
        });
    }

    private void switchToTaskListViewMode(View v) {
        final View tempView = v;
        editMode = false;

        TextView blockDialogTitle = v.findViewById(R.id.time_block_dialog_title);
        blockDialogTitle.setText("Clear your tasks");

        setUpTimeBlockTaskListAdapter();

        Button deleteButton = v.findViewById(R.id.delete_block_button);
        deleteButton.setBackgroundTintList(v.getResources().getColorStateList(R.color.materialcolorpicker__red));
        deleteButton.setText("DELETE BLOCK");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DELETE BLOCK
            }
        });

        Button addTasksButton = v.findViewById(R.id.add_tasks_button);
        addTasksButton.setText("ADD TASKS");
        addTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTaskListEditMode(tempView);
            }
        });
    }

    private void setUpDataView(View view) {
        taskListView = (RecyclerView) view.findViewById(R.id.block_task_list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskListView.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        taskListView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpTimeBlockTaskListAdapter() {
        if (taskSelectionViewModel != null && taskListView != null) {

            if(categoryListRecyclerTouchListener != null) {
                taskListView.removeOnItemTouchListener(categoryListRecyclerTouchListener);
                categoryListRecyclerTouchListener.clearList();
            }

            EditBlockTaskListAdapter adapter = new EditBlockTaskListAdapter(taskSelectionViewModel.getSelectedTasks());
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);

            if(itemTouchHelper != null){
                itemTouchHelper.attachToRecyclerView(null);
            }
            if(recyclerTouchListener == null) {
                recyclerTouchListener = new EditBlockRecyclerTouchListener(this.getContext(), taskListView);
            }
            taskListView.addOnItemTouchListener(recyclerTouchListener);
            itemTouchHelper = new ItemTouchHelper(new EditBlockSwipeToDeleteCallback(adapter, this.getContext()));
            itemTouchHelper.attachToRecyclerView(taskListView);
        }
    }

    private void updateTimeBlockTaskListAdapter() {
        EditBlockTaskListAdapter adapter = new EditBlockTaskListAdapter(taskSelectionViewModel.getSelectedTasks());
        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskListView.setAdapter(adapter);
    }

    private void setUpCategoryListAdapter() {
        if (taskSelectionViewModel != null && taskListView != null) {

            if(recyclerTouchListener != null) {
                taskListView.removeOnItemTouchListener(recyclerTouchListener);
            }

            EditBlockCategoryTaskListAdapter adapter = new EditBlockCategoryTaskListAdapter(taskSelectionViewModel.getUnselectedTasks());
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);

            if(itemTouchHelper != null){
                itemTouchHelper.attachToRecyclerView(null);
            }
            if(categoryListRecyclerTouchListener == null) {
                categoryListRecyclerTouchListener = new EditBlockCategoryListRecyclerTouchListener(this.getContext(), taskListView);
            }
            taskListView.addOnItemTouchListener(categoryListRecyclerTouchListener);
            // itemTouchHelper = new ItemTouchHelper();
            // itemTouchHelper.attachToRecyclerView(taskListView);
        }
    }

    private void updateCategoryListAdapter() {
        EditBlockCategoryTaskListAdapter adapter = new EditBlockCategoryTaskListAdapter(taskSelectionViewModel.getUnselectedTasks());
        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskListView.setAdapter(adapter);
    }

    private class EditBlockSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private EditBlockTaskListAdapter adapter;
        private Drawable icon;
        private ColorDrawable background;

        public EditBlockSwipeToDeleteCallback(EditBlockTaskListAdapter adapter, Context context) {
            super(0, ItemTouchHelper.LEFT);
            this.adapter = adapter;
            this.icon = ContextCompat.getDrawable(context,
                    R.drawable.ic_delete_white_24dp);
            this.background = new ColorDrawable(getResources().getColor(R.color.materialcolorpicker__red));
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = viewHolder.getAdapterPosition();
            TaskModel t = taskSelectionViewModel.getSelectedTasks().get(position);
            taskSelectionViewModel.removeTaskFromTimeBlock(timeblockId, t.getId(), progressBar);
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

    private class EditBlockRecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;

        public EditBlockRecyclerTouchListener(Context context, final RecyclerView recycleView) {
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
                TaskModel t = taskSelectionViewModel.getSelectedTasks().get(position);
                Log.d("asdfasdfasdfasdf", t.getText());
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }

    private class EditBlockCategoryListRecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        ArrayList<TaskModel> selectedTasks;

        public EditBlockCategoryListRecyclerTouchListener(Context context, final RecyclerView recycleView) {

            selectedTasks = new ArrayList<>();

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
                TaskModel t = taskSelectionViewModel.getUnselectedTasks().get(position);

                if(!selectedTasks.contains(t)) {
                    selectTask(child, t);
                } else {
                    unselectTask(child, t);
                }
            }
            return false;
        }

        private void selectTask(View child, TaskModel t) {
            selectedTasks.add(t);
            child.setBackgroundColor(Color.parseColor("#ceeecd"));
            TextView tv = child.findViewById(R.id.task_text);
            if(tv != null) {
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            }
        }

        private void unselectTask(View child, TaskModel t) {
            selectedTasks.remove(t);
            child.setBackgroundColor(Color.TRANSPARENT);
            TextView tv = child.findViewById(R.id.task_text);
            if(tv != null) {
                tv.setTypeface(null, Typeface.NORMAL);
            }
        }

        public ArrayList<TaskModel> getSelectedTasks() {
            return selectedTasks;
        }

        public void clearList() {
            selectedTasks.removeAll(selectedTasks);
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }
}
