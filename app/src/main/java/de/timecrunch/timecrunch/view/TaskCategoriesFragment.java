package de.timecrunch.timecrunch.view;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.viewModel.TaskViewModel;

public class TaskCategoriesFragment extends Fragment {
    ActionBar actionBar;
    ImageButton addButton;
    ExpandableListView categoryList;
    TaskViewModel taskViewModel;

    static final int NEW_CATEGORY_REQUEST=1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //get ViewModel from parent activity to sync with other fragments
        taskViewModel = ViewModelProviders.of(getActivity()).get(TaskViewModel.class);
        taskViewModel.getSubCategoryMap().observe(this, new Observer<Map<Category,List<Category>>>() {
            @Override
            public void onChanged(@Nullable final Map<Category,List<Category>> subcategoryMapLiveData) {
                setUpListAdapter(subcategoryMapLiveData);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpActionBar();
        setUpDataView(view);
        moveIndicatorToRight();

    }

    private void setUpActionBar(){
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_task_categories);
        View actionBarView =actionBar.getCustomView();

        addButton= (ImageButton)actionBarView.findViewById(R.id.action_bar_add);
    }

    private void setUpDataView(View view){
        Map<Category,List<Category>> subcategoryMap = taskViewModel.getSubCategoryMap().getValue();
        categoryList = (ExpandableListView)view.findViewById(R.id.category_list);
        setUpListAdapter(subcategoryMap);

    }


    private void moveIndicatorToRight(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, r.getDisplayMetrics());
        categoryList.setIndicatorBoundsRelative(width - px, width);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewTaskCategoryActivity.class);
                startActivityForResult(intent, NEW_CATEGORY_REQUEST);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case NEW_CATEGORY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    String categoryName = data.getStringExtra("name");
                    int categoryColor = data.getIntExtra("color", -1);
                    boolean hasTimeBlock = data.getBooleanExtra("hasTimeBlock", false);
                    Category newCategory = new Category(categoryName,categoryColor, hasTimeBlock);
                    taskViewModel.addCategory(newCategory);
                    categoryList.invalidate();
                }
        }
    }

    private void setUpListAdapter(Map<Category,List<Category>> subcategoryMap){
        categoryList.setAdapter(new ExpandableListAdapter(this.getContext(),subcategoryMap){
            @Override
            public void onCategoryClick(){
                Intent intent = new Intent(getContext(), TaskOverviewActivity.class);
                startActivity(intent);
            }

            @Override
            public void onIndicatorClick(ImageButton indicator, boolean isExpanded, int position){
                if(isExpanded){
                    indicator.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
                    categoryList.collapseGroup(position);
                }else{
                    indicator.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
                    categoryList.expandGroup(position);
                }
            }
        });
    }


}
