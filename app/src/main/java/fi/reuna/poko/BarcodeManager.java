package fi.reuna.poko;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BarcodeManager {

    public enum BarcodeEventType {
        Added,
        Removed
    }

    public static class BarcodeEvent {

        public final BarcodeEventType type;
        public final Barcode barcode;
        public final int position;

        public BarcodeEvent(BarcodeEventType type, Barcode barcode, int position) {
            this.type = type;
            this.barcode = barcode;
            this.position = position;
        }
    }

    private static final String PREF_BARCODES = "Barcodes";

    public static final BarcodeManager instance = new BarcodeManager();

    private ArrayList<Barcode> barcodes;

    private BarcodeManager() {
        load();
    }

    public List<Barcode> getBarcodes() {
        return barcodes;
    }

    public void addBarcode(Barcode barcode) {

        if (barcodes.contains(barcode)) {
            return;
        }

        barcodes.add(barcode);
        sortBarcodes(barcodes);
        save();
        int index = getBarcodeIndex(barcode);
        PokoApplication.getInstance().getBus().post(new BarcodeEvent(BarcodeEventType.Added, barcode, index));
    }

    public void removeBarcode(Barcode barcode) {
        int index = getBarcodeIndex(barcode);

        if (index > -1) {
            barcodes.remove(index);
            save();
            PokoApplication.getInstance().getBus().post(new BarcodeEvent(BarcodeEventType.Removed, barcode, index));
        }
    }

    private int getBarcodeIndex(Barcode barcode) {
        return barcodes.indexOf(barcode);
    }

    private void save() {
        String json = getBarcodesAsJson(barcodes);
        SharedPreferences prefs = getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_BARCODES, json);
        editor.apply();
    }

    private void load() {
        SharedPreferences prefs = getPreferences();
        String barcodesJson = prefs.getString(PREF_BARCODES, null);

        if (barcodesJson == null) {
            barcodes = new ArrayList<>(0);
        } else {
            barcodes = new ArrayList<>(parseBarcodes(barcodesJson));
            sortBarcodes(barcodes);
        }
    }

    private SharedPreferences getPreferences() {
        Context ctx = PokoApplication.getInstance();
        return ctx.getSharedPreferences("fi.reuna.poko.PREFERENCES", Context.MODE_PRIVATE);
    }

    private static String getBarcodesAsJson(List<Barcode> barcodes) {
        return new Gson().toJson(barcodes.toArray(new Barcode[barcodes.size()]));
    }

    private static List<Barcode> parseBarcodes(String json) {
        return Arrays.asList(new Gson().fromJson(json, Barcode[].class));
    }

    private static void sortBarcodes(ArrayList<Barcode> barcodes) {
        Collections.sort(barcodes, new Comparator<Barcode>() {
            @Override
            public int compare(Barcode lhs, Barcode rhs) {
                return rhs.getCreated().compareTo(lhs.getCreated());
            }
        });
    }
}
