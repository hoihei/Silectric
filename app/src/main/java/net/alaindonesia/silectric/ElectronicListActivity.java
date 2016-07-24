package net.alaindonesia.silectric;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import net.alaindonesia.silectric.model.DbConnection;
import net.alaindonesia.silectric.model.Electronic;

import java.util.ArrayList;

public class ElectronicListActivity extends AppCompatActivity {

    private DbConnection dbConnection;

    private static final int ELECTRONIC_ACTIVITY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_electronic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.listElectronicToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbConnection = new DbConnection(this);

        initListElectronic();
        initAddFloatingButton();
    }


    private void initListElectronic(){
        ArrayList<Electronic> electronicList = dbConnection.getElectronicList();

        ListView list = (ListView)findViewById(R.id.electronicListView);

        ListAdapter adapter = new ElectronicListAdapter(this, electronicList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent electronicActivity = new Intent(view.getContext(), ElectronicActivity.class);
                Electronic electronic = (Electronic) parent.getItemAtPosition(position);
                electronicActivity.putExtra("electronic", electronic);

                startActivityForResult(electronicActivity, ELECTRONIC_ACTIVITY_REQ);
            }

        });

    }



    private void initAddFloatingButton(){
        ImageButton addElectronicTypeFloating = (ImageButton) findViewById(R.id.addElectronicButton);
        addElectronicTypeFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent electronicActivity = new Intent(view.getContext(), ElectronicActivity.class);
                Electronic electronic = null;
                electronicActivity.putExtra("electronic", electronic);

                startActivityForResult(electronicActivity, ELECTRONIC_ACTIVITY_REQ);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ELECTRONIC_ACTIVITY_REQ) {

            initListElectronic();
        }
    }


}

class ElectronicListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Electronic> electronicList;
    private static LayoutInflater inflater=null;

    public ElectronicListAdapter(Activity activity, ArrayList<Electronic> electronicList) {
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
            view = inflater.inflate(R.layout.list_electronic, null);

        TextView nameInListElectronic = (TextView)view.findViewById(R.id.electronicNameInListElectronic);

        Electronic electronic = electronicList.get(position);

        try {

            nameInListElectronic.setText(electronic.getElectronicName());


        }catch (Exception e){
            Log.e("ListElectronic", e.getMessage());
        }

        return view;
    }



}
