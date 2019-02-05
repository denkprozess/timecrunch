package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.utilities.CategoryDBHandler;

public class CategoryViewModel extends AndroidViewModel {
    private MutableLiveData<Map<Category, List<Category>>> categoriesLiveData;
    private CategoryDBHandler categoryDBHandler;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryDBHandler = new CategoryDBHandler();
        categoriesLiveData = new MutableLiveData<>();
        Map<Category, List<Category>> categoryMap = new LinkedHashMap<>();
        categoriesLiveData.setValue(categoryMap);
    }

    public LiveData<Map<Category, List<Category>>> getSubCategoryMapLiveData() {
        return categoriesLiveData;
    }

    public void setUpLiveData(ProgressBar progressBar) {
        if (categoriesLiveData.getValue().isEmpty()) {
            initializeCategories(progressBar);
        }
    }

    private void initializeCategories(ProgressBar progressBar) {
        // DB-Calls are asynchronous by default, so no need for AsyncTask
        categoryDBHandler.getCategoriesAndRegisterListener(categoriesLiveData, progressBar);
    }

    public void addCategory(Category userCategory, ProgressBar progressBar) {
        Map<Category, List<Category>> categoryMap = categoriesLiveData.getValue();
        ArrayList<Category> categoryList = new ArrayList<>(categoryMap.keySet());
        if(!categoryList.isEmpty()){
        Collections.sort(categoryList, new Comparator<Category>() {
                    @Override
                    public int compare(Category o1, Category o2) {
                        return o1.getSorting() - o2.getSorting();
                    }
                });

            Category lastCategory = categoryList.get(categoryList.size()-1);
            int highestSorting = lastCategory.getSorting();
            // append new entry at the end of the list via the sorting field users can move their Categories in the list in the future
            userCategory.setSorting(highestSorting+1);
        }else{
            userCategory.setSorting(1);
        }
        // DB-Calls are asynchronous by default, so no need for AsyncTask
        categoryDBHandler.addCategory(userCategory, categoriesLiveData, progressBar);
    }

    public void addSubCategory() {

    }
}
