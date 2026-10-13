package org.telegram.ui.Components.glass;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import org.telegram.ui.ActionBar.Theme;

public final class NextogramBackdropDrawable extends Drawable {

    private final Theme.ResourcesProvider resourcesProvider;
    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width;
    private int height;
    private boolean lastDark;
    private boolean hasTheme;

    public NextogramBackdropDrawable(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int newWidth = bounds.width();
        int newHeight = bounds.height();
        boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        if (newWidth <= 0 || newHeight <= 0) {
            return;
        }
        if (width != newWidth || height != newHeight || !hasTheme || lastDark != isDark) {
            width = newWidth;
            height = newHeight;
            updateShaders();
            lastDark = isDark;
            hasTheme = true;
        }

        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        canvas.drawCircle(width * 0.9f, height * 0.08f, Math.max(width, height) * 0.58f, glowPaint);
        canvas.restore();
    }

    private void updateShaders() {
        boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        int top = isDark ? 0xFF101622 : 0xFFF0F5FA;
        int middle = isDark ? 0xFF182233 : 0xFFE7EFF7;
        int bottom = isDark ? 0xFF0C111A : 0xFFF7F9FC;
        backgroundPaint.setShader(new LinearGradient(
                0, 0, 0, height,
                new int[]{top, middle, bottom},
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP
        ));
        glowPaint.setShader(new RadialGradient(
                width * 0.9f,
                height * 0.08f,
                Math.max(width, height) * 0.58f,
                isDark ? new int[]{0x383C8DD0, 0x103C8DD0, 0x003C8DD0} : new int[]{0x443C8DD0, 0x183C8DD0, 0x003C8DD0},
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP
        ));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        width = 0;
        height = 0;
        hasTheme = false;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        backgroundPaint.setAlpha(alpha);
        glowPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        backgroundPaint.setColorFilter(colorFilter);
        glowPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
