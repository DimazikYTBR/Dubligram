package org.telegram.ui.Components.glass;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public final class NextogramGlassDrawable extends Drawable {

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Theme.ResourcesProvider resourcesProvider;
    private final float radius;
    private final float insetHorizontal;
    private final float insetVertical;
    private int drawableAlpha = 255;

    public NextogramGlassDrawable(Theme.ResourcesProvider resourcesProvider, float radiusDp, float insetHorizontalDp, float insetVerticalDp) {
        this.resourcesProvider = resourcesProvider;
        radius = AndroidUtilities.dp(radiusDp);
        insetHorizontal = AndroidUtilities.dp(insetHorizontalDp);
        insetVertical = AndroidUtilities.dp(insetVerticalDp);
        fillPaint.setStyle(Paint.Style.FILL);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(AndroidUtilities.dp(1));
    }

    public static Drawable createSelectable(Theme.ResourcesProvider resourcesProvider, float radiusDp, float insetHorizontalDp, float insetVerticalDp) {
        boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        int rippleColor = isDark ? 0x33FFFFFF : 0x22000000;
        GradientDrawable mask = new GradientDrawable();
        mask.setColor(Color.WHITE);
        mask.setCornerRadius(AndroidUtilities.dp(radiusDp));
        return new RippleDrawable(
                ColorStateList.valueOf(rippleColor),
                new NextogramGlassDrawable(resourcesProvider, radiusDp, insetHorizontalDp, insetVerticalDp),
                mask
        );
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        rect.set(
                bounds.left + insetHorizontal,
                bounds.top + insetVertical,
                bounds.right - insetHorizontal,
                bounds.bottom - insetVertical
        );
        if (rect.isEmpty()) {
            return;
        }

        boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        fillPaint.setColor(withAlpha(isDark ? 0x3BDAE7FF : 0xD9FFFFFF, drawableAlpha));
        strokePaint.setColor(withAlpha(isDark ? 0x55FFFFFF : 0xA6FFFFFF, drawableAlpha));

        canvas.drawRoundRect(rect, radius, radius, fillPaint);
        canvas.drawRoundRect(rect, radius, radius, strokePaint);
    }

    private int withAlpha(int color, int alpha) {
        return Color.argb(Color.alpha(color) * alpha / 255, Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    public void setAlpha(int alpha) {
        drawableAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
        strokePaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
