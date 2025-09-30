package com.srapp.FaceDetection;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.List;

public class ARFaceOverlayView extends View {
    private @Nullable Face face;
    private int imgW, imgH, rotation;
    private boolean isFront = true;

    private final Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint cornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scanPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float scanY = 0f;

    public ARFaceOverlayView(Context c, AttributeSet a) {
        super(c, a);

        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(3f);
        boxPaint.setColor(Color.parseColor("#55FFEE"));

        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeWidth(8f);
        cornerPaint.setColor(Color.parseColor("#22FFFF"));

        maskPaint.setStyle(Paint.Style.FILL);
        maskPaint.setColor(Color.parseColor("#33FFFFFF"));

        scanPaint.setStyle(Paint.Style.STROKE);
        scanPaint.setStrokeWidth(2f);
        scanPaint.setColor(Color.parseColor("#88A0FFE0"));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setShadowLayer(4, 0, 0, Color.BLACK);
    }

    public void setFaceData(@Nullable Face f, int imageWidth, int imageHeight, int rotationDeg, boolean front) {
        this.face = f;
        this.imgW = imageWidth;
        this.imgH = imageHeight;
        this.rotation = rotationDeg;
        this.isFront = front;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // স্ক্যানিং লাইন
        scanY += 6f;
        if (scanY > getHeight()) scanY = 0f;
        canvas.drawLine(0, scanY, getWidth(), scanY, scanPaint);
        postInvalidateOnAnimation();

        if (face == null || imgW == 0 || imgH == 0) return;

        RectF box = mapRect(face.getBoundingBox());
        canvas.drawRect(box, boxPaint);
        drawCorners(canvas, box, 28);

        // AI identity mask
        RectF oval = new RectF(box.left, box.top, box.right, box.bottom);
        canvas.drawOval(oval, maskPaint);

        Integer tid = face.getTrackingId();
        if (tid != null) {
            canvas.drawText("ID #" + tid, box.left + 8, box.top - 12, textPaint);
        }

        // contour points for AI vibe
        List<FaceContour> contours = face.getAllContours();
        if (contours != null) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.parseColor("#66FFBB33"));
            for (FaceContour c : contours) {
                for (PointF pt : c.getPoints()) {
                    PointF vpt = mapPoint(pt.getClass().getModifiers(), pt.getClass().getModifiers());
                    canvas.drawCircle(vpt.x, vpt.y, 2.5f, p);
                }
            }
        }
    }

    private void drawCorners(Canvas c, RectF r, float len) {
        // TL
        c.drawLine(r.left, r.top, r.left + len, r.top, cornerPaint);
        c.drawLine(r.left, r.top, r.left, r.top + len, cornerPaint);
        // TR
        c.drawLine(r.right, r.top, r.right - len, r.top, cornerPaint);
        c.drawLine(r.right, r.top, r.right, r.top + len, cornerPaint);
        // BL
        c.drawLine(r.left, r.bottom, r.left + len, r.bottom, cornerPaint);
        c.drawLine(r.left, r.bottom, r.left, r.bottom - len, cornerPaint);
        // BR
        c.drawLine(r.right, r.bottom, r.right - len, r.bottom, cornerPaint);
        c.drawLine(r.right, r.bottom, r.right, r.bottom - len, cornerPaint);
    }

    // ===== Center-crop + mirror mapping =====
    private PointF mapPoint(float x, float y) {
        float vw = getWidth(), vh = getHeight();
        float iw = imgW, ih = imgH;

        float scale = Math.max(vw / iw, vh / ih);
        float dx = (vw - iw * scale) / 2f;
        float dy = (vh - ih * scale) / 2f;

        float vx = x * scale + dx;
        float vy = y * scale + dy;

        if (isFront) vx = vw - vx;
        return new PointF(vx, vy);
    }

    private RectF mapRect(Rect r) {
        PointF tl = mapPoint(r.left,  r.top);
        PointF br = mapPoint(r.right, r.bottom);
        return new RectF(tl.x, tl.y, br.x, br.y);
    }
}
