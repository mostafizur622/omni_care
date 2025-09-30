package com.srapp.FaceDetection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleLivenessOverlayView extends View {

    public enum Phase { NEED_OPEN, NEED_CLOSED, NEED_REOPEN, VERIFIED }

    private final Paint scrimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float cx, cy, radius;
    private float ringStrokePx;
    private float gapDeg = 8f;        // সেগমেন্টের মাঝে গ্যাপ
    private int currentStep = 1;      // 1..3
    private int completed = 0;        // 0..3

    public CircleLivenessOverlayView(Context context) {
        this(context, null);
    }

    public CircleLivenessOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        scrimPaint.setStyle(Paint.Style.FILL);
        scrimPaint.setColor(0x99000000);

        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        ringStrokePx = dp(12);

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(ringStrokePx);
        trackPaint.setColor(Color.parseColor("#44FFFFFF"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        activePaint.setStyle(Paint.Style.STROKE);
        activePaint.setStrokeWidth(ringStrokePx);
        activePaint.setColor(Color.parseColor("#18F3C6")); // teal-ish
        activePaint.setStrokeCap(Paint.Cap.ROUND);

        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(ringStrokePx);
        currentPaint.setColor(Color.parseColor("#FFFFFF")); // সাদা একটু হাইলাইট
        currentPaint.setAlpha(220);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void updatePhase(Phase phase) {
        switch (phase) {
            case NEED_OPEN:
                currentStep = 1; completed = 0; break;
            case NEED_CLOSED:
                currentStep = 2; completed = 1; break;
            case NEED_REOPEN:
                currentStep = 3; completed = 2; break;
            case VERIFIED:
                currentStep = 3; completed = 3; break;
        }
        invalidate();
    }

    private float dp(float v) {
        return v * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cx = w / 2f;
        cy = h / 2f;
        float min = Math.min(w, h);
        // রিং + গ্যাপ মেনে রেডিয়াস ঠিক করুন
        radius = min / 2f - dp(16) - ringStrokePx / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1) ডার্ক স্ক্রিম
        canvas.drawRect(0, 0, getWidth(), getHeight(), scrimPaint);

        // 2) সার্কুলার উইন্ডো কাটুন
        canvas.drawCircle(cx, cy, radius, clearPaint);

        // 3) রিং ড্র করতে বাহিরে বাউন্ডিং রেক্ট
        RectF oval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);

        // 4) ৩ সেগমেন্টের বেস ট্র্যাক
        float seg = 360f / 3f; // 120 deg
        for (int i = 0; i < 3; i++) {
            float start = -90f + i * seg + gapDeg / 2f;
            float sweep = seg - gapDeg;
            canvas.drawArc(oval, start, sweep, false, trackPaint);
        }

        // 5) কমপ্লিটেড সেগমেন্টগুলো
        for (int i = 0; i < completed; i++) {
            float start = -90f + i * seg + gapDeg / 2f;
            float sweep = seg - gapDeg;
            canvas.drawArc(oval, start, sweep, false, activePaint);
        }

        // 6) কারেন্ট স্টেপ হাইলাইট (কমপ্লিটেড না হলে)
        if (completed < 3) {
            int idx = currentStep - 1;
            float start = -90f + idx * seg + gapDeg / 2f;
            float sweep = seg - gapDeg;
            canvas.drawArc(oval, start, sweep, false, currentPaint);
        }

        // (ঐচ্ছিক) হালকা “স্ক্যান লাইন”/পালস যোগ করতে চাইলে এখানে আঁকতে পারেন
    }
}
