package net.alaindonesia.simulatortagihanlistrik;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import net.alaindonesia.simulatortagihanlistrik.model.DatabaseSimulatorPLN;
import net.alaindonesia.simulatortagihanlistrik.model.Elektronik;
import net.alaindonesia.simulatortagihanlistrik.model.Pemakaian;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class PemakaianActivity extends AppCompatActivity {

    private final Context context = this;
    private Pemakaian pemakaian;
    Calendar lamaPemakaianTime;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private DatabaseSimulatorPLN databaseSimulatorPLN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemakaian);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseSimulatorPLN = new DatabaseSimulatorPLN(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pemakaian = getIntent().getParcelableExtra("pemakaian");
        if (pemakaian == null){
            Elektronik defaultElektronik = databaseSimulatorPLN.getDefaultElektronik();
            pemakaian = new Pemakaian(defaultElektronik, 0, 1);
            int lastInsertedId = (int) databaseSimulatorPLN.addPemakaian(pemakaian);
            pemakaian.setIdPemakaian(lastInsertedId);

        }

        lamaPemakaianTime = Calendar.getInstance();

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
        initJumlahPemakaianTimePicker(jumlahPemakaianJamTextView);
        initJumlahBarangNumberPicker(jumlahBarangTextView);

        Elektronik elektronik = pemakaian.getElektronik();
        double jumlahPemakaianJam = pemakaian.getJumlahPemakaianJam();
        int jumlahBarang = pemakaian.getJumlahBarang();
        int jumlahPemakaianMenit = (int) ((jumlahPemakaianJam - Math.floor(jumlahPemakaianJam)) * 60) ;
        lamaPemakaianTime.set(0, 0, 0, (int) jumlahPemakaianJam, jumlahPemakaianMenit);
        jumlahPemakaianJamTextView.setText(sdf.format(lamaPemakaianTime.getTime()));
        dayaWattTextView.setText(String.valueOf(elektronik.getDayaWatt()));
        jumlahBarangTextView.setText(String.valueOf(jumlahBarang));

        int elektronikSpinnerPosition = 0;
        for (Elektronik elektronikInLIst : elektronikList){
            if(elektronik.getIdElektronik() == elektronikInLIst.getIdElektronik())
                break;
            elektronikSpinnerPosition++;
        }
        namaElektronikSpinner.setSelection(elektronikSpinnerPosition);

    }

    private void initJumlahBarangNumberPicker(final TextView jumlahBarangTextView) {
        jumlahBarangTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);

                alert.setTitle("Jumlah Barang: ");


                final NumberPicker np = new NumberPicker(context);


                np.setMinValue(1);
                np.setMaxValue(10000);
                np.setWrapSelectorWheel(false);
                np.setValue(pemakaian.getJumlahBarang());



                alert.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        jumlahBarangTextView.setText(String.valueOf(np.getValue()));
                        pemakaian.setJumlahBarang(np.getValue());
                        databaseSimulatorPLN.savePemakaian(pemakaian);
                    }
                });

                final FrameLayout parent = new FrameLayout(context);
                parent.addView(np, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                alert.setView(parent);


                alert.show();
            }
        });
    }

    private void initJumlahPemakaianTimePicker(final TextView jumlahPemakaianJamTextView) {

        jumlahPemakaianJamTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            TimePickerDialog mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    lamaPemakaianTime.set(0, 0, 0, selectedHour, selectedMinute);
                    jumlahPemakaianJamTextView.setText(sdf.format(lamaPemakaianTime.getTime()));

                    double jumlahPemakaianJam = (double) selectedHour + (double) selectedMinute / 60;
                    pemakaian.setJumlahPemakaianJam(jumlahPemakaianJam);
                    databaseSimulatorPLN.savePemakaian(pemakaian);
                }
            }, lamaPemakaianTime.get(Calendar.HOUR_OF_DAY), lamaPemakaianTime.get(Calendar.MINUTE), true);//Yes 24 hour time

            mTimePicker.setCancelable(true);
            mTimePicker.setButton(TimePickerDialog.BUTTON_POSITIVE, "Simpan", mTimePicker);
            mTimePicker.setTitle("Lama Pemakaian Sehari");
            mTimePicker.show();

            }
        });

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
//                TextView jumlahPemakaianJamTextView = (TextView) findViewById(R.id.jumlahPemakaianJamTextView);
                Spinner namaElektronikSpinner = (Spinner) findViewById(R.id.namaElektronikSpinner);


                int jumlahBarang = Integer.parseInt(jumlahBarangTextView.getText().toString());
                Elektronik elektronik = (Elektronik) namaElektronikSpinner.getSelectedItem();

                pemakaian.setElektronik(elektronik);
                pemakaian.setJumlahBarang(jumlahBarang);


                finish();


            }
        });

    }

    private void initDeleteFloatingButton(){
        FloatingActionButton deleteFloatingButton = (FloatingActionButton) findViewById(R.id.hapus_floating_button);
//        if(this.pemakaian == null){
//            deleteFloatingButton.hide();
//        }else{

            deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("pemakaian", pemakaian);
//                    setResult(MainActivity.RESULT_ACTIVITY_DELETE_PEMAKAIAN, resultIntent);
                    databaseSimulatorPLN.deletePemakaian(pemakaian);
                    finish();
                }
            });


//        }
    }



}
