package net.alaindonesia.simulatortagihanlistrik;


public class Kalkulasi {

    public static double hitungBiayaTotal(double totalPemakaianKwh){

        double biayaBeban=0;
        double biayaTotal=0;

        double biayaPemakaianListrikRpPerKwh = 0;
        double biayaAbodemen = 0;

        biayaBeban = totalPemakaianKwh * biayaPemakaianListrikRpPerKwh;
        biayaTotal = biayaAbodemen + biayaBeban;

        return biayaTotal;

    }



//    private void kalkulasikan(){
//
//
//        EditText totalPemakaianEditText = (EditText) findViewById(R.id.totalPemakaianEditText);
//        double totalPemakaianKwh = 0;
//        try {
//            totalPemakaianKwh = Double.parseDouble(totalPemakaianEditText.getText().toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        double biayaTotal = Kalkulasi.hitungBiayaTotal(totalPemakaianKwh);
//
//        EditText biayaTotalEditText = (EditText) findViewById(R.id.biayaTotalEditText);
////
//        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
//        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
//        formatRp.setCurrencySymbol("Rp. ");
//        formatRp.setGroupingSeparator('.');
//        kursIndonesia.setDecimalFormatSymbols(formatRp);
//        kursIndonesia.setDecimalSeparatorAlwaysShown(false);
//
//        biayaTotalEditText.setText(kursIndonesia.format(biayaTotal));
//
//    }
}
