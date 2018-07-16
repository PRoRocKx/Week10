package example.com.week10;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Evgen on 16.07.2018.
 */

abstract class TimerActivity extends AppCompatActivity {

    public static final String TIMER_ACTION = "example.com.week10.TIMER_ACTION";
    public static final String TIME = "time";

    private MyService.MyBinder myBinder;
    private Intent intent;
    private ServiceConnection sConn;
    private boolean bound;


    abstract void onReceive(String msg);

    abstract void onTimerStatusChange(boolean running);


    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TimerActivity.this.onReceive(intent.getStringExtra(TIME));
            }
        };
        intent = new Intent(this, MyService.class);
        sConn = new ServiceConnection() {

            public void onServiceConnected(ComponentName name, IBinder binder) {
                myBinder = (MyService.MyBinder) binder;
                bound = true;
                onTimerStatusChange(myBinder.isRunning());
            }

            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TIMER_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
        bindService(intent, sConn, BIND_AUTO_CREATE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!bound) return;
        unbindService(sConn);
        bound = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    void startTimer() {
        if (bound && myBinder != null) {
            if (!myBinder.isRunning()) {
                myBinder.start();
                onTimerStatusChange(myBinder.isRunning());
            }
        }

    }

    void stopTimer() {
        if (bound && myBinder != null) {
            if (myBinder.isRunning()) {
                myBinder.stop();
                onTimerStatusChange(myBinder.isRunning());
            }
        }
    }
}
