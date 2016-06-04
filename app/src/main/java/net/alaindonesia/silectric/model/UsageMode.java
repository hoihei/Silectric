package net.alaindonesia.silectric.model;


public class UsageMode {
    private int idUsageMode;
    private String usageModeName;

    public UsageMode(int idUsageMode, String usageModeName) {
        this.idUsageMode = idUsageMode;
        this.usageModeName = usageModeName;
    }


    public int getIdUsageMode() {
        return idUsageMode;
    }

    public void setIdUsageMode(int idUsageMode) {
        this.idUsageMode = idUsageMode;
    }

    public String getUsageModeName() {
        return usageModeName;
    }

    public void setUsageModeName(String usageModeName) {
        this.usageModeName = usageModeName;
    }

    public String toString(){
        return usageModeName;
    }
}
