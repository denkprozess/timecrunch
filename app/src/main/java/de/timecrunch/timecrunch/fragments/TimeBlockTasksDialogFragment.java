package de.timecrunch.timecrunch.fragments;

import android.annotation.SuppressLint;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.adapters.TimeBlockCategoryTaskListAdapter;
import de.timecrunch.timecrunch.adapters.TimeBlockTaskListAdapter;
import de.timecrunch.timecrunch.model.TaskModel;
import de.timecrunch.timecrunch.model.TimeBlockTaskModel;
import de.timecrunch.timecrunch.viewModel.PlannerViewModel;
import de.timecrunch.timecrunch.viewModel.TaskSelectionViewModel;

public class TimeBlockTasksDialogFragment extends DialogFragment {

    RecyclerView taskListView;
    ItemTouchHelper itemTouchHelper;
    private Button addTasksBtn;
    private Button deleteButton;
    private TaskSelectionViewModel taskSelectionViewModel;
    private ProgressBar progressBar;
    private int year;
    private int month;
    private int day;
    private String timeblockId;
    private boolean editMode = false;
    private boolean taskListInitialized = false;
    private EditBlockCategoryListRecyclerTouchListener categoryListRecyclerTouchListener;
    private EditBlockRecyclerTouchListener recyclerTouchListener;
    private PlannerViewModel plannerViewModel;

    public TimeBlockTasksDialogFragment() {

    }

    @SuppressLint("ValidFragment")
    public TimeBlockTasksDialogFragment(int year, int month, int day, String timeblockId, PlannerViewModel plannerViewModel) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.timeblockId = timeblockId;
        this.plannerViewModel = plannerViewModel;
    }

    public static TimeBlockTasksDialogFragment newInstance(String title, int year, int month, int day, String timeblockId, PlannerViewModel plannerViewModel) {
        TimeBlockTasksDialogFragment fragment = new TimeBlockTasksDialogFragment(year, month, day, timeblockId, plannerViewModel);
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
                if (!editMode) {
                    if (!taskListInitialized) {
                        taskListInitialized = true;
                        setUpTimeBlockTaskListAdapter();
                    } else {
                        updateTimeBlockTaskListAdapter();
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
        deleteButton = view.findViewById(R.id.delete_block_button);
        final View tempView = view;
        addTasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToTaskListEditMode(tempView);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBlock();
            }
        });
        setUpDataView(view);
        initializeSelectedTaskList();
    }

    private void deleteBlock() {
        taskSelectionViewModel.unregisterFromDatabase();
        plannerViewModel.removeTimeBlock(timeblockId, progressBar);
        dismiss();
    }

    /*
        Switches the TimeBlock to EditMode in which the user can add Tasks from the
        corresponding Category to the Block
     */
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
                for (TaskModel t : categoryListRecyclerTouchListener.getSelectedTasks()) {
                    taskSelectionViewModel.addTaskToTimeBlock(
                            timeblockId,
                            new TimeBlockTaskModel(t, false),
                            progressBar);
                }
                switchToTaskListViewMode(tempView);
            }
        });
    }

    /*
        Switches back to the regular TaskList
     */
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
                deleteBlock();
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

            if (categoryListRecyclerTouchListener != null) {
                taskListView.removeOnItemTouchListener(categoryListRecyclerTouchListener);
                categoryListRecyclerTouchListener.clearList();
            }
            TimeBlockTaskListAdapter adapter = new TimeBlockTaskListAdapter(plannerViewModel.getTimeBlock(timeblockId).getTasks());
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);

            if (itemTouchHelper != null) {
                itemTouchHelper.attachToRecyclerView(null);
            }
            if (recyclerTouchListener == null) {
                recyclerTouchListener = new EditBlockRecyclerTouchListener(this.getContext(), taskListView);
            }
            taskListView.addOnItemTouchListener(recyclerTouchListener);
            itemTouchHelper = new ItemTouchHelper(new EditBlockSwipeToDeleteCallback(adapter, this.getContext()));
            itemTouchHelper.attachToRecyclerView(taskListView);
        }
    }

    private void updateTimeBlockTaskListAdapter() {
        TimeBlockTaskListAdapter adapter = new TimeBlockTaskListAdapter(plannerViewModel.getTimeBlock(timeblockId).getTasks());
        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskListView.setAdapter(adapter);
    }

    private void setUpCategoryListAdapter() {
        if (taskSelectionViewModel != null && taskListView != null) {

            if (recyclerTouchListener != null) {
                taskListView.removeOnItemTouchListener(recyclerTouchListener);
            }

            TimeBlockCategoryTaskListAdapter adapter = new TimeBlockCategoryTaskListAdapter(taskSelectionViewModel.getUnselectedTasks());
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);

            if (itemTouchHelper != null) {
                itemTouchHelper.attachToRecyclerView(null);
            }
            if (categoryListRecyclerTouchListener == null) {
                categoryListRecyclerTouchListener = new EditBlockCategoryListRecyclerTouchListener(this.getContext(), taskListView);
            }
            taskListView.addOnItemTouchListener(categoryListRecyclerTouchListener);
        }
    }

    private class EditBlockSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private TimeBlockTaskListAdapter adapter;
        private Drawable icon;
        private ColorDrawable background;

        public EditBlockSwipeToDeleteCallback(TimeBlockTaskListAdapter adapter, Context context) {
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
                icon.setBounds(0, 0, 0, 0);
            }
            background.draw(c);
            icon.draw(c);
        }
    }

    private class EditBlockRecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;

        public EditBlockRecyclerTouchListener(Context context, final RecyclerView recycleView) {
            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                int position = recyclerView.getChildAdapterPosition(child);
                TimeBlockTaskModel timeBlockTaskModel = plannerViewModel.getTimeBlock(timeblockId).getTasks().get(position);
                TaskModel t = timeBlockTaskModel.getTask();
                // dont change back for tasks that are not repeating and finished. Those tasks were erase upon finishing them
                if (t.getIsRepeating() || !timeBlockTaskModel.getIsFinished()) {
                    taskSelectionViewModel.changeFinishedStatusOfTask(timeblockId, t.getId(), progressBar);
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    }

    private class EditBlockCategoryListRecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        ArrayList<TaskModel> selectedTasks;
        private GestureDetector gestureDetector;

        public EditBlockCategoryListRecyclerTouchListener(Context context, final RecyclerView recycleView) {

            selectedTasks = new ArrayList<>();

            this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            if (child != null && gestureDetector.onTouchEvent(motionEvent)) {
                int position = recyclerView.getChildAdapterPosition(child);
                TaskModel t = taskSelectionViewModel.getUnselectedTasks().get(position);

                if (!selectedTasks.contains(t)) {
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
            if (tv != null) {
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            }
        }

        private void unselectTask(View child, TaskModel t) {
            selectedTasks.remove(t);
            child.setBackgroundColor(Color.TRANSPARENT);
            TextView tv = child.findViewById(R.id.task_text);
            if (tv != null) {
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
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    }
}
