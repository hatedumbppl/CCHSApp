package com.example.cl18peelerw.cchsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    Menu root;
    Timer timer;
    TimerTask timerTask;
    ImageView image = (ImageView) findViewById(R.id.mainContent);
    int[] imageIds = {R.drawable.CCHS1, R.drawable.CCHS2, R.drawable.CCHS9, R.drawable.CCHS13, R.drawable.CCHS14};
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.root = (Menu) this.getLayoutInflater().inflate(R.layout.activity_home, null);
        this.setContentView(root);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    public void toggleMenu(View v){
        this.root.toggleMenu();
    }

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                index++;
                if(index >= imageIds.length) {
                    index = 0;
                }
                image.setImageResource(imageIds[index]);
            }
        };
        timer.schedule(timerTask, 5000, 5000);
    }

}
