package fi.reuna.poko;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.squareup.otto.Bus;

public class PokoApplication extends Application {

    private static PokoApplication instance;

    public static PokoApplication getInstance() {
        return instance;
    }

    private Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.initialize(this);
        Log.v("log initialized");

        bus = new Bus();
        instance = this;
    }

    public Bus getBus() {
        return bus;
    }

    public static void startTrackPackageActivity(Context ctx, Barcode barcode) {
        String url = String.format(ctx.getResources().getString(R.string.tracking_url_template), barcode.getCode());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        ctx.startActivity(intent);
    }
}
