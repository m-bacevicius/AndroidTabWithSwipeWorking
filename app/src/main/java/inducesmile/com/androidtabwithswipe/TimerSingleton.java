package inducesmile.com.androidtabwithswipe;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by Mantas Bacevičius on 2016-11-02.
 */
public class TimerSingleton {
    private static TimerSingleton ourInstance = new TimerSingleton();

    public static TimerSingleton getInstance() {
        return ourInstance;
    }

    private TimerSingleton() {
    }

    static CountDownTimer timer3 = new CountDownTimer(1000, 1000) { // adjust the milli seconds here         this is working;

        public void onTick(long millisUntilFinished) {
            Log.e("TIMER", "IS WORKING");

        }

        public void onFinish() {
            timer3.start();
        }
    };

    static boolean isRunning = false;

    protected void start()
    {
        timer3.start();
        isRunning = true;
        Log.d("Timer Singleton", "Timer has started");

    }
    protected void stop()
    {
        timer3.cancel();
        isRunning = false;
        Log.d("Timer Singleton", "Timer has stopped");
    }
    protected boolean isRunning()
    {
        return isRunning;
    }
}
