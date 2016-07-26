package net.alaindonesia.silectric.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final int DATABASE_VERSION = 25;
    private SQLiteDatabase thisDB;
    private InputStream initialDataStream = null;

    public DbConnection(Context context, InputStream initialDataStream) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.initialDataStream = initialDataStream;

    }

    public DbConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public ArrayList<ElectronicTimeUsageTemplate> getElectronicTimeUsageTemplateByIdElectronic(int idElectronic) {
        ArrayList<ElectronicTimeUsageTemplate> timeUsageTemplateArrayList  = new ArrayList<>();

        String selectQuery = "SELECT 'ElectronicTimeUsageTemplate'.'idElectronicTimeUsageTemplate' , 'ElectronicTimeUsageTemplate'.'idElectronic' , 'ElectronicTimeUsageTemplate'.'idUsageMode' , 'ElectronicTimeUsageTemplate'.'wattage', 'ElectronicTimeUsageTemplate'.'hours' , 'ElectronicTimeUsageTemplate'.'minutes', 'UsageMode'.'usageModeName'  FROM ElectronicTimeUsageTemplate inner join 'UsageMode' on 'UsageMode'.idUsageMode = 'ElectronicTimeUsageTemplate'.idUsageMode  where idElectronic="+idElectronic;
//        String selectQuery = "SELECT * from ElectronicTimeUsageTemplate";

        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int idTimeUsage = cursor.getInt(0);
                idElectronic = cursor.getInt(1);
                int idUsageMode = cursor.getInt(2);
                int wattage = cursor.getInt(3);
                int hours = cursor.getInt(4);
                int minutes = cursor.getInt(5);
                String usageModeName = cursor.getString(6);
                timeUsageTemplateArrayList.add(new ElectronicTimeUsageTemplate(idTimeUsage, idElectronic, idUsageMode, wattage, hours, minutes, new UsageMode(idUsageMode, usageModeName)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return timeUsageTemplateArrayList;

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
                int numberOfItems = cursor.getInt(2);
                String electronicName = cursor.getString(3);
                int totalWattagePerDay = cursor.getInt(4);
                double totalUsageHoursPerDay = cursor.getDouble(5);
                Electronic Electronic = new Electronic(idElectronic, electronicName);
                Usage usage = new Usage(idUsage, Electronic, numberOfItems, totalWattagePerDay, totalUsageHoursPerDay);
                usageList.add(usage);

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return usageList;
    }


    public double getTotalWattDaily() {
        String selectQuery = "select sum(totalWattagePerDay * numberOfElectronic) as " +
                "total_watt_daily from Usage inner join TimeUsage on " +
                "Usage.idUsage=TimeUsage.idUsage;";

        double totalWattDaily = 0;
        openReadableDatabase();
        Cursor cursor = thisDB.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            totalWattDaily = cursor.getDouble(0);
        }

        cursor.close();
        closeDatabase();

        return totalWattDaily;
    }


    public void addUsage(Usage usage, ArrayList timeUsageList) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("numberOfElectronic", usage.getNumberOfElectronic());
        values.put("idElectronic", usage.getElectronic().getIdElectronic());
        values.put("totalWattagePerDay", usage.getTotalWattagePerDay());
        values.put("totalUsageHoursPerDay", usage.getTotalUsageHoursPerDay());

        long lastInsertedId = thisDB.insert("Usage", null, values);

        closeDatabase();

        updateTimeUsages(lastInsertedId, timeUsageList);

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

    public void editUsage(Usage usage, ArrayList timeUsageList) {
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


    }



    public void addElectronic(Electronic electronic, ArrayList<ElectronicTimeUsageTemplate> electronicTimeUsageTemplatesArrayList) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("electronicName", electronic.getElectronicName());

        long result =  thisDB.insert("Electronic", null, values);
        closeDatabase();

        updateElectronicTimeUsageTemplates(electronic.getIdElectronic(), electronicTimeUsageTemplatesArrayList);


    }

    private void updateElectronicTimeUsageTemplates(long idElectronic, ArrayList<ElectronicTimeUsageTemplate> electronicTimeUsageTemplatesArrayList) {
        openWritableDatabase();

        thisDB.delete("ElectronicTimeUsageTemplate", "idElectronic=?", new String[]{String.valueOf(idElectronic)});

        for (ElectronicTimeUsageTemplate e : electronicTimeUsageTemplatesArrayList){
            ContentValues values = new ContentValues();
            values.put("idElectronic", idElectronic);
            values.put("idUsageMode", e.getIdUsageMode());
            values.put("wattage", e.getWattage());
            values.put("hours", e.getHours());
            values.put("minutes", e.getMinutes());


            thisDB.insert("ElectronicTimeUsageTemplate", null, values);

        }

        closeDatabase();
    }

    public void editElectronic(Electronic electronic, ArrayList<ElectronicTimeUsageTemplate> electronicTimeUsageTemplatesArrayList) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("electronicName", electronic.getElectronicName());
        String[] whereArgs = {String.valueOf(electronic.getIdElectronic()) };
        long result = thisDB.update("Electronic", values, "idElectronic=?", whereArgs);
        closeDatabase();
        updateElectronicTimeUsageTemplates(electronic.getIdElectronic(), electronicTimeUsageTemplatesArrayList);


    }

    public void deleteUsage(Usage usage) {
        openWritableDatabase();

        thisDB.delete("Usage", "idUsage=?", new String[]{String.valueOf(usage.getIdUsage())});
        thisDB.delete("TimeUsage", "idUsage=?", new String[]{String.valueOf(usage.getIdUsage())});

        closeDatabase();
    }

    public void deleteElectronic(int idElectronic) {

        openWritableDatabase();

        thisDB.delete("Electronic", "idElectronic=?", new String[]{String.valueOf(idElectronic)});
        thisDB.delete("ElectronicTimeUsageTemplate", "idElectronic=?", new String[]{String.valueOf(idElectronic)});

        closeDatabase();
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Electronic ('idElectronic' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'electronicName' TEXT NOT NULL);");
        db.execSQL("CREATE TABLE ElectronicTimeUsageTemplate ('idElectronicTimeUsageTemplate' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'idElectronic' INTEGER NOT NULL,  'idUsageMode' INTEGER NOT NULL, 'wattage' INTEGER  NOT NULL  DEFAULT (1), 'hours' INTEGER NOT NULL DEFAULT (0), 'minutes' INTEGER NOT NULL DEFAULT (0));");
        db.execSQL("CREATE TABLE Usage ('idUsage' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,  'idElectronic' INTEGER  NOT NULL  DEFAULT (1), 'numberOfElectronic' INTEGER  NOT NULL  DEFAULT (0), 'totalUsageHoursPerDay' REAL NOT NULL DEFAULT (1), 'totalWattagePerDay' INTEGER NOT NULL DEFAULT (1));");
        db.execSQL("CREATE TABLE TimeUsage ('idTimeUsage' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'idUsage' INTEGER  NOT NULL  DEFAULT (1), 'idUsageMode' INTEGER NOT NULL, 'wattage' INTEGER  NOT NULL  DEFAULT (1), 'hours' INTEGER NOT NULL DEFAULT (0), 'minutes' INTEGER NOT NULL DEFAULT (0));");
        db.execSQL("CREATE TABLE UsageMode ('idUsageMode' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'usageModeName' TEXT NOT NULL);");

        try {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];

            Reader reader = new BufferedReader(new InputStreamReader(initialDataStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String initialDataJsonStr = writer.toString();
            JSONObject initialDataJson = new JSONObject(initialDataJsonStr);

            JSONArray electronicsJsonArr = initialDataJson.getJSONArray("Electronic");
            for (int i = 0; i < electronicsJsonArr.length(); i++){

                String electronicName = electronicsJsonArr.getJSONObject(i).getString("electronicName");


                ContentValues values = new ContentValues();
                values.put("electronicName", electronicName);
                long idElectronic = db.insert("Electronic", null, values);

                JSONArray timeUsageTemplateJsonArray = electronicsJsonArr.getJSONObject(i).getJSONArray("TimeUsageTemplate");
                for (int j = 0; j < timeUsageTemplateJsonArray.length(); j++) {
                    int idUsageMode = timeUsageTemplateJsonArray.getJSONObject(j).getInt("idUsageMode");
                    int wattage = timeUsageTemplateJsonArray.getJSONObject(j).getInt("wattage");
                    int hours = timeUsageTemplateJsonArray.getJSONObject(j).getInt("hours");
                    int minutes =timeUsageTemplateJsonArray.getJSONObject(j).getInt("minutes");
                    db.execSQL("INSERT into ElectronicTimeUsageTemplate('idElectronic', 'idUsageMode', 'wattage', 'hours', 'minutes') values(" + idElectronic + "," + idUsageMode + "," + wattage + "," + hours + "," + minutes +  ");");
                }
            }

            JSONArray usageModeJsonArr = initialDataJson.getJSONArray("UsageMode");
            for (int i = 0; i < usageModeJsonArr.length(); i++){
                int idUsageMode = usageModeJsonArr.getJSONObject(i).getInt("idUsageMode");
                String usageModeName = usageModeJsonArr.getJSONObject(i).getString("usageModeName");
                db.execSQL("INSERT into UsageMode('idUsageMode', 'usageModeName') values(" + idUsageMode + ",'" + usageModeName + "');");
            }


        } catch (Exception e) {
            db.execSQL("delete from Electronic");
            db.execSQL("delete from ElectronicTimeUsageTemplate");
            db.execSQL("delete from Usage");
            db.execSQL("delete from TimeUsage");
            db.execSQL("delete from UsageMode");

            Log.d("DbConnection", e.toString());

        }


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        thisDB = db;

        db.execSQL("DROP TABLE IF EXISTS ElectronicTimeUsageTemplate " );
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