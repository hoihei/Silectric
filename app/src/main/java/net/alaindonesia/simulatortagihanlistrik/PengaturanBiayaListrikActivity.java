package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.text.DecimalFormat;

public class PengaturanBiayaListrikActivity extends AppCompatActivity {

    private SharedPreferences biayaListrikPreferences;

    double biayaPemakaianPerKwh ;
    double biayaBeban;
    double biayaLainnya;

    EditText biayaPemakaianPerKwhEditText;
    EditText biayaBebanEditText;
    EditText biayaLainnyaEditText ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_biaya_listrik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        biayaListrikPreferences = getSharedPreferences("biayaListrikPreferences", Context.MODE_PRIVATE);

        biayaPemakaianPerKwh = (double) biayaListrikPreferences.getFloat("biaya_pemakaian_per_kwh", 0);
        biayaBeban = (double)  biayaListrikPreferences.getFloat("biaya_beban", 0);
        biayaLainnya = (double)  biayaListrikPreferences.getFloat("biaya_lainnya", 0);

        biayaPemakaianPerKwhEditText = (EditText) findViewById(R.id.biaya_pemakaian_per_kwh_edit_text);
        biayaBebanEditText = (EditText) findViewById(R.id.biaya_beban_edit_text);
        biayaLainnyaEditText = (EditText) findViewById(R.id.biaya_lainnya_edit_text);

        initFormPengaturanBiayaListrik();
        initSimpanBiayaListrikFloatingButton();

    }

    private void initSimpanBiayaListrikFloatingButton() {
        FloatingActionButton simpanBiayaListrikFloatingButton = (FloatingActionButton) findViewById(R.id.simpan_biaya_listrik_floating_button);
        simpanBiayaListrikFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
                biayaListrikEditPref.putBoolean("has_initiated", true);
                biayaListrikEditPref.putFloat("biaya_pemakaian_per_kwh", Float.valueOf(biayaPemakaianPerKwhEditText.getText().toString()));
                biayaListrikEditPref.putFloat("biaya_beban", Float.valueOf(biayaBebanEditText.getText().toString()));
                biayaListrikEditPref.putFloat("biaya_lainnya", Float.valueOf((biayaLainnyaEditText.getText().toString())));
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

        biayaPemakaianPerKwhEditText.setText(String.valueOf(df.format(biayaPemakaianPerKwh)));
        biayaBebanEditText.setText(String.valueOf(df.format(biayaBeban)));
        biayaLainnyaEditText.setText(String.valueOf(df.format(biayaLainnya)));

    }

}
