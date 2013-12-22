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
import android.widget.TextView;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.media.AudioManager;
import java.util.Random;

public class Alarm extends Activity {
	private Button judge;
	private TextView formula;
	private EditText ans;
	
	private MediaPlayer mp;
	private AudioManager am;
	private int vol;
	
	private WakeLock wakelock;
    private KeyguardLock keylock;
    
    private int num1, num2;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		int maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxvol), 0);
		
		formula = (TextView)findViewById(R.id.formulaText);
		formula.setTextSize(50);
		createFormula();
		
		ans = (EditText)findViewById(R.id.answer);
		
		judge = (Button)findViewById(R.id.judgeButton);
		judge.setOnClickListener( new Button.OnClickListener() {
            public void onClick(View arg0) {
            	int ans_int = Integer.valueOf(ans.getText().toString());
            	if(ans_int == num1 + num2){
	            	mp.stop();
	            	am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(vol), 0);
	            	finish();
            	}
            }
		});
		
		mp = MediaPlayer.create(this, R.raw.rakuen);
		mp.setLooping(true);
        mp.start();
        
        //スリープ回復
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "disableLock");
            wakelock.acquire();

        //ロック解除
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
	
	private void createFormula(){
		Random ran = new Random();
		num1 = ran.nextInt(50);
		num2 = ran.nextInt(50);
		
		String nums1 = String.valueOf(num1);
		String nums2 = String.valueOf(num2);
		
		formula.setText(nums1 + " + " + nums2);
	}
}
