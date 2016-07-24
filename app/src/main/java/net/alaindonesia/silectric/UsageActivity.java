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
import android.widget.EditText;
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
import net.alaindonesia.silectric.model.Usage;
import net.alaindonesia.silectric.model.TimeUsage;
import net.alaindonesia.silectric.model.UsageMode;

import java.util.ArrayList;
import java.util.List;

public class UsageActivity extends AppCompatActivity {

    private Usage usage;
    private DbConnection dbConnection;
    private boolean isNewUsage=false;
    private ArrayList<TimeUsage> timeUsageArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.usageToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbConnection = new DbConnection(this);

        ImageButton deleteUsageButton =  (ImageButton) findViewById(R.id.deleteUsageButton);

        usage = getIntent().getParcelableExtra("usage");
        if (usage == null){
            usage = new Usage(dbConnection.getDefaultElectronic(), 1);
            isNewUsage = true;
            deleteUsageButton.setVisibility(View.INVISIBLE);
        }else deleteUsageButton.setVisibility(View.VISIBLE);

        initUsageForm();
        initSaveButton();
        initDeleteFloatingButton();

    }

    private void initUsageForm() {
        SearchableSpinner electronicNameSpinner = (SearchableSpinner) findViewById(R.id.electronicNameSpinner);
        ImageButton addTimeUsageButton = (ImageButton) findViewById(R.id.addTimeUsageButton);

        EditText numberOfElectronicTextView = (EditText) findViewById(R.id.numberOfElectronicTextView);
        ArrayList<Electronic> electronicList = dbConnection.getElectronicList();

        timeUsageArrayList = dbConnection.getTimeUsagesByIdUsage(usage.getIdUsage());
        initElectronicSpinner(electronicNameSpinner, electronicList);

        initNumberOfElectronicNumberPicker(numberOfElectronicTextView);

        initListTimeUsage();

        int numberOfElectronic = usage.getNumberOfElectronic();
        numberOfElectronicTextView.setText(String.valueOf(numberOfElectronic));

        addTimeUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpTimeUsageDialog(v, true, null);
            }
        });

    }

    private void initNumberOfElectronicNumberPicker(final EditText numberOfElectronicTextView) {
        numberOfElectronicTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final NumberPicker np = new NumberPicker(v.getContext());

                np.setMinValue(1);
                np.setMaxValue(10000);
                np.setWrapSelectorWheel(false);
                np.setValue(usage.getNumberOfElectronic());

                alert.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        np.clearFocus();
                        numberOfElectronicTextView.setText(String.valueOf(np.getValue()));
                        usage.setNumberOfElectronic(np.getValue());
                    }
                });

                final FrameLayout parent = new FrameLayout(v.getContext());
                parent.addView(np, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                alert.setView(parent);
                alert.show();
            }
        });
    }

    private void initElectronicSpinner(SearchableSpinner electronicNameSpinner, List<Electronic> electronicList){

        ArrayAdapter<Electronic> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, electronicList);
        electronicNameSpinner.setAdapter(dataAdapter);


        electronicNameSpinner.setTitle("Select Electronic Type");
        electronicNameSpinner.setPositiveButton("Ok");

        electronicNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Electronic electronic = (Electronic) parent.getItemAtPosition(position);
                if (isNewUsage) {
                    ArrayList<ElectronicTimeUsageTemplate> timeUsageTemplateArrayList = dbConnection.getElectronicTimeUsageTemplateByIdElectronic(electronic.getIdElectronic());
                    timeUsageArrayList.clear();

                    for (ElectronicTimeUsageTemplate e : timeUsageTemplateArrayList) {
                        timeUsageArrayList.add(new TimeUsage(isNewUsage, e.getIdUsageMode(), e.getWattage(), e.getHours(), e.getMinutes(), e.getUsageMode()));
                    }
                    initListTimeUsage();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        Electronic electronicInUsage = usage.getElectronic();

        for (int i = 0; i < electronicList.size(); i ++) {
            if(electronicInUsage.getIdElectronic() == electronicList.get(i).getIdElectronic()) {
                electronicNameSpinner.setSelection(i);
            }
        }

    }

    private void initSaveButton(){

        ImageButton saveUsageButton = (ImageButton) findViewById(R.id.saveUsageButton);

        saveUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner electronicNameSpinner = (Spinner) findViewById(R.id.electronicNameSpinner);
                Electronic electronic = (Electronic) electronicNameSpinner.getSelectedItem();
                usage.setElectronic(electronic);

                double totalUsageHoursPerDay=0;
                int totalWattagePerDay=0;
                for(TimeUsage timeUsage : timeUsageArrayList){
                    double hours = timeUsage.getHours() + (timeUsage.getMinutes()/60);
                    totalUsageHoursPerDay = totalUsageHoursPerDay + hours;
                    totalWattagePerDay = (int)( totalWattagePerDay + (hours * timeUsage.getWattage()));
                }

                usage.setTotalUsageHoursPerDay(totalUsageHoursPerDay);
                usage.setTotalWattagePerDay(totalWattagePerDay);

                if (isNewUsage) {
                    dbConnection.addUsage(usage, timeUsageArrayList);
                } else {

                    dbConnection.editUsage(usage, timeUsageArrayList);
                }


                finish();


            }
        });

    }

    private void initDeleteFloatingButton(){
        ImageButton deleteUsageButton = (ImageButton) findViewById(R.id.deleteUsageButton);

        deleteUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnection.deleteUsage(usage);
                finish();
            }
        });


