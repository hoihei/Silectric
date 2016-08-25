package net.alaindonesia.silectric;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import net.alaindonesia.silectric.model.SilectricCurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Locale;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private double usageFeePerKwh;
    double basicChargeFee;
    private double otherFee;
    SilectricCurrency selectedSilectricCurrency;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.electricStaticFeeOptionsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("silectricPreferences", Context.MODE_PRIVATE);

        usageFeePerKwh = (double) sharedPreferences.getFloat("usage_fee_per_kwh", 0);
        basicChargeFee = (double)  sharedPreferences.getFloat("basic_fee", 0);
        otherFee = (double)  sharedPreferences.getFloat("others_fee", 0);
        String currencyCode = sharedPreferences.getString("currency_code", "USD");
        Currency currency = Currency.getInstance(currencyCode);
        this.selectedSilectricCurrency = new SilectricCurrency(currency);


        initFormOptions();
        initSaveElectricFeesFloatingButton();

    }

    private void initSaveElectricFeesFloatingButton() {

        ImageButton saveElectricFeeFloatingButton = (ImageButton) findViewById(R.id.saveElectricFeeButton);
        saveElectricFeeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editorSharedPref = sharedPreferences.edit();
                EditText usageFeePerKwhEditText  = (EditText) findViewById(R.id.usage_fee_per_kwh_edit_text);;
                EditText basicChargeFeeEditText  = (EditText) findViewById(R.id.basic_charge_fee_edit_text);
                EditText othersFeeEditText = (EditText) findViewById(R.id.other_fee_edit_text);
                SearchableSpinner currencyOptionsSpinner = (SearchableSpinner) findViewById(R.id.currencyOptionsSpinner);

                editorSharedPref.putBoolean("has_initiated", true);
                editorSharedPref.putFloat("usage_fee_per_kwh", Float.valueOf(usageFeePerKwhEditText.getText().toString()));
                editorSharedPref.putFloat("basic_fee", Float.valueOf(basicChargeFeeEditText.getText().toString()));
                editorSharedPref.putFloat("others_fee", Float.valueOf((othersFeeEditText.getText().toString())));
                SilectricCurrency silectricCurrency = (SilectricCurrency) currencyOptionsSpinner.getSelectedItem();
                editorSharedPref.putString("currency_code", silectricCurrency.getCurrency().getCurrencyCode());
                editorSharedPref.apply();

                setResult(Activity.RESULT_OK, new Intent());

                finish();
            }
        });


    }

    private static ArrayList<SilectricCurrency> getAllCurrencies()
    {

        LinkedHashSet<SilectricCurrency> silectricCurrencyLinkedHashSet = new LinkedHashSet<>();
        Locale[] locales = Locale.getAvailableLocales();

        for(Locale loc : locales) {
            try {
                Currency currency = Currency.getInstance(loc);
                silectricCurrencyLinkedHashSet.add(new SilectricCurrency(loc.getDisplayCountry(), currency));
            } catch(Exception exc)
            {
                // Locale not found
            }
        }
        ArrayList<SilectricCurrency> silectricCurrencies = new ArrayList<>();
        silectricCurrencies.addAll(silectricCurrencyLinkedHashSet);

        Collections.sort(silectricCurrencies);

        return silectricCurrencies;
    }

    private void initFormOptions(){

        EditText usageFeePerKwhEditText  = (EditText) findViewById(R.id.usage_fee_per_kwh_edit_text);;
        EditText basicChargeFeeEditText  = (EditText) findViewById(R.id.basic_charge_fee_edit_text);
        EditText othersFeeEditText = (EditText) findViewById(R.id.other_fee_edit_text);
        SearchableSpinner currencyOptionsSpinner = (SearchableSpinner) findViewById(R.id.currencyOptionsSpinner);

        ArrayList<SilectricCurrency> silectricCurrencies = getAllCurrencies();
        ArrayAdapter<SilectricCurrency> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, silectricCurrencies);
        currencyOptionsSpinner.setAdapter(dataAdapter);

        for (int i =0; i < silectricCurrencies.size(); i++) {
            String currencyCode1 = silectricCurrencies.get(i).getCurrency().getCurrencyCode();
            String currencyCode2 = selectedSilectricCurrency.getCurrency().getCurrencyCode();
            if (currencyCode1 == currencyCode2)
                currencyOptionsSpinner.setSelection(i);
        }

        usageFeePerKwhEditText.setText(String.valueOf(usageFeePerKwh));
        basicChargeFeeEditText.setText(String.valueOf(basicChargeFee));
        othersFeeEditText.setText(String.valueOf(otherFee));

    }

}


