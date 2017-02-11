package com.example.try_hit_brick;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	BallView myView;
	static int  screenWidth;
	static int screenHeight;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		/* 定義DisplayMetrics物件 */
		DisplayMetrics dm = new DisplayMetrics();
		/* 取得視窗屬性 */
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		/* 窗口的寬度 */
		screenWidth = dm.widthPixels;
		/* 窗口的高度 */
		screenHeight = dm.heightPixels;
//		setTitle("寬"+screenWidth+"  高"+screenHeight);
		
		/* 設置為無標題列 */
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		/* 設置為全屏模式 */ 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							 WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		/* 創建GameSurfaceView對象 */
		myView = new BallView(this);
		//設置顯示GameSurfaceView視圖
		setContentView(myView);
	}//end of onCreate()


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
