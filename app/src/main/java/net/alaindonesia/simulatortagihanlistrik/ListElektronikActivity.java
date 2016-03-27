package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.DatabaseSimulatorPLN;
import net.alaindonesia.simulatortagihanlistrik.model.Elektronik;

import java.util.ArrayList;

public class ListElektronikActivity extends AppCompatActivity {

    private DatabaseSimulatorPLN databaseSimulatorPLN;

    public static final int RESULT_ACTIVITY_DELETE_ELEKTRONIK = 2;
    private static final int ELEKTRONIK_ACTIVITY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_elektronik);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseSimulatorPLN = new DatabaseSimulatorPLN(this);

        initListElektronik();
        initTambahFloatingButton();
    }


    private void initListElektronik(){
        ArrayList<Elektronik> elektronikList = databaseSimulatorPLN.getElektronikList();

        ListView list = (ListView)findViewById(R.id.elektronikListView);

        ListAdapter adapter = new ElektronikListAdapter(this, elektronikList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent elektronikActivity = new Intent(view.getContext(), ElektronikActivity.class);
                Elektronik elektronik = (Elektronik) parent.getItemAtPosition(position);
                elektronikActivity.putExtra("elektronik", elektronik);

                startActivityForResult(elektronikActivity, ELEKTRONIK_ACTIVITY_REQ);
            }

        });

    }

    private void initTambahFloatingButton(){
        FloatingActionButton tambahJenisElektronikFloating = (FloatingActionButton) findViewById(R.id.tambah_jenis_elektronik_floating_button);
        tambahJenisElektronikFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent elektronikActivity = new Intent(view.getContext(), ElektronikActivity.class);
                Elektronik elektronik = null;
                elektronikActivity.putExtra("elektronik", elektronik);

                startActivityForResult(elektronikActivity, ELEKTRONIK_ACTIVITY_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ELEKTRONIK_ACTIVITY_REQ) {
            if ( resultCode == Activity.RESULT_OK) {
                Elektronik elektronik = data.getParcelableExtra("elektronik");

                if (elektronik.getIdElektronik() == 0) {
                    databaseSimulatorPLN.addElektronik(elektronik);

                } else {
                    databaseSimulatorPLN.editElektronik(elektronik);
                }

            }else if (resultCode == RESULT_ACTIVITY_DELETE_ELEKTRONIK){
                Elektronik elektronik = data.getParcelableExtra("elektronik");
                int idElektronik = elektronik.getIdElektronik();
                databaseSimulatorPLN.deleteElektronik(idElektronik);
            }
            initListElektronik();
        }
    }


}

class ElektronikListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Elektronik> elektronikList;
    private static LayoutInflater inflater=null;

    public ElektronikListAdapter(Activity activity, ArrayList<Elektronik> elektronikList) {
        this.activity = activity;
        this.elektronikList = elektronikList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return elektronikList.size();
    }

    @Override
    public Object getItem(int position) {
        return elektronikList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_elektronik, null);

        TextView namaInListElektronik = (TextView)view.findViewById(R.id.namaInListElektronik);
        TextView dayaInListElektronik = (TextView)view.findViewById(R.id.dayaInListElektronik);

        Elektronik elektronik = elektronikList.get(position);

        try {

            namaInListElektronik.setText(elektronik.getNamaElektronik());
            dayaInListElektronik.setText(String.format("%s watt", String.valueOf(elektronik.getDayaWatt())));


        }catch (Exception e){
            Log.e("ListElektronik", e.getMessage());
        }

        return view;
    }

}
