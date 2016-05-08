package net.alaindonesia.simulatortagihanlistrik;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import net.alaindonesia.simulatortagihanlistrik.model.Usage;
import net.alaindonesia.simulatortagihanlistrik.model.DbConnection;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

//TODO:Search feature in Template Elektronik
//TODO:Search feauture in Spiner nama elektronik in usage activity
//TODO:Add template feautre in spinner nama elektronik in usage activity
//DONE:Templete for mode and activity
//TODO:Adding or edit usageMode
//DONE:Kwh show more detail decimal
//DONE::Fake hari button
//DONE:Usage jam non ticker tapi jam : menit dalam scroll value seperti quality time
//DONE: Jam & menit dalam database
//DONE: Delete jam dan menit di usage
//DONE: Title pada usage
//DONE:Buton delete & simpan pisah lebar
//DONE:Save button pada atur biaya listrik
//DONE:Delete button in top left in usage activity
//TODO:Usage jam not more than 24 batasan
//DONE:pakai cara qualitytime app
//DONE:On click layout, popup usage
//DONE:Jam dalam bentuk Jam
//TODO:KOmfirmasi saat hapus
//TODO:Maximum jam is 24:00 in add usage, currently is 25:59
//DONE:Populate db with initial data
//TODO:Fix initial data with better values
//TODO:Autosave, save button remind when keyboard up
//TODO:Template Pemilihan jenis elektronik menggunakan listview daripada spinner
//TODO:Atur templete pemilihan jenis elektronik
//TODO:Bisa enable dan disable usage
//TODO:Menu kirim kritik saran ke email
//TODO:Stupid input handling
//TODO:Anda yakin saat hapus
//DONE:Kwh ada desimal
//TODO:Edit hari long press
//TODO:Jenis elektronik, stand by mode
//TODO:Jenis elektronik hanya sebagai template, buat baru on the fly, real watt value pada usageActivity
//TODO:Lama Standby, Normal, Inverter, dll perhitungan dan di dalam di database per item usage
//TODO: i18n, main code in English
//TODO:Export to csv
//TODO:Delete Usage from Database
//TODO:Total watt dan biaya per hari pada main list_usage daripada watt barang, lama dan jumlah barang
//TODO:delete all timeusage when delete usage

public class MainActivity extends AppCompatActivity {

    private final int PEMAKAIAN_ACTIVITY_REQ = 1;
    private final int BIAYA_LISTRIK_ACTIVITY_REQ = 3;
    private SharedPreferences biayaListrikPreferences;
    private DbConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        biayaListrikPreferences = getSharedPreferences("biayaListrikPreferences", Context.MODE_PRIVATE);

        dbConnection = new DbConnection(this, getResources().openRawResource(R.raw.initial_data));

