package de.timecrunch.timecrunch.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;

public class TaskCategoriesFragment extends Fragment {
    ActionBar actionBar;
    ImageButton addButton;
    ExpandableListView categoryList;
    static  List<Category> dummyCategories;
    static Map<Category,List<String>> dummyChildrenMap;
    static boolean initialized;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_task_categories);
        View actionBarView =actionBar.getCustomView();

        addButton= (ImageButton)actionBarView.findViewById(R.id.action_bar_add);
        if(!initialized) {
            dummyCategories = new ArrayList<>();
            dummyCategories.add(new Category("Testcategory1", Color.GREEN));
            dummyCategories.add(new Category("Testcategory2", Color.YELLOW));
            dummyCategories.add(new Category("Testcategory3", Color.BLUE));
            dummyChildrenMap = new HashMap<>();
            List<String> testChildren1 = new ArrayList<>();
            testChildren1.add("TestChild1");
            testChildren1.add("TestChild2");
            testChildren1.add("TestChild3");
            testChildren1.add("TestChild4");
            List<String> testChildren2 = new ArrayList<>();
            testChildren2.add("TestChild5");
            testChildren2.add("TestChild6");
            testChildren2.add("TestChild7");
            List<String> testChildren3 = new ArrayList<>();
            dummyChildrenMap.put(dummyCategories.get(0), testChildren1);
            dummyChildrenMap.put(dummyCategories.get(1), testChildren2);
            dummyChildrenMap.put(dummyCategories.get(2), testChildren3);
            initialized = true;
        }
        categoryList = (ExpandableListView)view.findViewById(R.id.category_list);
        categoryList.setAdapter(new ExpandableListAdapter(this.getContext(),dummyCategories,dummyChildrenMap));

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
                    Category newCategory = new Category(categoryName,categoryColor);
                    dummyCategories.add(newCategory);
                    dummyChildrenMap.put(newCategory,new ArrayList<String>());
                    categoryList.setAdapter(new ExpandableListAdapter(this.getContext(),dummyCategories,dummyChildrenMap));
                }
        }
    }


}
