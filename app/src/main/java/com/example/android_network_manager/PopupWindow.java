package com.example.android_network_manager;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

public class PopupWindow {
    private int LAYOUT_TYPE;
    // declaring required variables
    private Context context;
    private View mView;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private int dBm = 0;
    private int level = 0;

    public PopupWindow(Context context){
        this.context=context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.popup_window, null);

        mView.findViewById(R.id.close_widget_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        floatWindowLayoutParam = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        floatWindowLayoutParam.gravity = Gravity.TOP | Gravity.LEFT;
        floatWindowLayoutParam.x = width-100;
        floatWindowLayoutParam.y = 0;

        mView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View ve, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        px = event.getRawX();
                        py = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);
                        // updated parameter is applied to the WindowManager
                        mWindowManager.updateViewLayout(mView, floatWindowLayoutUpdateParam);
                        break;
                }
                return false;
            }
        });

    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, floatWindowLayoutParam);
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Handler handler = new Handler();
        Runnable runnableUpdate = new Runnable() {
            @Override
            public void run() {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 100);
                updateWifiBubble(wifiInfo.getRssi(), level);

                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnableUpdate);
    }

    public void close() {

        try {
            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup)mView.getParent()).removeAllViews();

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2",e.toString());
        }
    }

    private void updateWifiBubble(int rssi, int level){
        if(rssi > -60) {
            mView.findViewById(R.id.green_bubble).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.orange_bubble).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.red_bubble).setVisibility(View.INVISIBLE);
        } else if(rssi <= -60 && rssi >= -80) {
            mView.findViewById(R.id.green_bubble).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.orange_bubble).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.red_bubble).setVisibility(View.INVISIBLE);
        } else if(rssi < -80) {
            mView.findViewById(R.id.green_bubble).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.orange_bubble).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.red_bubble).setVisibility(View.VISIBLE);
        }
        ((TextView) mView.findViewById(R.id.green_bubble)).setText(level+"%\n"+rssi);
        ((TextView) mView.findViewById(R.id.orange_bubble)).setText(level+"%\n"+rssi);
        ((TextView) mView.findViewById(R.id.red_bubble)).setText(level+"%\n"+rssi);
    }
}