        checkBiayaListrikPreferences();
        initInputDays();
        initAddUsageButton();
        initListUsage();
        kalkulasiTotal();

    }

    private void kalkulasiTotal() {
        EditText jumlahHariEditText = (EditText) findViewById(R.id.jumlahHariEditText);
        int jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());

        double totalWattHarian = dbConnection.getTotalWattHarian();
        double totalKwh = totalWattHarian * jumlahHari / 1000; //divide 1000 for convert Watt to KWatt

        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getNumberInstance();
        decimalFormat.applyPattern("###,###.#####");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator('.');
        decimalFormatSymbols.setMonetaryDecimalSeparator(',');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setDecimalSeparatorAlwaysShown(false);

        TextView totalUsageListrikTextView = (TextView) findViewById(R.id.totalUsageListrikTextView);
        String totalKwHString = decimalFormat.format(totalKwh) + " KwH ";
        totalUsageListrikTextView.setText(totalKwHString);

        double biayaUsagePerKwh = (double) biayaListrikPreferences.getFloat("biaya_usage_per_kwh", 0);
        double biayaBeban = (double) biayaListrikPreferences.getFloat("biaya_beban", 0);
        double biayaLainnya = (double) biayaListrikPreferences.getFloat("biaya_lainnya", 0);

        double totalBiayaBulanan = totalKwh * biayaUsagePerKwh;
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

    private void initInputDays() {

        final EditText jumlahHariEditText = (EditText) findViewById(R.id.jumlahHariEditText);
        int jumlahHariInPreferences = biayaListrikPreferences.getInt("jumlah_hari", 30);
        jumlahHariEditText.setText(String.valueOf(jumlahHariInPreferences));



        jumlahHariEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int jumlahHari;
                jumlahHari = Integer.parseInt(jumlahHariEditText.getText().toString());


                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Jumlah Hari: ");
                final NumberPicker np = new NumberPicker(v.getContext());

                np.setMinValue(1);
                np.setMaxValue(10000);
                np.setWrapSelectorWheel(false);
                np.setValue(jumlahHari);

                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        int jumlahHari = newVal;
                        jumlahHariEditText.setText(String.valueOf(jumlahHari));

                        SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
                        biayaListrikEditPref.putInt("jumlah_hari", jumlahHari);
                        biayaListrikEditPref.apply();

                        kalkulasiTotal();

                    }
                });
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        np.clearFocus();
                        int jumlahHari = np.getValue();
                        jumlahHariEditText.setText(String.valueOf(jumlahHari));

                        SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
                        biayaListrikEditPref.putInt("jumlah_hari", jumlahHari);
                        biayaListrikEditPref.apply();

                        kalkulasiTotal();
                    }
                });


                final FrameLayout parent = new FrameLayout(v.getContext());
                parent.addView(np, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                alert.setView(parent);
                alert.show().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);;
            }
        });

    }

    private void initAddUsageButton() {
        ImageButton tambahUsageButton = (ImageButton) findViewById(R.id.tambahUsageButton);
        tambahUsageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usageActivity = new Intent(view.getContext(), UsageActivity.class);
                Usage usage = null;
                usageActivity.putExtra("usage", usage);
                startActivityForResult(usageActivity, PEMAKAIAN_ACTIVITY_REQ);
            }
        });
    }

    private void initListUsage() {
        ArrayList<Usage> usageList = dbConnection.getUsageList();

        ListView list = (ListView) findViewById(R.id.usageListView);

        ListAdapter adapter = new UsageListAdapter(this, usageList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent usageActivity = new Intent(view.getContext(), UsageActivity.class);
                Usage usage = (Usage) parent.getItemAtPosition(position);
                usageActivity.putExtra("usage", usage);

                startActivityForResult(usageActivity, PEMAKAIAN_ACTIVITY_REQ);
            }

        });

    }

    private void checkBiayaListrikPreferences() {

        boolean hasInitiated = biayaListrikPreferences.getBoolean("has_initiated", false);
        if (!hasInitiated) {

            SharedPreferences.Editor biayaListrikEditPref = biayaListrikPreferences.edit();
            biayaListrikEditPref.putBoolean("has_initiated", true);
            float biayaUsagePerKwh = (float) 1509.03;
            biayaListrikEditPref.putFloat("biaya_usage_per_kwh", biayaUsagePerKwh);
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

        initListUsage();
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
        }else if (id == R.id.action_jenis_elektronik) {

            Intent intent = new Intent(this, ElectronicListActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


class UsageListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Usage> usageList;
    private static LayoutInflater inflater=null;

    public UsageListAdapter(Activity activity, ArrayList<Usage> usageList) {
        this.activity = activity;
        this.usageList = usageList;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return usageList.size();
    }

    @Override
    public Object getItem(int position) {
        return usageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        if(convertView==null)
            view = inflater.inflate(R.layout.list_usage, null);


        TextView namaElektronikOnListRow = (TextView)view.findViewById(R.id.namaElektronikOnListRow);
        TextView totalWattagePerDayOnListRow = (TextView)view.findViewById(R.id.totalWattagePerDayOnListRow);
        TextView totalUsageHoursPerDayOnListRow = (TextView)view.findViewById(R.id.totalUsageHoursPerDayOnListRow);
        TextView jumlahBarangOnListRow = (TextView) view.findViewById(R.id.jumlahBarangOnListRow);

        Usage usage = usageList.get(position);

        try {
            namaElektronikOnListRow.setText(usage.getElectronic().getElectronicName());
            totalWattagePerDayOnListRow.setText(String.valueOf(usage.getTotalWattagePerDay()) + " watt");
            totalUsageHoursPerDayOnListRow.setText(String.valueOf(usage.getTotalUsageHoursPerDay())+" jam");
            jumlahBarangOnListRow.setText(String.valueOf(usage.getNumberOfElectronic()) + " buah");
        }catch (Exception e){
            Log.e("MAinActivity", e.getMessage());
        }

        return view;
    }

}

//DONE:Changing harga listrik, tapi total bulanan tidak langsung berubah
//DONE:Tambah/Ubah Form Electronic
//DONE:Atur masa perhitungan, jangan statik per bulan, ada scroller dg bilangan harian
//DONE : Total harian per jenis elektronik
//DONE:Biaya tariff lainnya in indonesia rupiah format beautiful
//DONE:Fix this using timepicker
