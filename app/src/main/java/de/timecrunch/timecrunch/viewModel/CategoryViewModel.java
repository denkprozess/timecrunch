package de.timecrunch.timecrunch.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.utilities.DBHandler;

public class CategoryViewModel extends AndroidViewModel {
    private MutableLiveData<Map<Category, List<Category>>> categoriesLiveData;
    private DBHandler dbHandler;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        dbHandler = new DBHandler(application.getApplicationContext());
        categoriesLiveData = new MutableLiveData<>();
        Map<Category, List<Category>> categoryMap = new LinkedHashMap<>();
        categoriesLiveData.setValue(categoryMap);
    }

    public List<Category> getCategoryList() {
        if (categoriesLiveData.getValue().isEmpty()) {
            initializeCategories();
        }
        return new ArrayList<Category>(categoriesLiveData.getValue().keySet());
    }

    public LiveData<Map<Category, List<Category>>> getSubCategoryMapLiveData() {
        return categoriesLiveData;
    }

    public Map<Category, List<Category>> getSubCategoryMap() {
        if (categoriesLiveData.getValue().isEmpty()) {
            initializeCategories();
        }
        return categoriesLiveData.getValue();
    }

    private void initializeCategories() {
        Map<Category, List<Category>> categoryMap = dbHandler.getCategories();
        categoriesLiveData.postValue(categoryMap);
    }

    public void addCategory(Category userCategory) {
        List<Category> categoryList = new ArrayList<>();
        Map<Category, List<Category>> categoryMap = categoriesLiveData.getValue();
        String name = userCategory.getName();
        int color = userCategory.getColor();
        boolean hasTimeBlock = userCategory.hasTimeBlock();
        int id = dbHandler.createCategory(name, color, hasTimeBlock);
        if(id!=-1){
            Category newCategory = new Category(id,name,color,hasTimeBlock);
            categoryMap.put(newCategory, categoryList);
            categoriesLiveData.postValue(categoryMap);
        }


    }

    public void addSubCategory() {

    }
}
