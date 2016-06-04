package net.alaindonesia.silectric.model;


import android.os.Parcel;
import android.os.Parcelable;
public class Usage implements Parcelable {

    private int idUsage;
    private Electronic electronic;
    private int numberOfElectronic;
    private double totalUsageHoursPerDay;
    private int totalWattagePerDay;



    public Usage(Electronic electronic, int numberOfElectronic){
        this.electronic = electronic;
        this.numberOfElectronic = numberOfElectronic;
    }

    public Usage(int idUsage, Electronic electronic, int numberOfElectronic, int totalWattagePerDay, double totalUsageHoursPerDay) {
        this.idUsage = idUsage;
        this.electronic = electronic;
        this.numberOfElectronic = numberOfElectronic;
        this.totalUsageHoursPerDay = totalUsageHoursPerDay;
        this.totalWattagePerDay = totalWattagePerDay;
    }

    public int getIdUsage() {
        return idUsage;
    }

    public void setIdUsage(int idUsage) {
        this.idUsage = idUsage;
    }


    public int getNumberOfElectronic() {
        return numberOfElectronic;
    }

    public void setNumberOfElectronic(int numberOfElectronic) {
        this.numberOfElectronic = numberOfElectronic;
    }

    public Electronic getElectronic() {
        return electronic;
    }

    public void setElectronic(Electronic electronic) {
        this.electronic = electronic;
    }

    public double getTotalUsageHoursPerDay() {
        return totalUsageHoursPerDay;
    }

    public void setTotalUsageHoursPerDay(double totalUsageHoursPerDay) {
        this.totalUsageHoursPerDay = totalUsageHoursPerDay;
    }

    public int getTotalWattagePerDay() {
        return totalWattagePerDay;
    }

    public void setTotalWattagePerDay(int totalWattagePerDay) {
        this.totalWattagePerDay = totalWattagePerDay;
    }

    protected Usage(Parcel in) {
        idUsage = in.readInt();
        electronic = (Electronic) in.readValue(Electronic.class.getClassLoader());
        numberOfElectronic = in.readInt();
        totalUsageHoursPerDay = in.readDouble();
        totalWattagePerDay = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsage);
        dest.writeValue(electronic);
        dest.writeInt(numberOfElectronic);
        dest.writeDouble(totalUsageHoursPerDay);
        dest.writeInt(totalWattagePerDay);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Usage> CREATOR = new Parcelable.Creator<Usage>() {
        @Override
        public Usage createFromParcel(Parcel in) {
            return new Usage(in);
        }

        @Override
        public Usage[] newArray(int size) {
            return new Usage[size];
        }
    };
}