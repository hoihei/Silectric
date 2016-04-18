package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.Electronic;

public class ElectronicActivity extends AppCompatActivity {
    //DONE:Edit button
    //DONE:Delete button
    //DONE:Save button

    private Electronic electronic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.electronic = getIntent().getParcelableExtra("electronic");

        initForm();
        initDeleteElectronicButton();
        initSaveElectronicButton();
    }

    private void initForm(){
        TextView namaElektronikInElektronikForm = (TextView) findViewById(R.id.namaElektronikInElektronikForm);

        if(this.electronic != null){

            namaElektronikInElektronikForm.setText(String.valueOf(this.electronic.getElectronicName()));

        }
    }

    private void initSaveElectronicButton(){

        ImageButton simpanFloatingButton = (ImageButton) findViewById(R.id.saveElectronicButton);
        simpanFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            TextView namaElektronikInElektronikForm = (TextView) findViewById(R.id.namaElektronikInElektronikForm);

            String electronicName = namaElektronikInElektronikForm.getText().toString();

            Electronic electronic = getIntent().getParcelableExtra("electronic");
            if (electronic == null) {
                electronic = new Electronic();
            }
            electronic.setElectronicName(electronicName);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("electronic", electronic);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();


            }
        });

    }

    private void initDeleteElectronicButton(){
        ImageButton deleteElectronicButton = (ImageButton) findViewById(R.id.deleteElectronicButton);
        if(this.electronic == null){
            deleteElectronicButton.setVisibility(View.INVISIBLE);
        }else{
            deleteElectronicButton.setVisibility(View.VISIBLE);
            deleteElectronicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("electronic", electronic);
                    setResult(ElectronicListActivity.RESULT_ACTIVITY_DELETE_ELEKTRONIK, resultIntent);
                    finish();
                }
            });


        }
    }



}
//DONE:Form init