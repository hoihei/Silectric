package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.Elektronik;

public class ElektronikActivity extends AppCompatActivity {
    //DONE:Edit button
    //DONE:Delete button
    //DONE:Save button

    private Elektronik elektronik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elektronik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.elektronik = getIntent().getParcelableExtra("elektronik");

        initForm();
        initDeleteFloatingButton();
        initSimpanFloatingButton();
    }

    private void initForm(){
        TextView namaElektronikInElektronikForm = (TextView) findViewById(R.id.namaElektronikInElektronikForm);
        TextView dayaWattInElektronikForm = (TextView) findViewById(R.id.dayaWattInElektronikForm);


        if(this.elektronik != null){

            namaElektronikInElektronikForm.setText(String.valueOf(this.elektronik.getNamaElektronik()));
            dayaWattInElektronikForm.setText(String.valueOf(this.elektronik.getDayaWatt()));

        }
    }

    private void initSimpanFloatingButton(){

        FloatingActionButton simpanFloatingButton = (FloatingActionButton) findViewById(R.id.simpan_elektronik_floating_button);
        simpanFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            TextView namaElektronikInElektronikForm = (TextView) findViewById(R.id.namaElektronikInElektronikForm);
            TextView dayaWattInElektronikForm = (TextView) findViewById(R.id.dayaWattInElektronikForm);


            String namaElektronik = namaElektronikInElektronikForm.getText().toString();
            int dayaWatt = Integer.parseInt(dayaWattInElektronikForm.getText().toString());


            Elektronik elektronik = getIntent().getParcelableExtra("elektronik");
            if (elektronik == null) {
                elektronik = new Elektronik();
            }
            elektronik.setNamaElektronik(namaElektronik);
            elektronik.setDayaWatt(dayaWatt);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("elektronik", elektronik);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();


            }
        });

    }

    private void initDeleteFloatingButton(){
        FloatingActionButton deleteFloatingButton = (FloatingActionButton) findViewById(R.id.hapus_elektronik_floating_button);
        if(this.elektronik == null){
            deleteFloatingButton.hide();
        }else{

            deleteFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("elektronik", elektronik);
                    setResult(ListElektronikActivity.RESULT_ACTIVITY_DELETE_ELEKTRONIK, resultIntent);
                    finish();
                }
            });


        }
    }



}
//DONE:Form init