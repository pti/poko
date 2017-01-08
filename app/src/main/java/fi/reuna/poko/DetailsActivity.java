package fi.reuna.poko;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BARCODE = "barcode";

    private Barcode barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        barcode = intent.getParcelableExtra(EXTRA_BARCODE);

        BarcodeImageView img = (BarcodeImageView) findViewById(R.id.details_barcode_image);
        img.setBarcode(barcode.getCode(), getBarcodeFormat(barcode.getBarcodeType()));

        TextView tv = (TextView) findViewById(R.id.details_code_text);
        tv.setText(barcode.getCode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.details_copy) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(getString(R.string.details_code_clipboard_label), barcode.getCode());
            cm.setPrimaryClip(clipData);

            String text = getString(R.string.details_code_copied_text);
            Toast toast = Toast.makeText(DetailsActivity.this, text, Toast.LENGTH_SHORT);
            toast.show();
            return true;

        } else if (item.getItemId() == R.id.details_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, barcode.getCode());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;

        } else if (item.getItemId() == R.id.details_delete) {
            BarcodeManager.instance.removeBarcode(barcode);
            finish();
            return true;

        } else if (item.getItemId() == R.id.details_track) {
            PokoApplication.startTrackPackageActivity(this, barcode);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static BarcodeFormat getBarcodeFormat(BarcodeType type) {

        switch (type) {
            case Code39:
                return BarcodeFormat.CODE_39;

            default:
                throw new IllegalArgumentException("Barcode type is not supported");
        }
    }
}
