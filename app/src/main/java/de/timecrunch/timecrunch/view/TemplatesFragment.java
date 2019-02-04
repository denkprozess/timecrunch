package de.timecrunch.timecrunch.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.timecrunch.timecrunch.R;

public class TemplatesFragment extends Fragment {

    private View view;
    private LinearLayout templateContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_templates, container, false);

        templateContainer = view.findViewById(R.id.templates_container);

        initBlocks(templateContainer);

        return view;
    }

    private void initBlocks(LinearLayout fl) {

        final TemplateBlock templateBlock = new TemplateBlock(this.getContext(), "#01a5af");
        templateBlock.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        templateBlock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) templateBlock.getColor());

                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(templateBlock);
                v.setOnDragListener(new TemplateDragEventListener());
                v.startDrag(dragData, myShadow, null, 0
                );
                return true;
            }
        });

        fl.addView(templateBlock);

        final TemplateBlock templateBlock2 = new TemplateBlock(this.getContext(), "#c4023f");
        templateBlock2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        templateBlock2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) templateBlock2.getColor());

                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(templateBlock2);
                v.setOnDragListener(new TemplateDragEventListener());
                v.startDrag(dragData, myShadow, null, 0
                );
                return true;
            }
        });


        fl.addView(templateBlock2);

        final TemplateBlock templateBlock3 = new TemplateBlock(this.getContext(), "#ec8c00");
        templateBlock3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        templateBlock3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((String) templateBlock3.getColor());

                ClipData dragData = new ClipData(
                        (String) v.getTag(),
                        new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                        item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(templateBlock3);
                v.setOnDragListener(new TemplateDragEventListener());
                v.startDrag(dragData, myShadow, null, 0
                );
                return true;
            }
        });

        fl.addView(templateBlock3);
    }
}
