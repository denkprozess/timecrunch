package de.timecrunch.timecrunch.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.model.Category;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<Map<Category, List<Category>>> categoriesLiveData;

    public List<Category> getCategoryList(){
        if(categoriesLiveData ==null){
            initializeCategories();
        }
       return new ArrayList<Category>(categoriesLiveData.getValue().keySet());
    }

    /*public Map<Category, List<Category>>getTaskMap(){
        if(categoriesLiveData ==null){
            initializeCategories();
        }
        return categoriesLiveData.getValue();
    }*/

    public LiveData<Map<Category, List<Category>>> getSubCategoryMap(){
        if(categoriesLiveData==null){
            initializeCategories();
        }
        return categoriesLiveData;
    }
    private void initializeCategories(){
        //Important that this is LinkedHashMap to keep the sorting
        LinkedHashMap<Category,List<Category>> dummyChildrenMap = new LinkedHashMap<>();

        List<Category> dummyCategories = new ArrayList<>();
        dummyCategories.add(new Category(1,"Testcategory1", Color.GREEN, false));
        dummyCategories.add(new Category(1,"Testcategory2", Color.YELLOW, false));
        dummyCategories.add(new Category(1,"Testcategory3", Color.BLUE, false));

        List<Category> testChildren1 = new ArrayList<>();
        testChildren1.add(new Category(1,"TestChild1", Color.GREEN, false));
        testChildren1.add(new Category(1,"TestChild2", Color.GREEN, false));
        testChildren1.add(new Category(1,"TestChild3", Color.GREEN, false));
        testChildren1.add(new Category(1,"TestChild4", Color.GREEN, false));

        List<Category> testChildren2 = new ArrayList<>();
        testChildren2.add(new Category(1,"TestChild5", Color.YELLOW, false));
        testChildren2.add(new Category(1,"TestChild6", Color.YELLOW, false));
        testChildren2.add(new Category(1,"TestChild7", Color.YELLOW, false));

        List<Category> testChildren3 = new ArrayList<>();
        dummyChildrenMap.put(dummyCategories.get(0), testChildren1);
        dummyChildrenMap.put(dummyCategories.get(1), testChildren2);
        dummyChildrenMap.put(dummyCategories.get(2), testChildren3);
        categoriesLiveData = new MutableLiveData<>();
        categoriesLiveData.setValue(dummyChildrenMap);
    }

    public void addCategory(Category category){
        List<Category> categoryList = new ArrayList<>();
        Map<Category, List<Category>> categoryMap = categoriesLiveData.getValue();
        categoryMap.put(category, categoryList);
        categoriesLiveData.setValue(categoryMap);

    }

    public void addSubCategory(){

    }
}
