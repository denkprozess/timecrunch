package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.utilities.CategoryDBHandler;

public class CategoryViewModel extends AndroidViewModel implements CategoryViewModelDatabaseCallback {
    private MutableLiveData<Map<Category, List<Category>>> categoriesLiveData;
    private CategoryDBHandler categoryDBHandler;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryDBHandler = new CategoryDBHandler();
        categoriesLiveData = new MutableLiveData<>();
        Map<Category, List<Category>> categoryMap = new LinkedHashMap<>();
        categoriesLiveData.setValue(categoryMap);
    }

    public List<Category> getCategoryList(ProgressBar progressBar) {
        if (categoriesLiveData.getValue().isEmpty()) {
            initializeCategories(progressBar);
        }
        return new ArrayList<Category>(categoriesLiveData.getValue().keySet());
    }

    public LiveData<Map<Category, List<Category>>> getSubCategoryMapLiveData() {
        return categoriesLiveData;
    }

    public Map<Category, List<Category>> getSubCategoryMap(ProgressBar progressBar) {
        if (categoriesLiveData.getValue().isEmpty()) {
            initializeCategories(progressBar);
        }
        return categoriesLiveData.getValue();
    }

    private void initializeCategories(ProgressBar progressBar) {
        // DB-Calls are asynchronous by default, so no need for AsyncTask
        categoryDBHandler.getCategories(categoriesLiveData, progressBar);
    }

    public void addCategory(Category userCategory, ProgressBar progressBar) {
        List<Category> categoryList = new ArrayList<>();
        Map<Category, List<Category>> categoryMap = categoriesLiveData.getValue();
        String name = userCategory.getName();
        int color = userCategory.getColor();
        boolean hasTimeBlock = userCategory.hasTimeBlock();
        // DB-Calls are asynchronous by default, so no need for AsyncTask
        categoryDBHandler.addCategory(userCategory, categoriesLiveData, progressBar);
    }

    public void addSubCategory() {

    }
}
