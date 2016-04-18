package net.alaindonesia.simulatortagihanlistrik.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

public class DbConnection extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectricUsageCostSimulation.db";
    private static final int DATABASE_VERSION = 8;
    private SQLiteDatabase thisDB;
    InputStream initialDataStream;

    public DbConnection(Context context,InputStream initialDataStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.initialDataStream = initialDataStream;

    }




    public Electronic getDefaultElectronic() {
        Electronic Electronic = new Electronic();
        String selectQuery = "SELECT * FROM Electronic limit 1";

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int idElectronic = cursor.getInt(0);
                String electronicName = cursor.getString(1);
                Electronic = new Electronic(idElectronic, electronicName);

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return Electronic;
    }

    public ArrayList<Electronic> getElectronicList() {
        ArrayList<Electronic> ElectronicList = new ArrayList<>();
        String selectQuery = "SELECT * FROM Electronic order by electronicName asc";

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int idElectronic = cursor.getInt(0);
                String electronicName = cursor.getString(1);
                ElectronicList.add(new Electronic(idElectronic, electronicName));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return ElectronicList;
    }

    public ArrayList<TimeUsage> getTimeUsagesByIdUsage(int idUsage) {
        ArrayList<TimeUsage>  timeUsageLists = new ArrayList<>();
        String selectQuery = "SELECT 'TimeUsage'.'idTimeUsage' , 'TimeUsage'.'idUsage' , 'TimeUsage'.'idUsageMode' , 'TimeUsage'.'wattage', 'TimeUsage'.'hours' , 'TimeUsage'.'minutes', 'UsageMode'.'usageModeName'  FROM TimeUsage inner join 'UsageMode' on 'UsageMode'.idUsageMode = 'TimeUsage'.idUsageMode  where idUsage="+idUsage;

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idTimeUsage = cursor.getInt(0);
                idUsage = cursor.getInt(1);
                int idUsageMode = cursor.getInt(2);
                int wattage = cursor.getInt(3);
                int hours = cursor.getInt(4);
                int minutes = cursor.getInt(5);
                String usageModeName = cursor.getString(6);
                timeUsageLists.add(new TimeUsage(idTimeUsage, idUsage, idUsageMode, wattage, hours, minutes, new UsageMode(idUsageMode, usageModeName)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return timeUsageLists;
    }

    public ArrayList<Usage> getUsageList() {
        ArrayList<Usage> usageList = new ArrayList<>();
        String selectQuery = "select Usage.idUsage, Usage.idElectronic, " +
                "Usage.numberOfElectronic, electronicName, " +
                "Usage.totalWattagePerDay, Usage.totalUsageHoursPerDay " +
                "from Usage inner join Electronic on " +
                "Usage.idElectronic=Electronic.idElectronic order by idUsage desc;";

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int idUsage = cursor.getInt(0);
                int idElectronic = cursor.getInt(1);
                int jumlahBarang = cursor.getInt(2);
                String electronicName = cursor.getString(3);
                int totalWattagePerDay = cursor.getInt(4);
                double totalUsageHoursPerDay = cursor.getDouble(5);
                Electronic Electronic = new Electronic(idElectronic, electronicName);
                Usage usage = new Usage(idUsage, Electronic, jumlahBarang, totalWattagePerDay, totalUsageHoursPerDay);
                usageList.add(usage);

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return usageList;
    }


    public double getTotalWattHarian() {
        String selectQuery = "select sum(totalWattagePerDay * numberOfElectronic) as " +
                "total_harian from Usage inner join TimeUsage on " +
                "Usage.idUsage=TimeUsage.idUsage;";

        double totalWattHarian = 0;
        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            totalWattHarian = cursor.getDouble(0);
        }

        cursor.close();
        closeDatabase();

        return totalWattHarian;
    }


    public long addUsage(Usage usage, ArrayList timeUsageList) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("numberOfElectronic", usage.getNumberOfElectronic());
        values.put("idElectronic", usage.getElectronic().getIdElectronic());
        values.put("totalWattagePerDay", usage.getTotalWattagePerDay());
        values.put("totalUsageHoursPerDay", usage.getTotalUsageHoursPerDay());

        long lastInsertedId = thisDB.insert("Usage", null, values);

        closeDatabase();

        updateTimeUsages(lastInsertedId, timeUsageList);



        return lastInsertedId;

    }

    private void updateTimeUsages(long idUsage, ArrayList<TimeUsage> timeUsageList) {
        openWritableDatabase();

        thisDB.delete("TimeUsage", "idUsage=?", new String[]{String.valueOf(idUsage)});

        for (TimeUsage timeUsage : timeUsageList){
            ContentValues values = new ContentValues();
            values.put("idUsage", idUsage);
            values.put("idUsageMode", timeUsage.getIdUsageMode());
            values.put("wattage", timeUsage.getWattage());
            values.put("hours", timeUsage.getHours());
            values.put("minutes", timeUsage.getMinutes());


            thisDB.insert("TimeUsage", null, values);

        }

        closeDatabase();
    }

    public boolean saveUsage(Usage usage, ArrayList timeUsageList) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idElectronic", usage.getElectronic().getIdElectronic());
        values.put("numberOfElectronic", usage.getNumberOfElectronic());
        values.put("totalWattagePerDay", usage.getTotalWattagePerDay());
        values.put("totalUsageHoursPerDay", usage.getTotalUsageHoursPerDay());
        String[] whereArgs = { String.valueOf(usage.getIdUsage())};

        long result = thisDB.update("Usage", values, "idUsage=?", whereArgs);

        closeDatabase();

        updateTimeUsages(usage.getIdUsage(), timeUsageList);


        return result > 0;

    }



    public boolean addElectronic(Electronic Electronic) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("electronicName", Electronic.getElectronicName());

        long result =  thisDB.insert("Electronic", null, values);
        closeDatabase();

        return result > 0;

    }

    public boolean editElectronic(Electronic Electronic) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("electronicName", Electronic.getElectronicName());
        String[] whereArgs = {String.valueOf(Electronic.getIdElectronic()) };
        long result = thisDB.update("Electronic", values, "idElectronic=?", whereArgs);
        closeDatabase();

        return result > 0;

    }

    public void deleteUsage(Usage usage) {
        openWritableDatabase();

        thisDB.delete("Usage", "idUsage=?", new String[]{String.valueOf(usage.getIdUsage())});

        closeDatabase();
    }

    public void deleteElectronic(int idElectronic) {

        openWritableDatabase();

        thisDB.delete("Electronic", "idElectronic=?", new String[]{String.valueOf(idElectronic)});

        closeDatabase();
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Electronic ('idElectronic' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'electronicName' TEXT NOT NULL);");
        db.execSQL("CREATE TABLE Usage ('idUsage' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,  'idElectronic' INTEGER  NOT NULL  DEFAULT (1), 'numberOfElectronic' INTEGER  NOT NULL  DEFAULT (0), 'totalUsageHoursPerDay' REAL NOT NULL DEFAULT (1), 'totalWattagePerDay' INTEGER NOT NULL DEFAULT (1));");
        db.execSQL("CREATE TABLE TimeUsage ('idTimeUsage' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'idUsage' INTEGER  NOT NULL  DEFAULT (1), 'idUsageMode' INTEGER NOT NULL, 'wattage' INTEGER  NOT NULL  DEFAULT (1), 'hours' INTEGER NOT NULL DEFAULT (0), 'minutes' INTEGER NOT NULL DEFAULT (0));");
        db.execSQL("CREATE TABLE UsageMode ('idUsageMode' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'usageModeName' TEXT NOT NULL);");

        ArrayList<Electronic> electronicArrayList = new ArrayList<>();
        ArrayList<UsageMode> usageModeArrayList = new ArrayList<>();

        try {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];

            JSONObject initialDataJson = null;

            Reader reader = new BufferedReader(new InputStreamReader(initialDataStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String initialDataJsonStr = writer.toString();
            initialDataJson = new JSONObject(initialDataJsonStr);

            JSONArray electronicsJsonArr = initialDataJson.getJSONArray("Electronic");
            for (int i = 0; i < electronicsJsonArr.length(); i++){
                int idElectronic = electronicsJsonArr.getJSONObject(i).getInt("idElectronic");
                String electronicName = electronicsJsonArr.getJSONObject(i).getString("electronicName");
                electronicArrayList.add(new Electronic(idElectronic, electronicName));
            }

            JSONArray usageModeJsonArr = initialDataJson.getJSONArray("UsageMode");
            for (int i = 0; i < usageModeJsonArr.length(); i++){
                int idUsageMode = usageModeJsonArr.getJSONObject(i).getInt("idUsageMode");
                String usageModeName = usageModeJsonArr.getJSONObject(i).getString("usageModeName");
                usageModeArrayList.add(new UsageMode(idUsageMode, usageModeName));
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (Electronic electronic : electronicArrayList) {
            db.execSQL("INSERT into Electronic('idElectronic', 'electronicName') values(" + electronic.getIdElectronic() + ",'" + electronic.getElectronicName() + "');");
        }

        for (UsageMode usageMode : usageModeArrayList) {
            String sql = "INSERT into UsageMode('idUsageMode', 'usageModeName') values(" + usageMode.getIdUsageMode() + ",'" + usageMode.getUsageModeName() + "');";
            db.execSQL("INSERT into UsageMode('idUsageMode', 'usageModeName') values(" + usageMode.getIdUsageMode() + ",'" + usageMode.getUsageModeName() + "');");
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        thisDB = db;
        db.execSQL("DROP TABLE IF EXISTS TimeUsage " );
        db.execSQL("DROP TABLE IF EXISTS Electronic " );
        db.execSQL("DROP TABLE IF EXISTS Usage " );
        db.execSQL("DROP TABLE IF EXISTS UsageMode " );


        this.onCreate(db);
    }

    private void closeDatabase() {
        if (thisDB != null && thisDB.isOpen()) {
            thisDB.close();
        }
    }

    private void openReadableDatabase() {
        thisDB = getReadableDatabase();
    }

    private void openWritableDatabase() {
        thisDB = getWritableDatabase();
    }


    public ArrayList<UsageMode> getUsageModeList() {
        ArrayList<UsageMode> usageModeList = new ArrayList<>();

        String selectQuery = "select idUsageMode, usageModeName from UsageMode";

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int idUsageMode = cursor.getInt(0);
                String usageModeName = cursor.getString(1);
                usageModeList.add(new UsageMode(idUsageMode, usageModeName));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return usageModeList;
    }
}