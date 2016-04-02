package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import net.alaindonesia.simulatortagihanlistrik.model.Elektronik;
import net.alaindonesia.simulatortagihanlistrik.model.Pemakaian;
import net.alaindonesia.simulatortagihanlistrik.model.DatabaseSimulatorPLN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//TODO:On click layout, popup pemakaian
//TODO:Jam dalam bentuk Jam
//TODO:Maximum jam is 24 in add pemakaian
//TODO:Autosave, save button remind when keyboard up
//TODO:Pemilihan jenis elektronik menggunakan listview daripada spinner, ada tombol edit dan hapus yang muncul jika click tahan
//TODO:Bisa enable dan disable pemakaian
//TODO:Menu kirim kritik saran ke email
//TODO:Stupid input handling
//TODO:Anda yakin saat hapus
//TODO:Kwh ada desimal
//TODO:Edit hari long press


public class MainActivity extends AppCompatActivity {

    public static final int RESULT_ACTIVITY_DELETE_PEMAKAIAN = 2;
    private final int PEMAKAIAN_ACTIVITY_REQ = 1;
    private final int BIAYA_LISTRIK_ACTIVITY_REQ = 3;
    private SharedPreferences biayaListrikPreferences;
    private DatabaseSimulatorPLN databaseSimulatorPLN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        biayaListrikPreferences = getSharedPreferences("biayaListrikPreferences", Context.MODE_PRIVATE);
        databaseSimulatorPLN = new DatabaseSimulatorPLN(this);

