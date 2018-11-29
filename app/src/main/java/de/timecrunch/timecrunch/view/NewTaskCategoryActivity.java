package de.timecrunch.timecrunch.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import de.timecrunch.timecrunch.R;

public class NewTaskCategoryActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton finishedButton;
    TextView actionBarLabel;
    TextInputLayout categoryNameInput;
    RelativeLayout colorPickerRow;
    Switch timeBlockSwitch;
    ImageView colorPreview;
    int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_category);
        setUpActionBar();

        colorPickerRow = (RelativeLayout) findViewById(R.id.color_layout_area);
        categoryNameInput = findViewById(R.id.category_name_input);
        timeBlockSwitch = findViewById(R.id.time_block_switch);
        colorPreview = findViewById(R.id.color_preview);

        if(savedInstanceState!=null){
            restoreFieldValues(savedInstanceState);
        }else{
            selectedColor = Color.RED;
        }
        colorPreview.setColorFilter(selectedColor);

        setUpColorPicker();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_new_category, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_finished:
                returnResult();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnResult(){
        String nameInput = categoryNameInput.getEditText().getText().toString();
        boolean hasTimeBlock = timeBlockSwitch.isChecked();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", nameInput);
        resultIntent.putExtra("color", selectedColor);
        resultIntent.putExtra("hasTimeBlock", hasTimeBlock);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void setUpActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpColorPicker(){
        int selectedAlpha = Color.alpha(selectedColor);
        int selectedRed = Color.red(selectedColor);
        int selectedGreen = Color.green(selectedColor);
        int selectedBlue = Color.blue(selectedColor);
        final ColorPicker cp = new ColorPicker(this,selectedAlpha, selectedRed, selectedGreen, selectedBlue);

        colorPickerRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cp.show();

                Button okColor = (Button)cp.findViewById(R.id.okColorButton);

                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedColor = Color.argb(cp.getAlpha(),cp.getRed(), cp.getGreen(),cp.getBlue());
                        colorPreview.setColorFilter(selectedColor);
                        cp.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CATEGORY_NAME_INPUT", categoryNameInput.getEditText().getText().toString());
        outState.putBoolean("TIME_BLOCK_SWITCH", timeBlockSwitch.isChecked());
        outState.putInt("SELECTED_COLOR", selectedColor);
        super.onSaveInstanceState(outState);
    }

    private void restoreFieldValues(Bundle savedInstanceState){
        String categoryNameText = (String) savedInstanceState.get("CATEGORY_NAME_INPUT");
        boolean timeBlockSwitchState = (boolean) savedInstanceState.get("TIME_BLOCK_SWITCH");
        selectedColor = (int) savedInstanceState.get("SELECTED_COLOR");
        categoryNameInput.getEditText().setText(categoryNameText);
        timeBlockSwitch.setChecked(timeBlockSwitchState);
    }
}
