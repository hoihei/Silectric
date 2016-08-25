package net.alaindonesia.silectric;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import net.alaindonesia.silectric.model.DbConnection;
import net.alaindonesia.silectric.model.Usage;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Currency;

public class MainActivity extends AppCompatActivity {

    private final int USAGE_ACTIVITY_REQ = 1;
    private final int ELECTRIC_FEE_ACTIVITY_REQ = 3;
    private SharedPreferences silectricPreferences;
    private DbConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        silectricPreferences = getSharedPreferences("silectricPreferences", Context.MODE_PRIVATE);

        dbConnection = new DbConnection(this, getResources().openRawResource(R.raw.initial_data));

        checkPreferences();
        initInputDays();
        initAddUsageButton();
        initListUsage();
        calculateTotal();

    }

    private void calculateTotal() {
        EditText daysEditText = (EditText) findViewById(R.id.daysEditText);
        int days = Integer.parseInt(daysEditText.getText().toString());

        double totalWattDaily = dbConnection.getTotalWattDaily();
        double totalKwh = totalWattDaily * days / 1000; //divide 1000 for convert Watt to KWatt

        DecimalFormat kwhFormat = (DecimalFormat) DecimalFormat.getNumberInstance();
//        kwhFormat.applyPattern("###,###.##");
//        kwhFormat.setDecimalSeparatorAlwaysShown(false);
        TextView totalElectricUsageTextView = (TextView) findViewById(R.id.totalElectricUsageTextView);
        String totalKwHString = kwhFormat.format(totalKwh) + " KwH ";
        totalElectricUsageTextView.setText(totalKwHString);

        double feeUsagePerKwh = (double) silectricPreferences.getFloat("usage_fee_per_kwh", 0.2f);
        double feeBase = (double) silectricPreferences.getFloat("basic_fee", 0);
        double feeOthers = (double) silectricPreferences.getFloat("others_fee", 0);

        double totalMonthlyUsage = totalKwh * feeUsagePerKwh;
        totalMonthlyUsage = totalMonthlyUsage + feeBase + feeOthers;

        String currencyCode = silectricPreferences.getString("currency_code", "USD");
        Currency currency = Currency.getInstance(currencyCode);

        DecimalFormat monthlyFeeFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(getResources().getConfiguration().locale);
        DecimalFormatSymbols monthlyFeeFormatSymbols = new DecimalFormatSymbols();
        monthlyFeeFormatSymbols.setCurrencySymbol(currency.getSymbol());
//        monthlyFeeFormatSymbols.setMonetaryDecimalSeparator(',');
//        monthlyFeeFormatSymbols.setGroupingSeparator('.');
        monthlyFeeFormat.setDecimalFormatSymbols(monthlyFeeFormatSymbols);
//        monthlyFeeFormat.setDecimalSeparatorAlwaysShown(false);
        TextView totalElectricFeeTextView = (TextView) findViewById(R.id.totalElectricFeeTextView);
        String totalMonthlyFeeString = monthlyFeeFormat.format(totalMonthlyUsage);
        totalElectricFeeTextView.setText(totalMonthlyFeeString);

    }

    private void initInputDays() {

        final EditText daysEditText = (EditText) findViewById(R.id.daysEditText);
        int daysInPreferences = silectricPreferences.getInt("number_of_days", 30);
        daysEditText.setText(String.valueOf(daysInPreferences));


        daysEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int days;
                days = Integer.parseInt(daysEditText.getText().toString());


                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Number of Days: ");
                final NumberPicker np = new NumberPicker(v.getContext());

                np.setMinValue(1);
                np.setMaxValue(10000);
                np.setWrapSelectorWheel(false);
                np.setValue(days);

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        int days = newVal;
                        daysEditText.setText(String.valueOf(days));

                        SharedPreferences.Editor editPref = silectricPreferences.edit();
                        editPref.putInt("number_of_days", days);
                        editPref.apply();

                        calculateTotal();

                    }
                });
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        int days = np.getValue();
                        daysEditText.setText(String.valueOf(days));

                        SharedPreferences.Editor editPref = silectricPreferences.edit();
                        editPref.putInt("number_of_days", days);
                        editPref.apply();

                        calculateTotal();
                    }
                });


                final FrameLayout parent = new FrameLayout(v.getContext());
                parent.addView(np, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                alert.setView(parent);
                alert.show().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                ;
            }
        });

    }

    private void initAddUsageButton() {
        ImageButton addUsageButton = (ImageButton) findViewById(R.id.addUsageButton);
        addUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usageActivity = new Intent(view.getContext(), UsageActivity.class);
                Usage usage = null;
                usageActivity.putExtra("usage", usage);
                startActivityForResult(usageActivity, USAGE_ACTIVITY_REQ);
            }
        });
    }

    private void initListUsage() {
        ArrayList<Usage> usageList = dbConnection.getUsageList();

        ListView list = (ListView) findViewById(R.id.usageListView);

        ListAdapter adapter = new UsageListAdapter(this, usageList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent usageActivity = new Intent(view.getContext(), UsageActivity.class);
                Usage usage = (Usage) parent.getItemAtPosition(position);
                usageActivity.putExtra("usage", usage);

                startActivityForResult(usageActivity, USAGE_ACTIVITY_REQ);
            }

        });

    }

    private void checkPreferences() {

        boolean hasInitiated = silectricPreferences.getBoolean("has_initiated", false);
        if (!hasInitiated) {

            SharedPreferences.Editor editorSharedPref = silectricPreferences.edit();
            editorSharedPref.putBoolean("has_initiated", true);
            editorSharedPref.putFloat("usage_fee_per_kwh", 0.20f);
            editorSharedPref.putFloat("basic_fee", 0);
            editorSharedPref.putFloat("others_fee", 0);
            editorSharedPref.putInt("number_of_days", 30);
            editorSharedPref.putString("currency_code", Currency.getInstance(getResources().getConfiguration().locale).getCurrencyCode());
            editorSharedPref.apply();

            Intent intent = new Intent(this, OptionsActivity.class);
            startActivityForResult(intent, ELECTRIC_FEE_ACTIVITY_REQ);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        initListUsage();
        calculateTotal();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_electric_price) {

            Intent intent = new Intent(this, OptionsActivity.class);
            startActivityForResult(intent, ELECTRIC_FEE_ACTIVITY_REQ);

            return true;
        } else if (id == R.id.action_electronic_type) {

            Intent intent = new Intent(this, ElectronicListActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


class UsageListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Usage> usageList;
    private static LayoutInflater inflater = null;

    public UsageListAdapter(Activity activity, ArrayList<Usage> usageList) {
        this.activity = activity;
        this.usageList = usageList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return usageList.size();
    }

    @Override
    public Object getItem(int position) {
        return usageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null)
            view = inflater.inflate(R.layout.list_usage, null);


        TextView nameElectronicOnListRow = (TextView) view.findViewById(R.id.electronicNameOnListRow);
        TextView totalWattagePerDayOnListRow = (TextView) view.findViewById(R.id.totalWattagePerDayOnListRow);
        TextView totalUsageHoursPerDayOnListRow = (TextView) view.findViewById(R.id.totalUsageHoursPerDayOnListRow);
        TextView numberOfElectronicOnListRow = (TextView) view.findViewById(R.id.numberOfElectronicOnListRow);

        Usage usage = usageList.get(position);

        try {
            nameElectronicOnListRow.setText(usage.getElectronic().getElectronicName());
            totalWattagePerDayOnListRow.setText(String.valueOf(usage.getTotalWattagePerDay()) + " watt");
            totalUsageHoursPerDayOnListRow.setText(String.valueOf(usage.getTotalUsageHoursPerDay()) + " hours");
            numberOfElectronicOnListRow.setText(String.valueOf(usage.getNumberOfElectronic()) + " electronics");
        } catch (Exception e) {
            Log.e("MAinActivity", e.getMessage());
        }

        return view;
    }

}

