package net.alaindonesia.silectric;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.DecimalFormat;

public class ElectricStaticFeeOptionsActivity extends AppCompatActivity {

    private SharedPreferences silentricPreferences;

    double usageFeePerKwh;
    double basicChargeFee;
    double otherFee;

    EditText usageFeePerKwhEditText;
    EditText basicChargeFeeEditText;
    EditText othersFeeEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_price);
        Toolbar toolbar = (Toolbar) findViewById(R.id.electricStaticFeeOptionsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        silentricPreferences = getSharedPreferences("silentricPreferences", Context.MODE_PRIVATE);

        usageFeePerKwh = (double) silentricPreferences.getFloat("biaya_usage_per_kwh", 0);
        basicChargeFee = (double)  silentricPreferences.getFloat("biaya_beban", 0);
        otherFee = (double)  silentricPreferences.getFloat("biaya_lainnya", 0);

        usageFeePerKwhEditText = (EditText) findViewById(R.id.usage_fee_per_kwh_edit_text);
        basicChargeFeeEditText = (EditText) findViewById(R.id.basic_charge_fee_edit_text);
        othersFeeEditText = (EditText) findViewById(R.id.biaya_lainnya_edit_text);

        initFormPengaturanBiayaListrik();
        initSimpanBiayaListrikFloatingButton();

    }

    private void initSimpanBiayaListrikFloatingButton() {
        ImageButton simpanBiayaListrikFloatingButton = (ImageButton) findViewById(R.id.saveElectricFeeButton);
        simpanBiayaListrikFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor biayaListrikEditPref = silentricPreferences.edit();
                biayaListrikEditPref.putBoolean("has_initiated", true);
                biayaListrikEditPref.putFloat("biaya_usage_per_kwh", Float.valueOf(usageFeePerKwhEditText.getText().toString()));
                biayaListrikEditPref.putFloat("biaya_beban", Float.valueOf(basicChargeFeeEditText.getText().toString()));
                biayaListrikEditPref.putFloat("biaya_lainnya", Float.valueOf((othersFeeEditText.getText().toString())));
                biayaListrikEditPref.apply();

                setResult(Activity.RESULT_OK, new Intent());

                finish();
            }
        });


    }

    private void initFormPengaturanBiayaListrik(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setGroupingUsed(false);

        usageFeePerKwhEditText.setText(String.valueOf(df.format(usageFeePerKwh)));
        basicChargeFeeEditText.setText(String.valueOf(df.format(basicChargeFee)));
        othersFeeEditText.setText(String.valueOf(df.format(otherFee)));

    }

}
