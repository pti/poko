package fi.reuna.poko;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Size;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;

public class BarcodeImageView extends ImageView {

    String barcodeData;
    BarcodeFormat barcodeFormat;
    GenerateBarcodeImageTask task;

    public BarcodeImageView(Context context) {
        super(context);
    }

    public BarcodeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BarcodeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        if (barcodeData == null) {
            return;
        }

        if (getScaleType() == ScaleType.FIT_XY && ZXingUtil.isFormat1D(barcodeFormat)) {
            // All the image rows are identical so just stretch the image vertically.
            h = 1;
        }

        if (task != null) {
            task.cancel(true);
        }

        int paddingH = 0;
        int paddingV = 0;

        if (getMaxWidth() < w) {
            paddingH = (w - getMaxWidth()) / 2;
        }

        if (getMaxHeight() < h) {
            paddingV = (h - getMaxHeight()) / 2;
        }

        task = new GenerateBarcodeImageTask();
        task.execute(w, h, paddingV, paddingH, paddingV, paddingH);
    }

    public void setBarcode(String data, BarcodeFormat format) {
        this.barcodeData = data;
        this.barcodeFormat = format;
    }

    class GenerateBarcodeImageTask extends AsyncTask<Integer, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Size size = new Size(params[0], params[1]);
            Padding padding = new Padding(params[2], params[3], params[4], params[5]);
            return ZXingUtil.getBitmap(barcodeData, barcodeFormat, Color.BLACK, size, padding);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setImageBitmap(bitmap);
        }
    }
}
