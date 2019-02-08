package de.timecrunch.timecrunch.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.timecrunch.timecrunch.R;
import de.timecrunch.timecrunch.model.Category;
import de.timecrunch.timecrunch.viewModel.CategoryViewModel;

public class TemplatesFragment extends Fragment {

    private View view;
    private LinearLayout templateContainer;
    private CategoryViewModel categoryViewModel;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_templates, container, false);
        categoryViewModel = ViewModelProviders.of(getActivity()).get(CategoryViewModel.class);
        templateContainer = view.findViewById(R.id.templates_container);
        // initBlocks(templateContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = getActivity().findViewById(R.id.category_progress_bar);
        categoryViewModel.setUpLiveData(progressBar);
        final LiveData<Map<Category,List<Category>>> categoryMapLiveData = categoryViewModel.getSubCategoryMapLiveData();
        categoryMapLiveData.observe(getViewLifecycleOwner(), new Observer<Map<Category, List<Category>>>() {
            @Override
            public void onChanged(@Nullable final Map<Category, List<Category>> subcategoryMap) {
                if(templateContainer != null) {
                    templateContainer.removeAllViews();
                }
                ArrayList<Category> categories = new ArrayList<>(subcategoryMap.keySet());
                for(Category c : categories) {
                    createHollowBlock(c);
                }
            }
        });
    }

    private void createHollowBlock(Category c) {

        final String colorString = String.format("#%06X", (0xFFFFFF & c.getColor()));
        final String categoryId = c.getId();

        final TemplateBlock templateBlock = new TemplateBlock(this.getContext(), colorString, c.getName());
        templateBlock.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        templateBlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item colorItem = new ClipData.Item(colorString);
                ClipData.Item idItem = new ClipData.Item(categoryId);

                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        colorItem);

                dragData.addItem(idItem);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(templateBlock);
                v.setOnDragListener(new TemplateDragEventListener());
                v.startDrag(dragData, myShadow, null, 1
                );
                return true;
            }
        });

        templateContainer.addView(templateBlock);
    }
}
