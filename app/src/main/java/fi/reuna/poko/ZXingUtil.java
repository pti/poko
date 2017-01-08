package fi.reuna.poko;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Size;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ZXingUtil {

    public static BitMatrix encode(String data, BarcodeFormat format, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        return writer.encode(data, format, width, height);
    }

    public static Bitmap getBitmap(BitMatrix matrix, BarcodeFormat format, int foregroundColor, Size size, @Nullable Padding padding) {
        long t0 = System.nanoTime();
        final int[] rect = matrix.getEnclosingRectangle();
        final int firstX = padding.left + rect[0];
        final int firstY = padding.top + rect[1];
        final int contentW = rect[2];
        final int contentH = rect[3];
        Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);

        if (isFormat1D(format)) {
            // All the image rows are identical so just generate 1 row of pixels, and copy that N times.
            int[] pixels = new int[contentW];

            for (int x = 0; x < contentW; x++) {
                pixels[x] = matrix.get(rect[0] + x, rect[1]) ? foregroundColor : Color.TRANSPARENT;
            }

            int lastY = firstY + (size.getHeight() - padding.top - padding.bottom);

            for (int y = firstY; y < lastY; y++) {
                bitmap.setPixels(pixels, 0, size.getWidth(), firstX, y, contentW, 1);
            }

        } else {
            int[] pixels = new int[contentW * contentH];

            for (int y = 0; y < contentH; y++) {

                for (int x = 0; x < contentW; x++) {
                    pixels[y * contentW + x] = matrix.get(rect[0] + x, rect[1] + y) ? foregroundColor : Color.TRANSPARENT;
                }
            }

            bitmap.setPixels(pixels, 0, contentW, firstX, firstY, contentW, contentH);
        }

        long t1 = System.nanoTime();
        Log.v("took %d ms", (t1 - t0) / 1000000);
        return bitmap;
    }

    public static Bitmap getBitmap(String data, BarcodeFormat format, int foregroundColor, Size size, @Nullable Padding padding) {

        try {
            int contentWidth = size.getWidth() - padding.right - padding.left;
            int contentHeight = size.getHeight() - padding.top - padding.bottom;
            BitMatrix matrix = encode(data, format, contentWidth, contentHeight);
            return getBitmap(matrix, format, foregroundColor, size, padding);
        } catch (WriterException e) {
            return null;
        }
    }

    public static boolean isFormat1D(BarcodeFormat format) {

        switch (format) {
            case CODABAR:
            case CODE_39:
            case CODE_93:
            case CODE_128:
            case EAN_8:
            case EAN_13:
            case ITF:
            case UPC_A:
            case UPC_E:
                return true;

            default:
                return false;
        }
    }
}
