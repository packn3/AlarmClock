package com.example.alarm;
/*
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
*/

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TextView;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    
     private AlarmManager alarmManager;
     private Calendar calendar;
     private TimePicker timePicker;
    
     private SharedPreferences prefs;
    
     private int cal_hour;
     private int cal_minute;
     private int cal_day;
     
     private WakeLock wakelock;
     private KeyguardLock keylock;
     
     private boolean alarmEnable;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
          // 現在時刻の取得 
         calendar = Calendar.getInstance();
         calendar.setTimeInMillis(System.currentTimeMillis());
         Date date = calendar.getTime();
         cal_hour = date.getHours();
         cal_minute = date.getMinutes();
         alarmEnable = false;
       
         // 保存された時刻とアラームのステータスを取得 
         prefs = PreferenceManager.getDefaultSharedPreferences(this);
         prefs.registerOnSharedPreferenceChangeListener(this);
         getSharedPreferences();
         
         //ボタンの作成
         final Button alarmYesBtn = (Button)findViewById(R.id.button1);
         alarmYesBtn.setEnabled(!alarmEnable);
         final Button alarmNo_Btn = (Button)findViewById(R.id.button2);
         alarmNo_Btn.setEnabled(alarmEnable);
         
          // TimePicker に反映 
         timePicker = (TimePicker)findViewById(R.id.timePicker1);
         timePicker.setIs24HourView(true);
         timePicker.setCurrentHour(cal_hour);
         timePicker.setCurrentMinute(cal_minute);
         timePicker.setEnabled(!alarmEnable);
         
         
        alarmYesBtn.setOnClickListener( new Button.OnClickListener() {
               public void onClick(View arg0) {
                    // TODO 自動生成されたメソッド・スタブ 
                    alarmYesBtn.setEnabled(false);
                    alarmNo_Btn.setEnabled(true);
                    timePicker.setEnabled(false);
                    
                    // アラームの設定 
                    alarmEnable = true;
                    startAlarm();
               }
        });
       
        alarmNo_Btn.setOnClickListener( new Button.OnClickListener() {
               public void onClick(View v) {
                    // TODO 自動生成されたメソッド・スタブ 
                    alarmYesBtn.setEnabled(true);
                    alarmNo_Btn.setEnabled(false);
                    timePicker.setEnabled(true);
                    
                    // アラームの解除 
                    alarmEnable = false;
                    stopAlarm();
               }
        });
        
 /*       // スリープ状態から復帰する
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, "disableLock");
            wakelock.acquire();

        // スクリーンロックを解除する
        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keylock = keyguard.newKeyguardLock("disableLock");
        keylock.disableKeyguard();*/
    }
    
    @Override
	public void onUserLeaveHint(){
		saveStatus();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			saveStatus();
			finish();
			return true;
		}
		return false;
	}
	
    public void startAlarm() {
         Log.d("AlarmTestActivity", "startAlarm()");
        
         alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
         //Calendarのインスタンスを作成
         calendar = Calendar.getInstance();
         
         //現在の日付と時間を取得
         int current_day = calendar.get(Calendar.DAY_OF_YEAR);
         int current_hour = calendar.get(Calendar.HOUR_OF_DAY);
         int current_minute = calendar.get(Calendar.MINUTE);
         
         //デバッグ用**********************************************************
         //TextView time = (TextView)findViewById(R.id.currentTime);
         //String day = String.valueOf(current_day);
         //String hour = String.valueOf(current_hour);
         //String minute = String.valueOf(current_minute);
         //time.setText("Current Time " + day + " : " + hour + " : " + minute);
         //*****************************************************************
         
         //TimePickerで選択された時刻を取得 
         timePicker = (TimePicker)findViewById(R.id.timePicker1);
         cal_hour = timePicker.getCurrentHour();
         cal_minute = timePicker.getCurrentMinute();
         cal_day = current_day;
         
         //もし設定された時間が過去の時間なら日付を明日にする
         if(cal_hour < current_hour){
        	 cal_day++;
         } else if(cal_hour == current_hour){
        	 if(cal_minute <= current_minute) cal_day++;
         }
         
         //うるう年の計算
         int year = calendar.get(Calendar.YEAR);
         if(( year % 4 ) == 0 && ( year % 100 ) != 0 || ( year % 400 ) == 0){
        	 if(cal_day > 366) cal_day = 1;
         } else{
        	 if(cal_day > 365) cal_day = 1;
         }
         
         //デバッグ用*************************************************
         TextView time = (TextView)findViewById(R.id.callTime);
         String day = String.valueOf(cal_day);
         String hour = String.valueOf(cal_hour);
         String minute = String.valueOf(cal_minute);
         time.setText("Call Time " + day + " : " + hour + " : " + minute);
         //********************************************************
         
         //取得した時刻をカレンダーに設定 
         calendar.set(Calendar.DAY_OF_YEAR, cal_day);
         calendar.set(Calendar.MINUTE, cal_minute );
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);
        
          // アラームに登録 
         alarmManager.set(AlarmManager.RTC_WAKEUP,
                             calendar.getTimeInMillis(),
                             getPendingIntent());
         
          //状態を保存
         saveStatus();
    }
   
    public void stopAlarm() {
         Log.d("AlarmTestActivity", "stopAlarm()");
        
          // 登録してあるアラームを解除 
         alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
         alarmManager.cancel(getPendingIntent());
    }
   
    private PendingIntent getPendingIntent() {
          // 起動するアプリケーションを登録 
    	  //ここで何らかのゲームをするためのActivityを設定する
         Intent intent = new Intent( getApplicationContext(), Alarm.class );
         PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
          return pendingIntent;
    }

     public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
          // TODO 自動生成されたメソッド・スタブ 
          getSharedPreferences();
     }
    
    private void getSharedPreferences() {
          // 保存されていた時刻を取得 
    	 cal_day = prefs.getInt("cal_day", cal_day);
         cal_hour = prefs.getInt("cal_hour", cal_hour);
         cal_minute = prefs.getInt("cal_minute", cal_minute);
         alarmEnable = prefs.getBoolean("alarmEnable", alarmEnable);
    }
    
    private void saveStatus(){
    	SharedPreferences.Editor editor = prefs.edit();
        editor.putInt( "cal_day", cal_day);
        editor.putInt( "cal_hour", cal_hour );
        editor.putInt( "cal_minute", cal_minute );
        editor.putBoolean( "alarmEnable", alarmEnable);
        editor.commit();
    }
}