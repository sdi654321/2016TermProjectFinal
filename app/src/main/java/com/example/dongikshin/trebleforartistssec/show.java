package com.example.dongikshin.trebleforartistssec;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by dongikshin on 2016-12-17.
 */

public class show extends Activity {

    ProgressDialog pd;
    VideoView view;
    String URL = "ec2-52-78-36-78.ap-northeast-2.compute.amazonaws.com";
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (VideoView)findViewById(R.id.videoView);
        pd = new ProgressDialog(show.this);

        pd.setTitle("비디오 스트리밍");
        pd.setMessage("버퍼링...");
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        pd.show();

        try{
            MediaController controller = new MediaController(show.this);
            controller.setAnchorView(view);
            Uri vid = Uri.parse(URL);
            view.setMediaController(controller);
            view.setVideoURI(vid);

        }catch(Exception e){
            Log.e("Error",e.getMessage());
            e.printStackTrace();
        }

        view.requestFocus();
        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();
                view.start();
            }
        });

    }


}
