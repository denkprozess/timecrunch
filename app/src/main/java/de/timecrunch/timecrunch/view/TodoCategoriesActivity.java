package de.timecrunch.timecrunch.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;

public class TodoCategoriesActivity extends AppCompatActivity {
    ImageButton backButton;
    ImageButton addButton;
    ExpandableListView categoryList;
    List<Category> dummyCategories;
    Map<Category,List<String>> dummyChildrenMap;

    static final int NEW_CATEGORY_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_categories);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_todo);
        View view =getSupportActionBar().getCustomView();

        backButton= (ImageButton)view.findViewById(R.id.action_bar_back);
        addButton= (ImageButton)view.findViewById(R.id.action_bar_add);

        dummyCategories= new ArrayList<>();
        dummyCategories.add(new Category("Testcategory1", Color.GREEN));
        dummyCategories.add(new Category("Testcategory2", Color.YELLOW));
        dummyCategories.add(new Category("Testcategory3", Color.BLUE));
        dummyChildrenMap = new HashMap<>();
        List<String> testChildren1= new ArrayList<>();
        testChildren1.add("TestChild1");
        testChildren1.add("TestChild2");
        testChildren1.add("TestChild3");
        testChildren1.add("TestChild4");
        List<String> testChildren2= new ArrayList<>();
        testChildren2.add("TestChild5");
        testChildren2.add("TestChild6");
        testChildren2.add("TestChild7");
        List<String> testChildren3= new ArrayList<>();
        dummyChildrenMap.put(dummyCategories.get(0), testChildren1);
        dummyChildrenMap.put(dummyCategories.get(1), testChildren2);
        dummyChildrenMap.put(dummyCategories.get(2),testChildren3);
        categoryList = (ExpandableListView)findViewById(R.id.category_list);
        categoryList.setAdapter(new ExpandableListAdapter(this,dummyCategories,dummyChildrenMap));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, r.getDisplayMetrics());
        categoryList.setIndicatorBoundsRelative(width - px, width);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewTodoCategoryActivity.class);
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
                    categoryList.setAdapter(new ExpandableListAdapter(this,dummyCategories,dummyChildrenMap));
                 }
        }
    }
}
