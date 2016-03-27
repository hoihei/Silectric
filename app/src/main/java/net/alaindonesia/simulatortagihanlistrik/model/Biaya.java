package net.alaindonesia.simulatortagihanlistrik.model;

public class Biaya {

    private int idBiaya;
    private String batasDayaBawah;
    private String batasDayaAtas;
    private String keteranganBatasDaya;
    private long harga;

    public Biaya(int id_biaya, String batasDayaBawah, String batasDayaAtas, String keteranganBatasDaya, long harga) {
        this.idBiaya = id_biaya;
        this.batasDayaBawah = batasDayaBawah;
        this.batasDayaAtas = batasDayaAtas;
        this.keteranganBatasDaya = keteranganBatasDaya;
        this.harga = harga;
    }

    @Override
    public String toString() {
        return this.keteranganBatasDaya;
    }


    public int getIdBiaya() {
        return idBiaya;
    }

    public void setIdBiaya(int idBiaya) {
        this.idBiaya = idBiaya;
    }

    public String getBatasDayaBawah() {
        return batasDayaBawah;
    }

    public void setBatasDayaBawah(String batasDayaBawah) {
        this.batasDayaBawah = batasDayaBawah;
    }

    public String getBatasDayaAtas() {
        return batasDayaAtas;
    }

    public void setBatasDayaAtas(String batasDayaAtas) {
        this.batasDayaAtas = batasDayaAtas;
    }

    public void setKeteranganBatasDaya(String keteranganBatasDaya) {
        this.keteranganBatasDaya = keteranganBatasDaya;
    }

    public long getHarga() {
        return harga;
    }

    public void setHarga(long harga) {
        this.harga = harga;
    }
}
