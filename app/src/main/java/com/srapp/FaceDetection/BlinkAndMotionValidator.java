package com.srapp.FaceDetection;


import androidx.annotation.Nullable;

public class BlinkAndMotionValidator {
    public enum Phase { NEED_OPEN, NEED_CLOSED, NEED_REOPEN, VERIFIED }

    private static final float OPEN_TH = 0.80f;
    private static final float CLOSED_TH = 0.30f;
    private static final int   REQ_OPEN_FRAMES_BEFORE = 5;
    private static final int   REQ_CLOSED_FRAMES      = 3;
    private static final int   REQ_REOPEN_FRAMES      = 5;
    private static final long  TIMEOUT_MS             = 7000;

    private Phase phase = Phase.NEED_OPEN;
    private int openCount = 0, closedCount = 0, reopenCount = 0;
    private long windowStart = System.currentTimeMillis();
    private Integer faceId = null;
    private boolean captured = false;

    public void reset() {
        phase = Phase.NEED_OPEN;
        openCount = closedCount = reopenCount = 0;
        windowStart = System.currentTimeMillis();
        faceId = null;
        captured = false;
    }

    public void markCaptured() { captured = true; }
    public boolean alreadyCaptured() { return captured; }
    public Phase getPhase() { return phase; }

    public void update(@Nullable Integer trackingId, float leftProb, float rightProb) {
        long now = System.currentTimeMillis();
        if (now - windowStart > TIMEOUT_MS) reset();

        if (trackingId == null) { reset(); return; }
        if (faceId == null) faceId = trackingId;
        else if (!faceId.equals(trackingId)) { reset(); faceId = trackingId; }

        if (leftProb < 0 || rightProb < 0) {
            openCount = closedCount = reopenCount = 0;
            return;
        }

        boolean bothOpen = leftProb > OPEN_TH && rightProb > OPEN_TH;
        boolean bothClosed = leftProb < CLOSED_TH && rightProb < CLOSED_TH;

        switch (phase) {
            case NEED_OPEN:
                if (bothOpen) { if (++openCount >= REQ_OPEN_FRAMES_BEFORE) phase = Phase.NEED_CLOSED; }
                else openCount = 0;
                break;

            case NEED_CLOSED:
                if (bothClosed) { if (++closedCount >= REQ_CLOSED_FRAMES) phase = Phase.NEED_REOPEN; }
                else closedCount = 0;
                break;

            case NEED_REOPEN:
                if (bothOpen) { if (++reopenCount >= REQ_REOPEN_FRAMES) phase = Phase.VERIFIED; }
                else reopenCount = 0;
                break;

            case VERIFIED:
                break;
        }
    }
}
