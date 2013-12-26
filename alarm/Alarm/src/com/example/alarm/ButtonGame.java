package com.example.alarm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ButtonGame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        setContentView(ll);
		
		int num = (int)(Math.random() * 10) + 1;
        int n = num;
        
        TextView tv = new TextView(this);
        tv.setText("アラーム鳴動");
        ll.addView(tv);

        Button[] bt = new Button[num];
        for(int i=0; i<n; i++)
        {
                bt[i] = new Button(this);
                bt[i].setText("ボタン" + i);
                ll.addView(bt[i]);

 //               bt[i].setOnClickListener(new OnclickListener());
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.button_game, menu);
		return true;
	}

}
