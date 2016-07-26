package net.alaindonesia.silectric.model;


public class ElectronicTimeUsageTemplate {
    private int idElectronicTimeUsageTemplate;
    private int hours;
    private int minutes;
    private int idElectronic;
    private int wattage;
    private int idUsageMode;
    private UsageMode usageMode;
    private boolean isNewElectronicTimeUsageTemplate;
    private boolean isNewElectronic;


    public ElectronicTimeUsageTemplate(int idElectronicTimeUsageTemplate, int idElectronic, int idUsageMode, int wattage, int hours, int minutes, UsageMode usageMode) {
        this.idElectronicTimeUsageTemplate = idElectronicTimeUsageTemplate;
        this.hours = hours;
        this.minutes = minutes;
        this.idElectronic = idElectronic;
        this.wattage = wattage;
        this.idUsageMode = idUsageMode;
        this.usageMode = usageMode;
        this.isNewElectronicTimeUsageTemplate = false;
    }


    public ElectronicTimeUsageTemplate(boolean isNewElectronic, int idUsageMode, int wattage, int hours, int minutes, UsageMode usageMode) {
        this.hours = hours;
        this.minutes = minutes;
        this.wattage = wattage;
        this.idUsageMode = idUsageMode;
        this.isNewElectronic = isNewElectronic;
        this.isNewElectronicTimeUsageTemplate = true;
        this.usageMode = usageMode;
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

    public int getIdElectronic() {
        return idElectronic;
    }

    public void setIdElectronic(int idElectronic) {
        this.idElectronic = idElectronic;
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

    public boolean isNewElectronicTimeUsageTemplate() {
        return isNewElectronicTimeUsageTemplate;
    }

    public void setIsNewTimeUsage(boolean isNewTimeUsage) {
        this.isNewElectronicTimeUsageTemplate = isNewTimeUsage;
    }

    public boolean isNewElectronic() {
        return isNewElectronic;
    }

    public void setIsNewUsage(boolean isNewUsage) {
        this.isNewElectronic = isNewUsage;
    }

    public UsageMode getUsageMode() {
        return usageMode;
    }

    public void setUsageMode(UsageMode usageMode) {
        this.usageMode = usageMode;
    }
}
