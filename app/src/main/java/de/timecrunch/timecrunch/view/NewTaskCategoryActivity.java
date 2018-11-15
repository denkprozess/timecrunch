package de.timecrunch.timecrunch.view;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import de.timecrunch.timecrunch.R;

public class NewTaskCategoryActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton finishedButton;
    TextView actionBarLabel;
    TextInputLayout categoryNameInput;
    Switch timeBlockSwitch;
    ImageView colorPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_todo_category);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_new_category);
        View view = getSupportActionBar().getCustomView();

        backButton = (ImageButton) view.findViewById(R.id.action_bar_back);
        finishedButton = (ImageButton) view.findViewById(R.id.action_bar_finished);
        actionBarLabel = (TextView) view.findViewById(R.id.action_bar_label);
        actionBarLabel.setText(R.string.title_activity_new_category);
        categoryNameInput = findViewById(R.id.category_name_input);
        timeBlockSwitch = findViewById(R.id.time_block_switch);
        colorPreview = findViewById(R.id.color_preview);

        if(savedInstanceState!=null){
            restoreFieldValues(savedInstanceState);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameInput = categoryNameInput.getEditText().getText().toString();
                // TODO: This might need to be reworked with something else than an ImageView because the tint of an ImageView is hard to set/get
                int colorInput = colorPreview.getSolidColor();
                Intent resultIntent = new Intent();
                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtra("name", nameInput);
                resultIntent.putExtra("color", colorInput);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CATEGORY_NAME_INPUT", categoryNameInput.getEditText().getText().toString());
        outState.putBoolean("TIME_BLOCK_SWITCH", timeBlockSwitch.isChecked());
        super.onSaveInstanceState(outState);
    }

    private void restoreFieldValues(Bundle savedInstanceState){
        String categoryNameText = (String) savedInstanceState.get("CATEGORY_NAME_INPUT");
        boolean timeBlockSwitchState = (boolean) savedInstanceState.get("TIME_BLOCK_SWITCH");
        categoryNameInput.getEditText().setText(categoryNameText);
        timeBlockSwitch.setChecked(timeBlockSwitchState);
    }
}
