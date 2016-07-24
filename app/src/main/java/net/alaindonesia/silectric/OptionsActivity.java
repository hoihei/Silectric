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

import net.alaindonesia.silectric.model.SilentricCurrency;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.Locale;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences silentricPrefereces;

    double usageFeePerKwh;
    double basicChargeFee;
    double otherFee;
    SilentricCurrency selectedSilentricCurrency;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.electricStaticFeeOptionsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        silentricPrefereces = getSharedPreferences("silentricPrefereces", Context.MODE_PRIVATE);

        usageFeePerKwh = (double) silentricPrefereces.getFloat("usage_fee_per_kwh", 0);
        basicChargeFee = (double)  silentricPrefereces.getFloat("basic_fee", 0);
        otherFee = (double)  silentricPrefereces.getFloat("others_fee", 0);
        String currencyCode = silentricPrefereces.getString("currency_code", Currency.getInstance( getResources().getConfiguration().locale).getCurrencyCode());
        Currency currency = Currency.getInstance(currencyCode);
        this.selectedSilentricCurrency = new SilentricCurrency(currency);


        initFormOptions();
        initSaveElectricFeesFloatingButton();

    }

    private void initSaveElectricFeesFloatingButton() {

        ImageButton saveElectricFeeFloatingButton = (ImageButton) findViewById(R.id.saveElectricFeeButton);
        saveElectricFeeFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editorSharedPref = silentricPrefereces.edit();
                EditText usageFeePerKwhEditText  = (EditText) findViewById(R.id.usage_fee_per_kwh_edit_text);;
                EditText basicChargeFeeEditText  = (EditText) findViewById(R.id.basic_charge_fee_edit_text);
                EditText othersFeeEditText = (EditText) findViewById(R.id.other_fee_edit_text);
                SearchableSpinner currencyOptionsSpinner = (SearchableSpinner) findViewById(R.id.currencyOptionsSpinner);

                editorSharedPref.putBoolean("has_initiated", true);
                editorSharedPref.putFloat("usage_fee_per_kwh", Float.valueOf(usageFeePerKwhEditText.getText().toString()));
                editorSharedPref.putFloat("basic_fee", Float.valueOf(basicChargeFeeEditText.getText().toString()));
                editorSharedPref.putFloat("others_fee", Float.valueOf((othersFeeEditText.getText().toString())));
                SilentricCurrency silentricCurrency = (SilentricCurrency) currencyOptionsSpinner.getSelectedItem();
                editorSharedPref.putString("currency_code", silentricCurrency.getCurrency().getCurrencyCode());
                editorSharedPref.apply();

                setResult(Activity.RESULT_OK, new Intent());

                finish();
            }
        });


    }

    public static ArrayList<SilentricCurrency> getAllCurrencies()
    {

        LinkedHashSet<SilentricCurrency> silentricCurrencyLinkedHashSet = new LinkedHashSet<>();
        Locale[] locales = Locale.getAvailableLocales();

        for(Locale loc : locales) {
            try {
                Currency currency = Currency.getInstance(loc);
                silentricCurrencyLinkedHashSet.add( new SilentricCurrency(loc.getDisplayCountry(), currency) );
            } catch(Exception exc)
            {
                // Locale not found
            }
        }
        ArrayList<SilentricCurrency> silentricCurrencies = new ArrayList<>();
        silentricCurrencies.addAll(silentricCurrencyLinkedHashSet);

        Collections.sort(silentricCurrencies);

        return silentricCurrencies;
    }

    private void initFormOptions(){

        EditText usageFeePerKwhEditText  = (EditText) findViewById(R.id.usage_fee_per_kwh_edit_text);;
        EditText basicChargeFeeEditText  = (EditText) findViewById(R.id.basic_charge_fee_edit_text);
        EditText othersFeeEditText = (EditText) findViewById(R.id.other_fee_edit_text);
        SearchableSpinner currencyOptionsSpinner = (SearchableSpinner) findViewById(R.id.currencyOptionsSpinner);

        ArrayList<SilentricCurrency> silentricCurrencies = getAllCurrencies();
        ArrayAdapter<SilentricCurrency> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, silentricCurrencies);
        currencyOptionsSpinner.setAdapter(dataAdapter);

        for (int i =0; i < silentricCurrencies.size(); i++) {
            String currencyCode1 = silentricCurrencies.get(i).getCurrency().getCurrencyCode();
            String currencyCode2 = selectedSilentricCurrency.getCurrency().getCurrencyCode();
            if (currencyCode1 == currencyCode2)
                currencyOptionsSpinner.setSelection(i);
        }
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setGroupingUsed(false);

        usageFeePerKwhEditText.setText(String.valueOf(df.format(usageFeePerKwh)));
        basicChargeFeeEditText.setText(String.valueOf(df.format(basicChargeFee)));
        othersFeeEditText.setText(String.valueOf(df.format(otherFee)));

    }

}


