package net.alaindonesia.silectric.model;


public class TimeUsage {
    private int idTimeUsage;
    private int hours;
    private int minutes;
    private int idUsage;
    private int wattage;
    private int idUsageMode;
    private UsageMode usageMode;
    private boolean isNewTimeUsage;
    private boolean isNewUsage;


    public TimeUsage(int idTimeUsage, int idUsage, int idUsageMode, int wattage, int hours, int minutes, UsageMode usageMode) {
        this.idTimeUsage = idTimeUsage;
        this.hours = hours;
        this.minutes = minutes;
        this.idUsage = idUsage;
        this.wattage = wattage;
        this.idUsageMode = idUsageMode;
        this.usageMode = usageMode;
        this.isNewTimeUsage = false;
    }


    public TimeUsage(boolean isNewUsage, int idUsageMode, int wattage, int hours, int minutes, UsageMode usageMode) {
        this.hours = hours;
        this.minutes = minutes;
        this.wattage = wattage;
        this.idUsageMode = idUsageMode;
        this.isNewUsage = isNewUsage;
        this.isNewTimeUsage = true;
        this.usageMode = usageMode;
    }

    public int getIdTimeUsage() {
        return idTimeUsage;
    }


    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getIdUsage() {
        return idUsage;
    }

    public void setIdUsage(int idUsage) {
        this.idUsage = idUsage;
    }

    public int getWattage() {
        return wattage;
    }

    public void setWattage(int wattage) {
        this.wattage = wattage;
    }

    public int getIdUsageMode() {
        return idUsageMode;
    }

    public void setIdUsageMode(int idUsageMode) {
        this.idUsageMode = idUsageMode;
    }

    public boolean isNewTimeUsage() {
        return isNewTimeUsage;
    }

    public void setIsNewTimeUsage(boolean isNewTimeUsage) {
        this.isNewTimeUsage = isNewTimeUsage;
    }

    public boolean isNewUsage() {
        return isNewUsage;
    }

    public void setIsNewUsage(boolean isNewUsage) {
        this.isNewUsage = isNewUsage;
    }

    public UsageMode getUsageMode() {
        return usageMode;
    }

    public void setUsageMode(UsageMode usageMode) {
        this.usageMode = usageMode;
    }
}
