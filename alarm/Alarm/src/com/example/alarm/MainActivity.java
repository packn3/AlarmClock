package com.example.alarm;

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
	 
	private int current_hour;
	private int current_minute;
	private int current_day;
	 
	private WakeLock wakelock;
	private KeyguardLock keylock;
	 
	private boolean alarmEnable;
    
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		   
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		Date date = calendar.getTime();
		cal_hour = date.getHours();
		cal_minute = date.getMinutes();
		alarmEnable = false;
		
		//保存された設定の読み出し
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		getSharedPreferences();
		     
		// 各種ボタンの設定
		final Button alarmYesBtn = (Button)findViewById(R.id.judgeButton);
		alarmYesBtn.setEnabled(!alarmEnable);
		final Button alarmNo_Btn = (Button)findViewById(R.id.button2);
		alarmNo_Btn.setEnabled(alarmEnable);
		 
		// TimePicker の設定
		timePicker = (TimePicker)findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(cal_hour);
		timePicker.setCurrentMinute(cal_minute);
		timePicker.setEnabled(!alarmEnable);
		
		alarmYesBtn.setOnClickListener( new Button.OnClickListener() {
        	 public void onClick(View arg0) {
				alarmYesBtn.setEnabled(false);
				alarmNo_Btn.setEnabled(true);
				timePicker.setEnabled(false);
				
				//アラームの開始
				alarmEnable = true;
				startAlarm();
			}
         });
       
        alarmNo_Btn.setOnClickListener( new Button.OnClickListener() {
        	public void onClick(View v) {
				alarmYesBtn.setEnabled(true);
				alarmNo_Btn.setEnabled(false);
				timePicker.setEnabled(true);
				
				//アラームを終了
				alarmEnable = false;
				stopAlarm();
			}
        });
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
		current_day = calendar.get(Calendar.DAY_OF_YEAR);
		current_hour = calendar.get(Calendar.HOUR_OF_DAY);
		current_minute = calendar.get(Calendar.MINUTE);
		 
		//デバッグ用**********************************************************
		//TextView time = (TextView)findViewById(R.id.currentTime);
		//String day = String.valueOf(current_day);
		//String hour = String.valueOf(current_hour);
		//String minute = String.valueOf(current_minute);
		//time.setText("Current Time " + day + " : " + hour + " : " + minute);
		//*****************************************************************
		 
		//TimePickerから時間を取得
		timePicker = (TimePicker)findViewById(R.id.timePicker1);
		cal_hour = timePicker.getCurrentHour();
		cal_minute = timePicker.getCurrentMinute();
		cal_day = current_day;
		 
		//時間を超えていたら翌日に設定
		calcDate();
		
		//うるう年の処理
		calcLeapYear();
		 
		//デバッグ用*************************************************
		TextView time = (TextView)findViewById(R.id.callTime);
		String day = String.valueOf(cal_day);
		String hour = String.valueOf(cal_hour);
		String minute = String.valueOf(cal_minute);
		time.setText("Call Time " + day + " : " + hour + " : " + minute);
		//********************************************************
		 
		//カレンダーに設定
		calendar.set(Calendar.DAY_OF_YEAR, cal_day);
		calendar.set(Calendar.HOUR_OF_DAY, cal_hour);
		calendar.set(Calendar.MINUTE, cal_minute );
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		// アラームを登録
		alarmManager.set(AlarmManager.RTC_WAKEUP,
		                     calendar.getTimeInMillis(),
		                     getPendingIntent());
		 
		//設定を保存
		saveStatus();
    }
   
    public void stopAlarm() {
         Log.d("AlarmTestActivity", "stopAlarm()");
        
         //アラームの解除
         alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
         alarmManager.cancel(getPendingIntent());
    }
   
    private PendingIntent getPendingIntent() {
    	//遷移するActivityの設定
		Intent intent = new Intent(this, Alarm.class );
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		return pendingIntent;
    }

     public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
          getSharedPreferences();
     }
    
     private void getSharedPreferences() {
		//保存された設定の取得
		cal_day = prefs.getInt("cal_day", cal_day);
		cal_hour = prefs.getInt("cal_hour", cal_hour);
		cal_minute = prefs.getInt("cal_minute", cal_minute);
		alarmEnable = prefs.getBoolean("alarmEnable", alarmEnable);
	 }
    
    private void saveStatus(){
    	SharedPreferences.Editor editor = prefs.edit();
    	//設定の保存
        editor.putInt( "cal_day", cal_day);
        editor.putInt( "cal_hour", cal_hour );
        editor.putInt( "cal_minute", cal_minute );
        editor.putBoolean( "alarmEnable", alarmEnable);
        editor.commit();
    }
    
    private void calcDate(){
    	if(cal_hour < current_hour){
    		cal_day++;
        } else if(cal_hour == current_hour){
        	if(cal_minute <= current_minute) cal_day++;
        }
    }
    
    private void calcLeapYear(){
    	int year = calendar.get(Calendar.YEAR);
        if(( year % 4 ) == 0 && ( year % 100 ) != 0 || ( year % 400 ) == 0){
        	if(cal_day > 366) cal_day = 1;
        } else{
        	if(cal_day > 365) cal_day = 1;
        }
    }
}