package example.com.week10;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {

    private static final String TIMER_PREFERENCES = "timerPref";
    private static final String TIMER_RUN = "run";
    private static final String TIMER_TICK = "tick";

    private final static long INTERVAL = 1000;

    private MyBinder binder = new MyBinder();

    private SharedPreferences timerPreferences;

    private Timer timer;
    private TimerTask timerTask;
    private boolean timerRun;
    private int timerTick;


    @Override
    public void onCreate() {
        super.onCreate();
        timerPreferences = getApplicationContext().getSharedPreferences(TIMER_PREFERENCES, Context.MODE_PRIVATE);
        timer = new Timer();
        loadTimer();
        if (timerRun) {
            startTimer();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void startTimer() {
        stopTimer();
        timerTask = new TimerTask() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction(TimerActivity.TIMER_ACTION);
                timerTick++;
                String time = String.format("%02d:%02d:%02d", timerTick / 3600, timerTick / 60 % 60, timerTick % 60);
                intent.putExtra(TimerActivity.TIME, time);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        };
        timer.schedule(timerTask, 0, INTERVAL);
        timerRun = true;
        saveTimer();
    }

    private void stopTimer() {
        if (timerTask != null) timerTask.cancel();
        timerRun = false;
    }

    private void saveTimer() {
        timerPreferences
                .edit()
                .putBoolean(TIMER_RUN, timerRun)
                .putInt(TIMER_TICK, timerTick)
                .apply();
    }

    private void loadTimer() {
        timerRun = timerPreferences.getBoolean(TIMER_RUN, false);
        timerTick = timerPreferences.getInt(TIMER_TICK, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveTimer();
        stopTimer();
    }

    class MyBinder extends Binder {
        void start() {
            startTimer();
        }

        void stop() {
            stopTimer();
        }

        boolean isRunning() {
            return timerRun;
        }
    }
}
