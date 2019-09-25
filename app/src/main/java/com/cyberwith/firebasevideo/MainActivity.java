package com.cyberwith.firebasevideo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private VideoView mainVideoView;
    private ImageView playButton;
    private TextView currentTime, durationTime;
    private ProgressBar currentProgress, bufferProgress;
    private boolean isPlaying = false;

    private Uri videoUri;
    private int current = 0, duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainVideoView = findViewById(R.id.videoID);
        playButton = findViewById(R.id.playButtonID);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);
        currentProgress = findViewById(R.id.progressBar);
        bufferProgress = findViewById(R.id.progressBar2);

        videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-video-413b9.appspot.com/o/Marvel%20Studios'%20Captain%20Marvel%20-%20Official%20Trailer%20-%20YouTube%20(720p).mp4?alt=media&token=bdca87cf-2f3c-4937-ba6a-2fde51a388c3");
        mainVideoView.setVideoURI(videoUri);
        currentProgress.setMax(100);

        //for video buffering progressBar
        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == mp.MEDIA_INFO_BUFFERING_START) {
                    bufferProgress.setVisibility(View.VISIBLE);
                } else if (what == mp.MEDIA_INFO_BUFFERING_END) {
                    bufferProgress.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        //for set duration
        mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = mp.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration/60, duration%60);
                durationTime.setText(durationString);
            }
        });

        mainVideoView.requestFocus();
        mainVideoView.start();
        isPlaying = true;
        playButton.setImageResource(R.drawable.ic_pause);

        new VideoProgress().execute();//star current time class

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mainVideoView.pause();
                    isPlaying = false;
                    playButton.setImageResource(R.drawable.ic_play);
                } else {
                    mainVideoView.start();
                    isPlaying = true;
                    playButton.setImageResource(R.drawable.ic_pause);
                }
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;
    }


    //for video time count
    public class VideoProgress extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            do {
                if(isPlaying){
                    current = mainVideoView.getCurrentPosition() / 1000;
                    publishProgress(current);
                }
            } while (currentProgress.getProgress() <= 100);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            try {
                int currentPercent = values[0] * 100 / duration;
                currentProgress.setProgress(currentPercent);
                String currentString = String.format("%02d:%02d", values[0]/60, values[0]%60);
                currentTime.setText(currentString);
            } catch (Exception e) {

            }
        }
    }

}
