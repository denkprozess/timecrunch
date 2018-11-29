package de.timecrunch.timecrunch.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Category> listHeaderData; // header titles
    // child data in format of header title, child title
    private Map<Category, List<Category>> mapChildData;

    public ExpandableListAdapter(Context context, Map<Category, List<Category>> mapChildData) {
        this.context = context;
        this.mapChildData = mapChildData;
        this.listHeaderData = new ArrayList<>(mapChildData.keySet());
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mapChildData.get(this.listHeaderData.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        Category childCategory = (Category) getChild(groupPosition, childPosition);
        final String childText = childCategory.getName();
        int childColor = childCategory.getColor();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
        RelativeLayout subcategoryLayout = (RelativeLayout) convertView.findViewById(R.id.subcategory_layout);
        setUpCategoryLayout(subcategoryLayout, childCategory);
        TextView categoryText = (TextView) convertView.findViewById(R.id.subcategory_text);
        setUpCategoryText(categoryText, childCategory);
        ImageView categoryColor = (ImageView) convertView.findViewById(R.id.subcategory_color);
        setUpCategoryColor(categoryColor, childCategory);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mapChildData.get(this.listHeaderData.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeaderData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listHeaderData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Category headerCategory = (Category) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.todo_list_group, null);
        }
        RelativeLayout categoryLayout = (RelativeLayout) convertView.findViewById(R.id.category_layout);
        setUpCategoryLayout(categoryLayout, headerCategory);
        TextView categoryText = (TextView) convertView.findViewById(R.id.category_text);
        setUpCategoryText(categoryText, headerCategory);
        ImageView categoryColor = (ImageView) convertView.findViewById(R.id.category_color);
        setUpCategoryColor(categoryColor, headerCategory);
        ImageButton indicator = (ImageButton) convertView.findViewById(R.id.category_indicator);
        setUpGroupIndicator(indicator,groupPosition,isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setUpCategoryLayout(RelativeLayout layout, final Category category){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCategoryClick(category);
            }
        });
    }
    private void setUpCategoryText(TextView categoryText, Category category){
        String headerTitle = category.getName();
        categoryText.setTypeface(null, Typeface.BOLD);
        categoryText.setText(headerTitle);
    }

    private void setUpCategoryColor(ImageView categoryColor, Category category){
        int headerColor = category.getColor();
        categoryColor.setColorFilter(headerColor);
    }

    private void setUpGroupIndicator(final ImageButton indicator, final int groupPosition, final boolean isExpanded){
        indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIndicatorClick(indicator, isExpanded, groupPosition);
            }
        });
    }

    public void onCategoryClick(Category category){
        Toast.makeText(context, "Category Clicked!", Toast.LENGTH_LONG).show();
    }

    public void onIndicatorClick(ImageButton indicator, boolean isExpanded, int position){
        Toast.makeText(context, "Indicator Clicked!", Toast.LENGTH_LONG).show();
    }
}
