package net.alaindonesia.simulatortagihanlistrik.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Pemakaian implements Parcelable {

    private int idPemakaian;
    private Elektronik elektronik;
    private double jumlahPemakaianJam;
    private int jumlahBarang;


    public Pemakaian(){
        this.idPemakaian=0;
    }

    public Pemakaian(int idPemakaian, Elektronik elektronik, double jumlahPemakaianJam, int jumlahBarang) {
        this.idPemakaian = idPemakaian;
        this.elektronik = elektronik;
        this.jumlahPemakaianJam = jumlahPemakaianJam;
        this.jumlahBarang = jumlahBarang;
    }



    public int getIdPemakaian() {
        return idPemakaian;
    }

    public void setIdPemakaian(int idPemakaian) {
        this.idPemakaian = idPemakaian;
    }


    public double getJumlahPemakaianJam() {
        return jumlahPemakaianJam;
    }

    public void setJumlahPemakaianJam(double jumlahPemakaianJam) {
        this.jumlahPemakaianJam = jumlahPemakaianJam;
    }



    public int getJumlahBarang() {
        return jumlahBarang;
    }

    public void setJumlahBarang(int jumlahBarang) {
        this.jumlahBarang = jumlahBarang;
    }

    public Elektronik getElektronik() {
        return elektronik;
    }

    public void setElektronik(Elektronik elektronik) {
        this.elektronik = elektronik;
    }

    protected Pemakaian(Parcel in) {
        idPemakaian = in.readInt();
        elektronik = (Elektronik) in.readValue(Elektronik.class.getClassLoader());
        jumlahPemakaianJam = in.readDouble();
        jumlahBarang = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPemakaian);
        dest.writeValue(elektronik);
        dest.writeDouble(jumlahPemakaianJam);
        dest.writeInt(jumlahBarang);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Pemakaian> CREATOR = new Parcelable.Creator<Pemakaian>() {
        @Override
        public Pemakaian createFromParcel(Parcel in) {
            return new Pemakaian(in);
        }

        @Override
        public Pemakaian[] newArray(int size) {
            return new Pemakaian[size];
        }
    };
}