package example.com.week10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends TimerActivity {

    @BindView(R.id.buttonStart)
    Button buttonStart;
    @BindView(R.id.buttonStop)
    Button buttonStop;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.buttonNext)
    Button buttonNext;

    @Override
    void onReceive(String msg) {
        if (textView != null) {
            textView.setText(msg);
        }
    }

    @Override
    void onTimerStatusChange(boolean running) {
        if (running) {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
        } else {
            buttonStart.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buttonStart.setOnClickListener(view -> startTimer());
        buttonStop.setOnClickListener(view -> stopTimer());
        buttonNext.setOnClickListener(view -> nextActivity());
    }

    private void nextActivity() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }


}
