package ahegao.ahaaan;

import android.view.View;
import android.view.View.OnClickListener;

public class OnclickListener implements OnClickListener
{
	@Override
	public void onClick(View v)
	{
		// クリックされるとボタンを非表示にする
		v.setVisibility(View.INVISIBLE);
		v.destroyDrawingCache();
	}
}
