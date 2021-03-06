package de.timecrunch.timecrunch.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.activities.TaskEditCategoryActivity;
import de.timecrunch.timecrunch.activities.TaskOverviewActivity;
import de.timecrunch.timecrunch.adapters.ExpandableListAdapter;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.viewModel.CategoryViewModel;

public class TaskCategoriesFragment extends Fragment {
    static final int NEW_CATEGORY_REQUEST = 1;
    ActionBar actionBar;
    ExpandableListView categoryList;
    CategoryViewModel categoryViewModel;
    ProgressBar progressBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        //get ViewModel from parent activity to sync with other fragments
        categoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_categories, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_task_categories, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

        ActionBar actionBar = parentActivity.getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.categories);

        progressBar = getActivity().findViewById(R.id.category_progress_bar);

        setUpDataView(view);

        LiveData<Map<Category, List<Category>>> categoryMapLiveData = categoryViewModel.getSubCategoryMapLiveData();
        categoryMapLiveData.observe(getViewLifecycleOwner(), new Observer<Map<Category, List<Category>>>() {
            @Override
            public void onChanged(@Nullable final Map<Category, List<Category>> subcategoryMap) {
                setUpListAdapter(subcategoryMap);
            }
        });
    }

    private void setUpDataView(View view) {
        categoryList = (ExpandableListView) view.findViewById(R.id.category_list);
        categoryViewModel.setUpLiveData(progressBar);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_CATEGORY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    String categoryName = data.getStringExtra("name");
                    int categoryColor = data.getIntExtra("color", -1);
                    boolean hasTimeBlock = data.getBooleanExtra("getHasTimeBlock", false);
                    Category newCategory = new Category("1", categoryName, categoryColor, hasTimeBlock);
                    categoryViewModel.addCategory(newCategory, progressBar);
                }
        }
    }

    private void setUpListAdapter(Map<Category, List<Category>> subcategoryMap) {
        categoryList.setAdapter(new ExpandableListAdapter(this.getContext(), subcategoryMap) {
            @Override
            public void onCategoryClick(Category category) {
                Intent intent = new Intent(getContext(), TaskOverviewActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_add:
                Intent intent = new Intent(getContext(), TaskEditCategoryActivity.class);
                startActivityForResult(intent, NEW_CATEGORY_REQUEST);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
