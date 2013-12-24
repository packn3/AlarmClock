package com.example.alarm;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.media.MediaPlayer;
import android.media.AudioManager;
import java.util.Random;

public class Alarm extends Activity {
	private Button judge;
	private TextView formula;
	private TextView questionNoText;
	private EditText ans;
	
	private MediaPlayer mp;
	private AudioManager am;
	private int vol;
	
	private WakeLock wakelock;
    private KeyguardLock keylock;
    
    private int num1, num2;
    int questionNo;
    final int MAX_QUESTION_NO = 3;
    
    Vibrator vib;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		int maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxvol), 0);
		
		questionNo = 1;
		
		formula = (TextView)findViewById(R.id.formulaText);
		formula.setTextSize(50);
		
		ans = (EditText)findViewById(R.id.answer);
		
		questionNoText = (TextView)findViewById(R.id.questionNumber);
		createQuestionNoText(questionNo);
		
		createFormula();
		
		judge = (Button)findViewById(R.id.judgeButton);
		judge.setOnClickListener( new Button.OnClickListener() {
            public void onClick(View arg0) {
            	//空白の状態でボタンを押した場合は処理しない
            	if("".equals(ans.getText().toString())) return;
            	if(check(ans.getText().toString())){
            		if(questionNo == MAX_QUESTION_NO){
            			mp.stop();
            			am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(vol), 0);
            			wakelock.release(); //スリープ解除状態の解除
            			keylock.reenableKeyguard(); //ロック解除状態の解除
            			finish();
            		} else{
            			questionNo++;
            			createQuestionNoText(questionNo);
            			createFormula();
            		}
            	} else{
            		wrongAnswer();
            	}
            }
		});
		
		mp = MediaPlayer.create(this, R.raw.rakuen);
		mp.setLooping(true);
        mp.start();
        
        //SystemService系の準備*******************************************************************
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
        
        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        //***************************************************************************************
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}
	
	public boolean dispatchKeyEvent(KeyEvent event) {
	    if (event.getAction()==KeyEvent.ACTION_DOWN) {
	        switch (event.getKeyCode()) {
	        case KeyEvent.KEYCODE_BACK:
	            return true;
	        }
	    }
	    return super.dispatchKeyEvent(event);
	}
	
	//計算式を作るメソッド
	private void createFormula(){
		Random ran = new Random();
		num1 = ran.nextInt(50);
		num2 = ran.nextInt(50);
		
		String nums1 = String.valueOf(num1);
		String nums2 = String.valueOf(num2);
		
		formula.setText(nums1 + " + " + nums2);
		ans.setText("");
	}
	
	//問題番号を表示するメソッド
	private void createQuestionNoText(int q){
		String no = String.valueOf(questionNo);
		questionNoText.setText(no + "問目");
		questionNoText.setTextSize(40);
	}
	
	//答えの判定をするメソッド
	private boolean check(String ans_string){
		int ans_int = Integer.valueOf(ans_string);
		if(ans_int == num1 + num2) return true;
		else return false;
	}
	
	//答えが間違っていた時の処理をするメソッド
	private void wrongAnswer(){
		createFormula();
		//バイブを鳴らして間違っていることを知らせる
		long[] pattern = {0, 120, 50, 220}; //「ブッブー」のパターン
		vib.vibrate(pattern, -1);
	}
}
