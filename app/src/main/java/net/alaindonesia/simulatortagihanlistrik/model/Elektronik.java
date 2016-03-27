package net.alaindonesia.simulatortagihanlistrik.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Elektronik implements Parcelable {

    private int idElektronik;
    private int dayaWatt;
    private String namaElektronik;


    public Elektronik(int idElektronik, int dayaWatt, String namaElektronik) {
        this.idElektronik = idElektronik;
        this.dayaWatt = dayaWatt;
        this.namaElektronik = namaElektronik;
    }

    public Elektronik(){
        idElektronik=0;
    }


    public int getIdElektronik() {
        return idElektronik;
    }

    public void setIdElektronik(int idElektronik) {
        this.idElektronik = idElektronik;
    }

    public int getDayaWatt() {
        return dayaWatt;
    }

    public void setDayaWatt(int dayaWatt) {
        this.dayaWatt = dayaWatt;
    }

    public String getNamaElektronik() {
        return namaElektronik;
    }

    public void setNamaElektronik(String namaElektronik) {
        this.namaElektronik = namaElektronik;
    }

    protected Elektronik(Parcel in) {
        idElektronik = in.readInt();
        dayaWatt = in.readInt();
        namaElektronik = in.readString();
    }

    public String toString(){
        return namaElektronik;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idElektronik);
        dest.writeInt(dayaWatt);
        dest.writeString(namaElektronik);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Elektronik> CREATOR = new Parcelable.Creator<Elektronik>() {
        @Override
        public Elektronik createFromParcel(Parcel in) {
            return new Elektronik(in);
        }

        @Override
        public Elektronik[] newArray(int size) {
            return new Elektronik[size];
        }
    };
}