//        }
    }

    private void initListTimeUsage(){
        ListView listUsageListView = (ListView)findViewById(R.id.timeUsageListView);

        ListAdapter timeUsageListAdapter = new TimeUsageListAdapter(this, timeUsageArrayList);
        listUsageListView.setAdapter(timeUsageListAdapter);
        listUsageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TimeUsage timeUsage = (TimeUsage) parent.getItemAtPosition(position);
                popUpTimeUsageDialog(view, false, timeUsage);
            }

        });


    }

    private void popUpTimeUsageDialog(View v, final boolean isNewTimeUsage, final TimeUsage timeUsage){

        final Spinner usageModeSpinner = new Spinner(v.getContext());
        final NumberPicker wattageNumberPicker = new NumberPicker(v.getContext());
        final NumberPicker hoursInPopupTimeUsage = new NumberPicker(v.getContext());
        final NumberPicker minutesInPopupTimeUsage = new NumberPicker(v.getContext());

        LinearLayout timeUsageLayoutDialog = initTimeUsageLayoutDialog(v,
                usageModeSpinner, wattageNumberPicker, hoursInPopupTimeUsage,
                minutesInPopupTimeUsage);

        String  okButtonString;
        if (isNewTimeUsage){
            okButtonString = "Add";
        }else{
            okButtonString = "Save";
            wattageNumberPicker.setValue(timeUsage.getWattage());
            hoursInPopupTimeUsage.setValue(timeUsage.getHours());
            minutesInPopupTimeUsage.setValue(timeUsage.getMinutes());

            ArrayList<UsageMode> usageModeArrayList = dbConnection.getUsageModeList();
            int usageModeSpinnerPos = 0;
            for (UsageMode usageModeInList : usageModeArrayList){
                if(timeUsage.getIdUsageMode() == usageModeInList.getIdUsageMode())
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

                if (isNewTimeUsage) {
                    timeUsageArrayList.add(new TimeUsage(isNewUsage, idUsageMode, wattage, hours, minutes, usageMode));
                    initListTimeUsage();
                }else {

                    timeUsage.setIdUsageMode(idUsageMode);
                    timeUsage.setWattage(wattage);
                    timeUsage.setHours(hours);
                    timeUsage.setMinutes(minutes);

                    initListTimeUsage();
                }

            }
        });

        alert.setNegativeButton("Cancel", null);
        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(timeUsage !=null){
                    timeUsageArrayList.remove(timeUsage);
                    initListTimeUsage();
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

        usageModeTextView.setText("Mode : ");
        wattageTextView.setText("Watt : ");
        hoursTextView.setText("Hours : ");
        minutesTextView.setText("Minutes : ");

//        if (Build.VERSION.SDK_INT < 23) {
//            usageModeTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
//            wattageTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
//            hoursTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
//            minutesTextView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
//        }else{
//            usageModeTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
//            wattageTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
//            hoursTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
//            minutesTextView.setTextAppearance(android.R.style.TextAppearance_Medium);
//        }


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

class TimeUsageListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<TimeUsage> timeUsageList;
    private static LayoutInflater inflater=null;

    public TimeUsageListAdapter(Activity activity, ArrayList<TimeUsage> timeUsageList) {
        this.activity = activity;
        this.timeUsageList = timeUsageList;
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


        TimeUsage timeUsage = timeUsageList.get(position);

        try {
            usageModeNameInListTimeUsage.setText(String.format("%s", timeUsage.getUsageMode().getUsageModeName()));
            wattageInListTimeUsage.setText(String.format("%s Watt", String.valueOf(timeUsage.getWattage())));
            timeInListTimeUsage.setText( String.valueOf(timeUsage.getHours())+ " : " + String.valueOf(timeUsage.getMinutes()));



        }catch (Exception e){
            Log.e("ListElectronic", e.getMessage());
        }

        return view;
    }



}
