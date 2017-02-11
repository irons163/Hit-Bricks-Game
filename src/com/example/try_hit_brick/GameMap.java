package com.example.try_hit_brick;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameMap extends Activity implements OnClickListener {
	final int GAME_TOTAL_LEVEL = 10;
	int currentMaxGameLevel = 0;
	int vWidth;
	int vHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/* 設置為無標題列 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 設置為全屏模式 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// View view = View.inflate(this, R.layout.game_map, null);

		setContentView(R.layout.game_map);


	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		currentMaxGameLevel = preferences.getInt("level", 0);
		
		RelativeLayout game_map_layout = (RelativeLayout) findViewById(R.id.game_map_layout);

		DisplayMetrics dm = new DisplayMetrics();
		// 取得螢幕大小
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);

		vWidth = dm.widthPixels; // 螢幕寬
		vHeight = dm.heightPixels; // 螢幕高

		// 176 33 193 50
		// 158 33 175 50
		// 140 30 157 47
		// 92 113 109 130
		// 76 130 93 147
		// 68 157 85 174
		// 68 189 85 206
		// 45 238 62 255
		// 55 275 72 292
		// 100 341 117 358 : 1
		Matrix matrix = new Matrix();
		// matrix.set

		int oldXs[] = { 100, 55, 45, 68, 68, 76, 92, 140, 158, 176 };
		int oldYs[] = { 341, 275, 238, 189, 157, 130, 113, 30, 33, 33 };

		ArrayList<ImageView> imageViews = new ArrayList<ImageView>();

		for (int i = 0; i < GAME_TOTAL_LEVEL; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setId(i);
			imageView.setOnClickListener(this);
			imageView.setScaleType(ScaleType.FIT_CENTER);

			setImageXYByOldXY(imageView, oldXs[i], oldYs[i]);
			if (i <= currentMaxGameLevel) {
				// imageView.setImageResource(R.drawable.o);
				imageView.setBackgroundResource(R.drawable.o);
			} else {
				// imageView.setImageResource(R.drawable.x);
				imageView.setBackgroundResource(R.drawable.x);
			}
			game_map_layout.addView(imageView);
		}
		//
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);
		// imageView.setId(0);

		ArrayList<Rect> rects = new ArrayList<Rect>();
		rects.add(new Rect(100, 341, 117, 358));

		for (Rect rect : rects) {

		}

		float aa = (float) (148.0 / 254.0);

		float b = aa * vWidth;

		float a = convertPixelsToDp(140, this);
		int bb = 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		float x = event.getX();
		float y = event.getY();

		return super.onTouchEvent(event);
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() <= currentMaxGameLevel) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("playGameLevel", v.getId());
			startActivity(intent);
//			switch (v.getId()) {
//			case 0:
//				startActivity(intent);
//				break;
//			case 1:
//				startActivity(intent);
//				break;
//			case 2:
//				startActivity(intent);
//				break;
//			case 3:
//				startActivity(intent);
//				break;
//			case 4:
//
//				break;
//			case 5:
//
//				break;
//			case 6:
//
//				break;
//			case 7:
//
//				break;
//			case 8:
//
//				break;
//			case 9:
//
//				break;
//			}
		}
	}

	private void setImageXYByOldXY(ImageView imageView, int oldX, int oldY) {
		float newX = (float) (oldX / 254.0) * vWidth;
		float newY = (float) (oldY / 403.0) * vHeight;
		// imageView.setX(convertPixelsToDp(newX, this));
		// imageView.setY(convertPixelsToDp(newY, this));
		// imageView.setX(convertDpToPixel(newX, this));
		// imageView.setY(convertDpToPixel(newY, this));
		imageView.setX(newX);
		imageView.setY(newY);
		imageView.setLayoutParams(new LayoutParams(
				(int) ((17 / 254.0) * vWidth), (int) ((17 / 254.0) * vWidth)));

		// imageView.setMaxHeight(5);
		// imageView.setMaxWidth(5);
		// imageView.setLeft((int)newX);
		// imageView.setTop((int)newY);
		// imageView.setRight((int)newX+17);
		// imageView.setBottom((int)newY+17);
	}
}
