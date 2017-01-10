package fi.reuna.poko;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ItemTouchDrawer {

    private Bitmap iconRight;
    private Bitmap iconLeft;
    private int iconMargin;
    private Paint paintRight;
    private Paint paintLeft;

    public ItemTouchDrawer(Context ctx, @Nullable Integer iconRightResId, @Nullable Integer iconLeftResId, @Nullable Integer colorRightResId, @Nullable Integer colorLeftResId, @Nullable Integer iconMargin) {
        Resources res = ctx.getResources();
        this.iconRight = iconRightResId == null ? null : BitmapFactory.decodeResource(res, iconRightResId);
        this.iconLeft = iconLeftResId == null ? null : BitmapFactory.decodeResource(res, iconLeftResId);
        this.iconMargin = iconMargin == null ? Math.round(16 * res.getDisplayMetrics().density) : iconMargin;

        if (colorRightResId != null) {
            paintRight = new Paint();
            paintRight.setColor(ctx.getColor(colorRightResId));
        }

        if (colorLeftResId != null) {
            paintLeft = new Paint();
            paintLeft.setColor(ctx.getColor(colorLeftResId));
        }
    }

    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View item = viewHolder.itemView;

        if (dX > 0) {
            int x0 = item.getLeft();
            int y0 = item.getTop();

            if (paintLeft != null) {
                c.drawRect(x0, y0, x0 + dX, y0 + item.getHeight(), paintLeft);
            }

            if (iconLeft != null) {
                int iconAreaW = iconLeft.getWidth() + 2 * iconMargin;
                int iconAreaH = item.getHeight();
                c.drawBitmap(iconLeft,
                        x0 + (iconAreaW - iconLeft.getWidth()) / 2,
                        y0 + (iconAreaH - iconLeft.getHeight()) / 2, null);
            }

        } else if (dX < 0) {
            int x0 = item.getRight();
            int y0 = item.getTop();

            if (paintRight != null) {
                c.drawRect(x0 + dX, y0, x0, y0 + item.getHeight(), paintRight);
            }

            if (iconRight != null) {
                int iconAreaW = iconRight.getWidth() + 2 * iconMargin;
                int iconAreaH = item.getHeight();
                c.drawBitmap(iconRight,
                        x0 - iconAreaW + (iconAreaW - iconRight.getWidth()) / 2,
                        y0 + (iconAreaH - iconRight.getHeight()) / 2, null);
            }
        }
    }
}