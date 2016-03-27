package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.Elektronik;
import net.alaindonesia.simulatortagihanlistrik.model.Pemakaian;

import java.util.List;

public class PemakaianActivity extends AppCompatActivity {

    private final Context context = this;
    Pemakaian pemakaian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemakaian);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.pemakaian = getIntent().getParcelableExtra("pemakaian");

        initPemakaianForm();
        initSimpanFloatingButton();
        initDeleteFloatingButton();

    }

    private void initPemakaianForm() {
        Spinner namaElektronikSpinner = (Spinner) findViewById(R.id.namaElektronikSpinner);
        TextView dayaWattTextView = (TextView) findViewById(R.id.dayaWattTextView);
        TextView jumlahPemakaianJamTextView = (TextView) findViewById(R.id.jumlahPemakaianJamTextView);
        TextView jumlahBarangTextView = (TextView) findViewById(R.id.jumlahBarangTextView);

        List<Elektronik> elektronikList = getIntent().getParcelableArrayListExtra("elektronikList");
        initElektronikSpinner(namaElektronikSpinner, elektronikList);


        if(this.pemakaian != null){
            Elektronik elektronik = this.pemakaian.getElektronik();
            double jumlahPemakaianJam = this.pemakaian.getJumlahPemakaianJam();
            int jumlahBarang = this.pemakaian.getJumlahBarang();


            dayaWattTextView.setText(String.valueOf(elektronik.getDayaWatt()));
            jumlahPemakaianJamTextView.setText(String.valueOf(jumlahPemakaianJam));
            jumlahBarangTextView.setText(String.valueOf(jumlahBarang));

            int elektronikSpinnerPosition = 0;
            for (Elektronik elektronikInLIst : elektronikList){
                if(elektronik.getIdElektronik() == elektronikInLIst.getIdElektronik())
                    break;
                elektronikSpinnerPosition++;
            }
            namaElektronikSpinner.setSelection(elektronikSpinnerPosition);

        }

    }

    private void initElektronikSpinner(Spinner namaElektronikSpinner, List<Elektronik> elektronikList){
        //DONE: Spinner elektronik wih sub item watt

        ArrayAdapter<Elektronik> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, elektronikList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        namaElektronikSpinner.setAdapter(dataAdapter);


        namaElektronikSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Elektronik elektronik = (Elektronik) parent.getItemAtPosition(position);
                TextView dayaWattTextView = (TextView) findViewById(R.id.dayaWattTextView);
                dayaWattTextView.setText(String.valueOf(elektronik.getDayaWatt()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void initSimpanFloatingButton(){

        FloatingActionButton simpanFloatingButton = (FloatingActionButton) findViewById(R.id.simpan_pemakaian_floating_button);
        simpanFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView jumlahBarangTextView = (TextView) findViewById(R.id.jumlahBarangTextView);
                TextView jumlahPemakaianJamTextView = (TextView) findViewById(R.id.jumlahPemakaianJamTextView);
                Spinner namaElektronikSpinner = (Spinner) findViewById(R.id.namaElektronikSpinner);


                int jumlahBarang = Integer.parseInt(jumlahBarangTextView.getText().toString());
                double jumlahPemakaianJam = Double.parseDouble(jumlahPemakaianJamTextView.getText().toString());
                Elektronik elektronik = (Elektronik) namaElektronikSpinner.getSelectedItem();

                Pemakaian pemakaian = getIntent().getParcelableExtra("pemakaian");
                if (pemakaian == null) {
                    pemakaian = new Pemakaian();
                }
                pemakaian.setJumlahPemakaianJam(jumlahPemakaianJam);
                pemakaian.setElektronik(elektronik);
                pemakaian.setJumlahBarang(jumlahBarang);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("pemakaian", pemakaian);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();


            }
        });

    }

    private void initDeleteFloatingButton(){
        FloatingActionButton deleteFloatingButton = (FloatingActionButton) findViewById(R.id.hapus_floating_button);
        if(this.pemakaian == null){
            deleteFloatingButton.hide();
        }else{

            deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("pemakaian", pemakaian);
                    setResult(MainActivity.RESULT_ACTIVITY_DELETE_PEMAKAIAN, resultIntent);
                    finish();
                }
            });


        }
    }



}
