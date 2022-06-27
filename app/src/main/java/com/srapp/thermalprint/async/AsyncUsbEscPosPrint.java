package com.srapp.thermalprint.async;

import android.content.Context;

import com.srapp.Util.PrintedListener;

public class AsyncUsbEscPosPrint extends AsyncEscPosPrint {
    public AsyncUsbEscPosPrint(Context context, PrintedListener printedListener) {
        super(context,printedListener);
    }
}
