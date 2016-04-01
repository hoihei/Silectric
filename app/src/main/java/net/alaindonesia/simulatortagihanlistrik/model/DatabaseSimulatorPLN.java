package net.alaindonesia.simulatortagihanlistrik.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.alaindonesia.simulatortagihanlistrik.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DatabaseSimulatorPLN extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SimulasiTarifPLN.db";
    private static final int DATABASE_VERSION = 5;
    private SQLiteDatabase dbSimulasiTarifPLN;
    private Context context;
    private Elektronik defaultElektronik;

    public DatabaseSimulatorPLN(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    public Elektronik getDefaultElektronik() {
        Elektronik elektronik = new Elektronik();
        String selectQuery = "SELECT * FROM elektronik limit 1";

        openReadableDatabase();
        Cursor cursor = dbSimulasiTarifPLN.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int id_elektronik = cursor.getInt(0);
                String nama_elektronik = cursor.getString(1);
                int daya_watt = cursor.getInt(2);
                elektronik = new Elektronik(id_elektronik, daya_watt, nama_elektronik);

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return elektronik;
    }

    public ArrayList<Elektronik> getElektronikList() {
        ArrayList<Elektronik> elektronikList = new ArrayList<>();
        String selectQuery = "SELECT * FROM elektronik order by nama_elektronik asc";

        openReadableDatabase();
        Cursor cursor = dbSimulasiTarifPLN.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int id_elektronik = cursor.getInt(0);
                String nama_elektronik = cursor.getString(1);
                int daya_watt = cursor.getInt(2);
                elektronikList.add(new Elektronik(id_elektronik, daya_watt, nama_elektronik));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return elektronikList;
    }

    public ArrayList<Pemakaian> getPemakaianList() {
        ArrayList<Pemakaian> pemakaianList = new ArrayList<>();
        String selectQuery = "select pemakaian.id_pemakaian, pemakaian.id_elektronik, " +
                "pemakaian.jumlah_pemakaian_jam, pemakaian.jumlah_barang,  " +
                "elektronik.daya_watt, nama_elektronik from pemakaian inner join elektronik on " +
                "pemakaian.id_elektronik=elektronik.id_elektronik order by id_pemakaian desc;";

        openReadableDatabase();
        Cursor cursor = dbSimulasiTarifPLN.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                int idPemakaian = cursor.getInt(0);
                int idElektronik = cursor.getInt(1);
                double jumlahPemakaianJam = cursor.getDouble(2);

                int jumlahBarang = cursor.getInt(3);
                int dayaWatt = cursor.getInt(4);
                String namaElektronik = cursor.getString(5);
                Elektronik elektronik = new Elektronik(idElektronik, dayaWatt, namaElektronik);
                pemakaianList.add(new Pemakaian(idPemakaian, elektronik, jumlahPemakaianJam, jumlahBarang));

            } while (cursor.moveToNext());
        }

        cursor.close();
        closeDatabase();

        return pemakaianList;
    }


    public double getTotalWattHarian() {
        String selectQuery = "select sum(daya_watt*  jumlah_pemakaian_jam* jumlah_barang) as " +
                "total_harian from pemakaian inner join elektronik on " +
                "pemakaian.id_elektronik=elektronik.id_elektronik;";

        double totalWattHarian = 0;
        openReadableDatabase();
        Cursor cursor = dbSimulasiTarifPLN.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            totalWattHarian = cursor.getDouble(0);
        }

        cursor.close();
        closeDatabase();

        return totalWattHarian;
    }

    public long addPemakaian(Pemakaian pemakaian) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("jumlah_pemakaian_jam", pemakaian.getJumlahPemakaianJam());
        values.put("jumlah_barang", pemakaian.getJumlahBarang());
        values.put("id_elektronik", pemakaian.getElektronik().getIdElektronik());

        long lastInsertedId = dbSimulasiTarifPLN.insert("pemakaian", null, values);

        closeDatabase();

        return lastInsertedId;

    }

    public boolean savePemakaian(Pemakaian pemakaian) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("jumlah_pemakaian_jam", pemakaian.getJumlahPemakaianJam());
        values.put("jumlah_barang", pemakaian.getJumlahBarang());
        String[] whereArgs = { String.valueOf(pemakaian.getIdPemakaian())};

        long result = dbSimulasiTarifPLN.update("pemakaian", values, "id_pemakaian=?", whereArgs);
        closeDatabase();
        return result > 0;

    }

    public boolean addElektronik(Elektronik elektronik) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nama_elektronik", elektronik.getNamaElektronik());
        values.put("daya_watt", elektronik.getDayaWatt());

        long result =  dbSimulasiTarifPLN.insert("elektronik", null, values);
        closeDatabase();

        return result > 0;

    }

    public boolean editElektronik(Elektronik elektronik) {
        openWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nama_elektronik", elektronik.getNamaElektronik());
        values.put("daya_watt", elektronik.getDayaWatt());
        String[] whereArgs = {String.valueOf(elektronik.getIdElektronik()) };
        long result = dbSimulasiTarifPLN.update("elektronik", values, "id_elektronik=?", whereArgs);
        closeDatabase();

        return result > 0;

    }

    public void deletePemakaian(Pemakaian pemakaian) {
        openWritableDatabase();

        dbSimulasiTarifPLN.delete("pemakaian", "id_pemakaian=?", new String[]{String.valueOf(pemakaian.getIdPemakaian())});

        closeDatabase();
    }

    public void deleteElektronik(int idElektronik) {

        openWritableDatabase();

        dbSimulasiTarifPLN.delete("elektronik", "id_elektronik=?", new String[]{String.valueOf(idElektronik)});

        closeDatabase();
    }




    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableElektronik = "CREATE TABLE elektronik ('id_elektronik' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'nama_elektronik' TEXT NOT NULL, 'daya_watt' INTEGER NOT NULL);";

        db.execSQL(createTableElektronik);

        String createTablePemakaian = "CREATE TABLE pemakaian ('id_pemakaian' INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,  'id_elektronik' INTEGER  NOT NULL  DEFAULT (1), 'jumlah_pemakaian_jam'REAL NOT NULL DEFAULT (1), 'jumlah_barang' INTEGER  NOT NULL  DEFAULT (0));";

        db.execSQL(createTablePemakaian);

        db.execSQL("INSERT INTO elektronik(id_elektronik,nama_elektronik,daya_watt) VALUES\n" +
                "    (1,'AC 0.5 PK',400),\n" +
                "    (2,'AC 1 PK',840),\n" +
                "    (3,'AC 1.5 PK',1170),\n" +
                "    (4,'AC 2 PK',1920),\n" +
                "    (5,'AC 2.5 PK',2570),\n" +
                "    (6,'Akuarium Besar',250),\n" +
                "    (7,'Akuarium Kecil',50),\n" +
                "    (8,'Audio/ Stereo Set.',50),\n" +
                "    (9,'Blender ',130),\n" +
                "    (10,'Charger Handphone',5),\n" +
                "    (11,'Dispenser (Menyala)',250),\n" +
                "    (12,'Dispenser (Standby)',6),\n" +
                "    (13,'DVD Player',25),\n" +
                "    (14,'Hair dryer',1200),\n" +
                "    (15,'Handphone Charger',5),\n" +
                "    (16,'Heater ',750),\n" +
                "    (17,'Internet ADSL Router',10),\n" +
                "    (18,'Internet Wifi Router',10),\n" +
                "    (19,'Kipas Angin',103),\n" +
                "    (20,'Kompor Listrik',380),\n" +
                "    (21,'Komputer + Monitor',250),\n" +
                "    (22,'Komputer Standby',30),\n" +
                "    (23,'Kulkas (Kompressor Standby)',10),\n" +
                "    (24,'Lampu 100 Watt',100),\n" +
                "    (25,'Lampu 11 Watt',11),\n" +
                "    (26,'Lampu 18 Watt',18),\n" +
                "    (27,'Lampu 20 Watt',20),\n" +
                "    (28,'Lampu 3 Watt',3),\n" +
                "    (29,'Lampu 35 Watt',35),\n" +
                "    (30,'Lampu 5 Watt',5),\n" +
                "    (31,'Lampu 65 Watt',65),\n" +
                "    (32,'Lampu 75 Watt',75),\n" +
                "    (33,'Lampu 12 Watt',12),\n" +
                "    (34,'Lampu 15 Watt',15),\n" +
                "    (35,'Lampu 23 Watt',23),\n" +
                "    (36,'Lampu 25 Watt',25),\n" +
                "    (37,'Lampu 8 Watt',8),\n" +
                "    (38,'Laptop',75),\n" +
                "    (39,'Magic Jar (Memasak)',400),\n" +
                "    (40,'Magic Jar (Menghangatkan)',50),\n" +
                "    (41,'Mesin Cuci',300),\n" +
                "    (42,'Mesin Kopi',900),\n" +
                "    (43,'Microwave',1270),\n" +
                "    (44,'Pemanas Air',400),\n" +
                "    (45,'Pompa Air',650),\n" +
                "    (46,'Radio ',10),\n" +
                "    (47,'Set Decoder Box (TV Langganan)',50),\n" +
                "    (48,'Set Parabola',50),\n" +
                "    (49,'Setrika',300),\n" +
                "    (50,'TV 19 Inchi',70),\n" +
                "    (51,'TV 27 Inchi',110),\n" +
                "    (52,'TV 36 Inchi',130),\n" +
                "    (53,'TV 53 Inchi',170),\n" +
                "    (54,'TV Standby',10),\n" +
                "    (55,'Kulkas (Kompressor Menyala)',50),\n" +
                "    (56,'Vacuum cleaner',1000);");

        db.execSQL("INSERT INTO pemakaian(id_elektronik, jumlah_pemakaian_jam, jumlah_barang ) VALUES\n" +
                "    (1, 12,1);");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dbSimulasiTarifPLN = db;

        db.execSQL("DROP TABLE IF EXISTS elektronik " );
        db.execSQL("DROP TABLE IF EXISTS pemakaian " );


        this.onCreate(db);
    }

    private void closeDatabase() {
        if (dbSimulasiTarifPLN != null && dbSimulasiTarifPLN.isOpen()) {
            dbSimulasiTarifPLN.close();
        }
    }

    private void openReadableDatabase() {
        dbSimulasiTarifPLN = getReadableDatabase();
    }

    private void openWritableDatabase() {
        dbSimulasiTarifPLN = getWritableDatabase();
    }



}