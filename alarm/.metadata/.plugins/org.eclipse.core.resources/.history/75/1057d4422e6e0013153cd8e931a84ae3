package com.example.alarm;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Vibrator;

public class ButtonGame extends Activity implements OnClickListener{

	final int num = 10;
	Button[] bt;
	int[] number;
	int currentNumber;
	
	private MediaPlayer mp;
	private AudioManager am;
	private int vol;
	
	private WakeLock wakelock;
    private KeyguardLock keylock;
    
    private Vibrator vib;
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);

        //音量最大
        am = (AudioManager)getSystemService(AUDIO_SERVICE);
		int maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxvol), 0);
		
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
        
        //バイブレータの準備
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        
        //音楽を鳴らす
        mp = MediaPlayer.create(this, R.raw.rakuen);
		mp.setLooping(true);
		mp.start();
        
        number = new int[num];
        for(int i=0; i<num; i++){
        	number[i] = i+1;
        }
        shuffle(number);
        
        currentNumber = 1;

        bt = new Button[num];
        for(int i=0; i<num; i++)
        {
                bt[i] = new Button(this);
                bt[i].setText("ボタン" + number[i]);
                ll.addView(bt[i]);

                bt[i].setOnClickListener(this);
        }
	}
	
	public void onClick(View v)
    {
		for(int i=0; i<num; i++){
			//どのボタンが押されたかを調べる
			if(v == bt[i]){
				//正しい番号のボタンが押されたかを調べる
				if(currentNumber == number[i]){
					v.setEnabled(false); //ボタンを無効にする
					currentNumber++; //番号を進める
				} else{ //間違った番号のボタンを押したらバイブを鳴らして知らせる
					wrong();
				}
				if(currentNumber == num+1){ //最後のボタンが押されたらAcrivityを終了する
					mp.stop(); //音楽を止める
        			am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(vol), 0); //音量を元に戻す
        			wakelock.release(); //スリープ解除状態の解除
        			keylock.reenableKeyguard(); //ロック解除状態の解除
					finish();
				}
			}
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.button_game, menu);
		return true;
	}
	
	//値をシャッフルする
	public static void shuffle(int[] arr){
        for(int i=arr.length-1; i>0; i--){
            int t = (int)(Math.random() * i);  //0～i-1の中から適当に選ぶ

            //選ばれた値と交換する
            int tmp = arr[i];
            arr[i]  = arr[t];
            arr[t]  = tmp;
        }
    }
	
	private void wrong(){
		//バイブを鳴らして間違っていることを知らせる
		long[] pattern = {0, 120, 50, 220}; //「ブッブー」のパターン
		vib.vibrate(pattern, -1);
	}
}
