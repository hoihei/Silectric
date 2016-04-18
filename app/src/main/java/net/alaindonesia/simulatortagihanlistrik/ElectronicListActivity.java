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
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.DbConnection;
import net.alaindonesia.simulatortagihanlistrik.model.Electronic;

import java.util.ArrayList;

public class ElectronicListActivity extends AppCompatActivity {

    private DbConnection dbConnection;

    public static final int RESULT_ACTIVITY_DELETE_ELEKTRONIK = 2;
    private static final int ELEKTRONIK_ACTIVITY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_electronic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.listElektronikToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbConnection = new DbConnection(this, getResources().openRawResource(R.raw.initial_data));

        initListElektronik();
        initTambahFloatingButton();
    }


    private void initListElektronik(){
        ArrayList<Electronic> electronicList = dbConnection.getElectronicList();

        ListView list = (ListView)findViewById(R.id.elektronikListView);

        ListAdapter adapter = new ElektronikListAdapter(this, electronicList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent elektronikActivity = new Intent(view.getContext(), ElectronicActivity.class);
                Electronic electronic = (Electronic) parent.getItemAtPosition(position);
                elektronikActivity.putExtra("electronic", electronic);

                startActivityForResult(elektronikActivity, ELEKTRONIK_ACTIVITY_REQ);
            }

        });

    }



    private void initTambahFloatingButton(){
        ImageButton tambahJenisElektronikFloating = (ImageButton) findViewById(R.id.addElectronicButton);
        tambahJenisElektronikFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent elektronikActivity = new Intent(view.getContext(), ElectronicActivity.class);
                Electronic electronic = null;
                elektronikActivity.putExtra("electronic", electronic);

                startActivityForResult(elektronikActivity, ELEKTRONIK_ACTIVITY_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ELEKTRONIK_ACTIVITY_REQ) {
            if ( resultCode == Activity.RESULT_OK) {
                Electronic electronic = data.getParcelableExtra("electronic");

                if (electronic.getIdElectronic() == 0) {
                    dbConnection.addElectronic(electronic);

                } else {
                    dbConnection.editElectronic(electronic);
                }

            }else if (resultCode == RESULT_ACTIVITY_DELETE_ELEKTRONIK){
                Electronic electronic = data.getParcelableExtra("electronic");
                int idElektronik = electronic.getIdElectronic();
                dbConnection.deleteElectronic(idElektronik);
            }
            initListElektronik();
        }
    }


}

class ElektronikListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Electronic> electronicList;
    private static LayoutInflater inflater=null;

    public ElektronikListAdapter(Activity activity, ArrayList<Electronic> electronicList) {
        this.activity = activity;
        this.electronicList = electronicList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return electronicList.size();
    }

    @Override
    public Object getItem(int position) {
        return electronicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_elektronic, null);

        TextView namaInListElektronik = (TextView)view.findViewById(R.id.electronicNameInListElektronik);

        Electronic electronic = electronicList.get(position);

        try {

            namaInListElektronik.setText(electronic.getElectronicName());


        }catch (Exception e){
            Log.e("ListElektronik", e.getMessage());
        }

        return view;
    }



}