        checkBiayaListrikPreferences();
        initInputJumlahHari();
        initTambahFloatingButton();
        initListPemakaian();
        kalkulasiTotal();

    }


    private void kalkulasiTotal() {
        EditText jumlahHariEditText = (EditText) findViewById(R.id.jumlahHariEditText);
        int jumlahHari;
        try {
            jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());
        } catch (NumberFormatException e) {
            jumlahHari = biayaListrikPreferences.getInt("jumlah_hari", 30);
            ;
        }


        double totalWattHarian = databaseSimulatorPLN.getTotalWattHarian();
        double totalKwh = totalWattHarian * jumlahHari / 1000; //divide 1000 for convert Watt to KWatt

        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getNumberInstance();
        decimalFormat.applyPattern("###,###");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        TextView totalPemakaianListrikTextView = (TextView) findViewById(R.id.totalPemakaianListrikTextView);
        String totalWattHarianString = decimalFormat.format(totalKwh) + " KwH ";
        totalPemakaianListrikTextView.setText(totalWattHarianString);

        double biayaPemakaianPerKwh = (double) biayaListrikPreferences.getFloat("biaya_pemakaian_per_kwh", 0);
        double biayaBeban = (double) biayaListrikPreferences.getFloat("biaya_beban", 0);
        double biayaLainnya = (double) biayaListrikPreferences.getFloat("biaya_lainnya", 0);

        double totalBiayaBulanan = totalKwh * biayaPemakaianPerKwh;
        totalBiayaBulanan = totalBiayaBulanan + biayaBeban + biayaLainnya;

        decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp. ");
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        TextView totalBiayaListrikTextView = (TextView) findViewById(R.id.totalBiayaListrikTextView);
        String totalBiayaBulananString = decimalFormat.format(totalBiayaBulanan);
        totalBiayaListrikTextView.setText(totalBiayaBulananString);


    }


    private void initInputJumlahHari() {

        final EditText jumlahHariEditText = (EditText) findViewById(R.id.jumlahHariEditText);
        int jumlahHariInPreferences = biayaListrikPreferences.getInt("jumlah_hari", 30);
        jumlahHariEditText.setText(String.valueOf(jumlahHariInPreferences));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        jumlahHariEditText.clearFocus();
        jumlahHariEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int jumlahHari;
                try {
                    jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());
                } catch (NumberFormatException e) {
                    jumlahHari = 0;
                }
                if (jumlahHari > 0 && jumlahHari < 31) {

                    SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
                    biayaListrikEditPref.putInt("jumlah_hari", jumlahHari);
                    biayaListrikEditPref.apply();

                    kalkulasiTotal();
                }
            }
        });

        Button kurangiHariButton = (Button) findViewById(R.id.kurangiHariButton);
        kurangiHariButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());
                jumlahHari = jumlahHari - 1;
                if (jumlahHari < 1)
                    jumlahHari = 1;
                jumlahHariEditText.setText(String.valueOf(jumlahHari));
            }
        });

        Button tambahHariEditText = (Button) findViewById(R.id.tambahHariEditText);
        tambahHariEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());
                jumlahHari = jumlahHari + 1;
                if (jumlahHari > 31)
                    jumlahHari = 31;
                jumlahHariEditText.setText(String.valueOf(jumlahHari));
            }
        });


    }

    private void initTambahFloatingButton() {
        FloatingActionButton tambahElektronikFloatingButton = (FloatingActionButton) findViewById(R.id.tambah_pemakaian_floating_button);
        tambahElektronikFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pemakaianActivity = new Intent(view.getContext(), PemakaianActivity.class);
                Pemakaian pemakaian = null;
                pemakaianActivity.putExtra("pemakaian", pemakaian);
                ArrayList<Elektronik> elektronikList = databaseSimulatorPLN.getElektronikList();
                pemakaianActivity.putParcelableArrayListExtra("elektronikList", elektronikList);
                startActivityForResult(pemakaianActivity, PEMAKAIAN_ACTIVITY_REQ);
            }
        });
    }

    private void initListPemakaian() {
        ArrayList<Pemakaian> pemakaianList = databaseSimulatorPLN.getPemakaianList();

        ListView list = (ListView) findViewById(R.id.pemakaianListView);

        ListAdapter adapter = new PengaturanListAdapter(this, pemakaianList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent pemakaianActivity = new Intent(view.getContext(), PemakaianActivity.class);
                Pemakaian pemakaian = (Pemakaian) parent.getItemAtPosition(position);
                pemakaianActivity.putExtra("pemakaian", pemakaian);
                ArrayList<Elektronik> elektronikList = databaseSimulatorPLN.getElektronikList();
                pemakaianActivity.putParcelableArrayListExtra("elektronikList", elektronikList);

                startActivityForResult(pemakaianActivity, PEMAKAIAN_ACTIVITY_REQ);
            }

        });

    }

    private void checkBiayaListrikPreferences() {

        boolean hasInitiated = biayaListrikPreferences.getBoolean("has_initiated", false);
        if (!hasInitiated) {

            SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
            biayaListrikEditPref.putBoolean("has_initiated", true);
            float biayaPemakaianPerKwh = (float) 1509.03;
            biayaListrikEditPref.putFloat("biaya_pemakaian_per_kwh", biayaPemakaianPerKwh);
            biayaListrikEditPref.putFloat("biaya_beban", 0);
            biayaListrikEditPref.putFloat("biaya_lainnya", 0);
            biayaListrikEditPref.putInt("jumlah_hari", 30);
            biayaListrikEditPref.apply();

            Intent intent = new Intent(this, PengaturanBiayaListrikActivity.class);
            startActivityForResult(intent, BIAYA_LISTRIK_ACTIVITY_REQ);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        initListPemakaian();
        kalkulasiTotal();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_harga_listrik) {

            Intent intent = new Intent(this, PengaturanBiayaListrikActivity.class);
            startActivityForResult(intent, BIAYA_LISTRIK_ACTIVITY_REQ);

            return true;
        } else if (id == R.id.action_jenis_elektronik) {

            Intent intent = new Intent(this, ListElektronikActivity.class);
            startActivity(intent);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}



class PengaturanListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Pemakaian> pemakaianList;
    private static LayoutInflater inflater=null;

    public PengaturanListAdapter(Activity activity, ArrayList<Pemakaian> pemakaianList) {
        this.activity = activity;
        this.pemakaianList =pemakaianList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return pemakaianList.size();
    }

    @Override
    public Object getItem(int position) {
        return pemakaianList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_pemakaian, null);


        TextView namaElektronikOnListRow = (TextView)view.findViewById(R.id.namaElektronikOnListRow);
        TextView dayaWattOnListRow = (TextView)view.findViewById(R.id.dayaWattOnListRow);
        TextView jumlahPemakaianJamOnListRow = (TextView)view.findViewById(R.id.jumlahPemakaianJamOnListRow);
        TextView jumlahBarangOnListRow = (TextView) view.findViewById(R.id.jumlahBarangOnListRow);


        Pemakaian pemakaian = pemakaianList.get(position);

        try {
            namaElektronikOnListRow.setText(pemakaian.getElektronik().getNamaElektronik());
            dayaWattOnListRow.setText(String.valueOf(pemakaian.getElektronik().getDayaWatt()) + " watt");

            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            Calendar lamaPemakaianTime = Calendar.getInstance();
            int jumlahPemakaianMenit = (int) ( (pemakaian.getJumlahPemakaianJam() - Math.floor( pemakaian.getJumlahPemakaianJam())) * 60) ;
            lamaPemakaianTime.set(0, 0, 0, (int) pemakaian.getJumlahPemakaianJam(), jumlahPemakaianMenit);
            jumlahPemakaianJamOnListRow.setText( sdf.format(lamaPemakaianTime.getTime()));
            jumlahBarangOnListRow.setText(String.valueOf(pemakaian.getJumlahBarang()) + " buah");
        }catch (Exception e){
            Log.e("MAinActivity", e.getMessage());
        }

        return view;
    }

}

//DONE:Changing harga listrik, tapi total bulanan tidak langsung berubah
//DONE:Tambah/Ubah Form Elektronik
//DONE:Atur masa perhitungan, jangan statik per bulan, ada scroller dg bilangan harian
//DONE : Total harian per jenis elektronik
//DONE:Biaya tariff lainnya in indonesia rupiah format beautiful
//DONE:Fix this using timepicker