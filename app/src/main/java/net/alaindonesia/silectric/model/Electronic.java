package net.alaindonesia.silectric.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Electronic implements Parcelable {

    private int idElectronic;
    private String electronicName;


    public Electronic(int idElectronic, String electronicName) {
        this.idElectronic = idElectronic;
        this.electronicName = electronicName;
    }

    public Electronic(){
        this.idElectronic =0;
        this.electronicName="";
    }


    public int getIdElectronic() {
        return idElectronic;
    }

        public String getElectronicName() {
        return electronicName;
    }

    public void setElectronicName(String electronicName) {
        this.electronicName = electronicName;
    }

    protected Electronic(Parcel in) {
        idElectronic = in.readInt();
        electronicName = in.readString();
    }

    public String toString(){
        return electronicName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idElectronic);
        dest.writeString(electronicName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Electronic> CREATOR = new Parcelable.Creator<Electronic>() {
        @Override
        public Electronic createFromParcel(Parcel in) {
            return new Electronic(in);
        }

        @Override
        public Electronic[] newArray(int size) {
            return new Electronic[size];
        }
    };
}
