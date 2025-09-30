package com.srapp.FaceDetection;

import android.app.Activity;
import android.content.Intent;

public class LivenessStarter {
    public static final int REQ_LIVENESS = 9101;

    public static void launch(Activity activity) {
        Intent i = new Intent(activity, LivenessOverlayActivity.class);
        activity.startActivityForResult(i, REQ_LIVENESS);
    }
}