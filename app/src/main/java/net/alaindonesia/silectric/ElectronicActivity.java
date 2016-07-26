package net.alaindonesia.silectric;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import net.alaindonesia.silectric.model.DbConnection;
import net.alaindonesia.silectric.model.Electronic;
import net.alaindonesia.silectric.model.ElectronicTimeUsageTemplate;
import net.alaindonesia.silectric.model.UsageMode;

import java.util.ArrayList;
import java.util.List;

public class ElectronicActivity extends AppCompatActivity {

    private Electronic electronic;
    private DbConnection dbConnection;
    private boolean isNewElectronic =false;
    private ArrayList<ElectronicTimeUsageTemplate> timeUsageTemplateArrayList;
    ImageButton deleteElectronicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbConnection = new DbConnection(this);
        this.electronic = getIntent().getParcelableExtra("electronic");

        deleteElectronicButton = (ImageButton) findViewById(R.id.deleteElectronicButton);

        if (electronic == null){
            this.electronic = new Electronic();
            isNewElectronic = true;
            deleteElectronicButton.setVisibility(View.INVISIBLE);
        }else deleteElectronicButton.setVisibility(View.VISIBLE);

        initForm();
        initListTimeUsageTemplate();
        initDeleteElectronicButton();
        initSaveElectronicButton();

    }

    private void initForm(){
        TextView electronicNameInElectronicContentForm = (TextView) findViewById(R.id.electronicNameInElectronicContent);
        ImageButton addTimeUsageButton = (ImageButton) findViewById(R.id.addTimeUsageInElectronicContentButton);
        timeUsageTemplateArrayList = dbConnection.getElectronicTimeUsageTemplateByIdElectronic(electronic.getIdElectronic());

        if(this.electronic != null){
            electronicNameInElectronicContentForm.setText(String.valueOf(this.electronic.getElectronicName()));
        }

        addTimeUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpTimeUsageDialog(v, true, null);
            }
        });

    }

    private void initDeleteElectronicButton(){

        if(this.electronic == null){
            deleteElectronicButton.setVisibility(View.INVISIBLE);
        }else{
            deleteElectronicButton.setVisibility(View.VISIBLE);
            deleteElectronicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int idElectronic = electronic.getIdElectronic();
                    dbConnection.deleteElectronic(idElectronic);
                    finish();
                }
            });


        }
    }

    private void initSaveElectronicButton(){

        ImageButton saveFloatingButton = (ImageButton) findViewById(R.id.saveElectronicButton);
        saveFloatingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView electronicNameInElectronicForm = (TextView) findViewById(R.id.electronicNameInElectronicContent);
                String electronicName = electronicNameInElectronicForm.getText().toString();
                electronic.setElectronicName(electronicName);

                if (isNewElectronic) {
                    dbConnection.addElectronic(electronic, timeUsageTemplateArrayList);
                } else {
                    dbConnection.editElectronic(electronic, timeUsageTemplateArrayList);
                }

                finish();


            }
        });

    }


    private void initListTimeUsageTemplate(){
        ListView listUsageListView = (ListView)findViewById(R.id.timeUsageListViewInElectronicContent);

        ListAdapter timeUsageTemplateListAdapter = new ElectronicTimeUsageTemplateListAdapter(this, timeUsageTemplateArrayList);

        listUsageListView.setAdapter(timeUsageTemplateListAdapter);
        listUsageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ElectronicTimeUsageTemplate timeUsageTemplate = (ElectronicTimeUsageTemplate) parent.getItemAtPosition(position);
                popUpTimeUsageDialog(view, false, timeUsageTemplate);
            }
        });


    }

    private void popUpTimeUsageDialog(View v, final boolean isNewElectronicTimeUsageTemplate, final ElectronicTimeUsageTemplate timeUsageTemplate) {

        final Spinner usageModeSpinner = new Spinner(v.getContext());
        final NumberPicker wattageNumberPicker = new NumberPicker(v.getContext());
        final NumberPicker hoursInPopupTimeUsage = new NumberPicker(v.getContext());
        final NumberPicker minutesInPopupTimeUsage = new NumberPicker(v.getContext());

        LinearLayout timeUsageLayoutDialog = initTimeUsageLayoutDialog(v,
                usageModeSpinner, wattageNumberPicker, hoursInPopupTimeUsage,
                minutesInPopupTimeUsage);

        String  okButtonString;
        if (isNewElectronicTimeUsageTemplate){
            okButtonString = "Add";
        }else{
            okButtonString = "Save";
            wattageNumberPicker.setValue(timeUsageTemplate.getWattage());
            hoursInPopupTimeUsage.setValue(timeUsageTemplate.getHours());
            minutesInPopupTimeUsage.setValue(timeUsageTemplate.getMinutes());

            ArrayList<UsageMode> usageModeArrayList = dbConnection.getUsageModeList();
            int usageModeSpinnerPos = 0;
            for (UsageMode usageModeInList : usageModeArrayList){
                if(timeUsageTemplate.getIdUsageMode() == usageModeInList.getIdUsageMode())
                    break;
                usageModeSpinnerPos++;
            }
            usageModeSpinner.setSelection(usageModeSpinnerPos);


        }

        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());

        alert.setPositiveButton(okButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wattageNumberPicker.clearFocus();
                hoursInPopupTimeUsage.clearFocus();
                minutesInPopupTimeUsage.clearFocus();
                UsageMode usageMode = (UsageMode) usageModeSpinner.getSelectedItem();
                int idUsageMode = usageMode.getIdUsageMode();
                int wattage = wattageNumberPicker.getValue();
                int hours = hoursInPopupTimeUsage.getValue();
                int minutes = minutesInPopupTimeUsage.getValue();

                if (isNewElectronicTimeUsageTemplate) {
                    timeUsageTemplateArrayList.add(new ElectronicTimeUsageTemplate(true, idUsageMode, wattage, hours, minutes, usageMode));
                    initListTimeUsageTemplate();
                }else {

                    timeUsageTemplate.setIdUsageMode(idUsageMode);
                    timeUsageTemplate.setWattage(wattage);
                    timeUsageTemplate.setHours(hours);
                    timeUsageTemplate.setMinutes(minutes);

                    initListTimeUsageTemplate();
                }

            }
        });

        alert.setNegativeButton("Cancel", null);
        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(timeUsageTemplate !=null){
                    timeUsageTemplateArrayList.remove(timeUsageTemplate);
                    initListTimeUsageTemplate();
                }
            }
        });

        FrameLayout frameAlert = new FrameLayout(v.getContext());
        frameAlert.addView(timeUsageLayoutDialog, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        alert.setView(frameAlert);
        alert.show();
    }

    private LinearLayout initTimeUsageLayoutDialog(View v, Spinner usageModeSpinner, NumberPicker wattageNumberPicker, NumberPicker hoursInPopupTimeUsage, NumberPicker minutesInPopupTimeUsage){

        List<UsageMode> usageModeList = dbConnection.getUsageModeList();
        ArrayAdapter<UsageMode> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, usageModeList);
        usageModeSpinner.setAdapter(dataAdapter);
        usageModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UsageMode usageMode = (UsageMode) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        wattageNumberPicker.setMinValue(0);
        wattageNumberPicker.setMaxValue(10000);
        wattageNumberPicker.setWrapSelectorWheel(false);

        hoursInPopupTimeUsage.setMinValue(0);
        hoursInPopupTimeUsage.setMaxValue(24);
        hoursInPopupTimeUsage.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        minutesInPopupTimeUsage.setMinValue(0);
        minutesInPopupTimeUsage.setMaxValue(60);
        minutesInPopupTimeUsage.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        TextView usageModeTextView = new TextView(v.getContext());
        TextView wattageTextView = new TextView(v.getContext());
        TextView hoursTextView  = new TextView(v.getContext());
        TextView minutesTextView = new TextView(v.getContext());

        usageModeTextView.setText(getString(R.string.mode) +getString(R.string.double_score));
        wattageTextView.setText(getString(R.string.watt)  +getString(R.string.double_score));
        hoursTextView.setText(getString(R.string.hours)  +getString(R.string.double_score));
        minutesTextView.setText(getString(R.string.minutes)  +getString(R.string.double_score));


        usageModeTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        wattageTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        hoursTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        minutesTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);


        LinearLayout.LayoutParams wrappingLayParam =  new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);

        LinearLayout mainLayoutTimeUsage = new LinearLayout(v.getContext());
        mainLayoutTimeUsage.setLayoutParams(wrappingLayParam);
        mainLayoutTimeUsage.setOrientation(LinearLayout.VERTICAL);
        mainLayoutTimeUsage.setPadding(10, 10, 10, 10);

        LinearLayout usageModeLayout = new LinearLayout(v.getContext());
        usageModeLayout.setLayoutParams(wrappingLayParam);
        usageModeLayout.setOrientation(LinearLayout.HORIZONTAL);
        usageModeLayout.setPadding(30, 10, 30, 10);

        LinearLayout wattageLayout = new LinearLayout(v.getContext());
        wattageLayout.setLayoutParams(wrappingLayParam);
        wattageLayout.setOrientation(LinearLayout.VERTICAL);
        wattageLayout.setPadding(30, 10, 30, 10);

        LinearLayout hoursLayout = new LinearLayout(v.getContext());
        hoursLayout.setLayoutParams(wrappingLayParam);
        hoursLayout.setOrientation(LinearLayout.VERTICAL);
        hoursLayout.setPadding(70, 10, 10, 10);

        LinearLayout minutesLayout = new LinearLayout(v.getContext());
        minutesLayout.setLayoutParams(wrappingLayParam);
        minutesLayout.setOrientation(LinearLayout.VERTICAL);
        minutesLayout.setPadding(0, 10, 10, 10);

        LinearLayout numberPickersLayout = new LinearLayout(v.getContext());
        numberPickersLayout.setLayoutParams(wrappingLayParam);
        numberPickersLayout.setOrientation(LinearLayout.HORIZONTAL);
        numberPickersLayout.setPadding(10, 10, 10, 10);

        usageModeLayout.addView(usageModeTextView, wrappingLayParam);
        usageModeLayout.addView(usageModeSpinner, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        mainLayoutTimeUsage.addView(usageModeLayout, wrappingLayParam);

        wattageLayout.addView(wattageTextView, wrappingLayParam);
        wattageLayout.addView(wattageNumberPicker, wrappingLayParam);
        numberPickersLayout.addView(wattageLayout, wrappingLayParam);

        hoursLayout.addView(hoursTextView, wrappingLayParam);
        hoursLayout.addView(hoursInPopupTimeUsage, wrappingLayParam);
        numberPickersLayout.addView(hoursLayout, wrappingLayParam);

        minutesLayout.addView(minutesTextView, wrappingLayParam);
        minutesLayout.addView(minutesInPopupTimeUsage, wrappingLayParam);
        numberPickersLayout.addView(minutesLayout, wrappingLayParam);


        mainLayoutTimeUsage.addView(numberPickersLayout, wrappingLayParam);

        return mainLayoutTimeUsage;
    }

}

class ElectronicTimeUsageTemplateListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<ElectronicTimeUsageTemplate> timeUsageList;
    private static LayoutInflater inflater=null;

    public ElectronicTimeUsageTemplateListAdapter(Activity activity, ArrayList<ElectronicTimeUsageTemplate> electronicTimeUsageTemplateList) {
        this.activity = activity;
        this.timeUsageList = electronicTimeUsageTemplateList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return timeUsageList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeUsageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_time_usage, null);

        TextView wattageInListTimeUsage = (TextView)view.findViewById(R.id.wattageInListTimeUsage);
        TextView usageModeNameInListTimeUsage = (TextView)view.findViewById(R.id.usageModeNameInListTimeUsage);
        TextView timeInListTimeUsage = (TextView)view.findViewById(R.id.timeInListTimeUsage);


        ElectronicTimeUsageTemplate electronicTimeUsageTemplate = timeUsageList.get(position);

        try {
            usageModeNameInListTimeUsage.setText(String.format("%s", electronicTimeUsageTemplate.getUsageMode().getUsageModeName()));
            wattageInListTimeUsage.setText(String.format("%s Watt", String.valueOf(electronicTimeUsageTemplate.getWattage())));
            timeInListTimeUsage.setText( String.valueOf(electronicTimeUsageTemplate.getHours())+ " : " + String.valueOf(electronicTimeUsageTemplate.getMinutes()));



        }catch (Exception e){
            Log.e("ListElectronic", e.getMessage());
        }

        return view;
    }



}


//DONE:Form init
//DONE:Edit button
//DONE:Delete button
//DONE:Save button
