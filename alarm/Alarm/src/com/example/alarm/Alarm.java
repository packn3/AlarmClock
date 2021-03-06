package com.example.alarm;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;
import android.media.AudioManager;

public class Alarm extends Activity {
	private Button backButton;
	public MediaPlayer mp;
	private AudioManager am;
	public int vol;
	
	private WakeLock wakelock;
    private KeyguardLock keylock;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		int maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxvol), 0);
		
		backButton = (Button)findViewById(R.id.Backbnt);
		backButton.setOnClickListener( new Button.OnClickListener() {
            public void onClick(View arg0) {
            	mp.stop();
            	am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(vol), 0);
            	finish();
            }
		});
		
		mp = MediaPlayer.create(this, R.raw.gyoza);
		mp.setLooping(true);
        mp.start();
        
     // スリープ状態から復帰する
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "disableLock");
            wakelock.acquire();

        // スクリーンロックを解除する
        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keylock = keyguard.newKeyguardLock("disableLock");
        keylock.disableKeyguard();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}

}
