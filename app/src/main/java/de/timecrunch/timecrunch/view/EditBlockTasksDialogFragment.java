package de.timecrunch.timecrunch.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
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

import java.util.ArrayList;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.TaskModel;

public class EditBlockTasksDialogFragment extends DialogFragment {

    RecyclerView taskListView;
    ItemTouchHelper itemTouchHelper;
    ArrayList<TaskModel> taskList;

    public EditBlockTasksDialogFragment() {

    }

    public static EditBlockTasksDialogFragment newInstance(String title, ArrayList<TaskModel> taskList) {
        EditBlockTasksDialogFragment fragment = new EditBlockTasksDialogFragment();
        fragment.taskList = taskList;
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_edit_block_tasks, container);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpDataView(view);
        setUpListAdapter();
    }

    private void setUpDataView(View view) {
        taskListView = (RecyclerView) view.findViewById(R.id.block_task_list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskListView.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        taskListView.addItemDecoration(dividerItemDecoration);
        taskListView.addOnItemTouchListener(new EditBlockRecyclerTouchListener(this.getContext(), taskListView));
    }

    private void setUpListAdapter() {
        if (taskList != null) {
            EditBlockTaskListAdapter adapter = new EditBlockTaskListAdapter(taskList);
            taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
            taskListView.setAdapter(adapter);
            if(itemTouchHelper!=null){
                itemTouchHelper.attachToRecyclerView(null);
            }
            itemTouchHelper = new ItemTouchHelper(new EditBlockSwipeToDeleteCallback(adapter, this.getContext()));
            itemTouchHelper.attachToRecyclerView(taskListView);
        }
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
            this.background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            int position = viewHolder.getAdapterPosition();
            // TaskModel deletedTask = adapter.deleteItem(position);
            // taskViewModel.removeTask(deletedTask, progressBar);
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
                // task abhaken
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {}
    }
}
