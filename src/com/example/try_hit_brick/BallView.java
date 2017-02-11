package com.example.try_hit_brick;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class BallView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	// 執行緒延時控制
	final int ball_sleep = 1;// 毫秒，延時越大，球速越慢
	final int ball_r = 8;// 小球半徑
	final float ball2_r = 8;// 底下滾珠小球半徑
	final int ballXorYadd = 4;// 小球的基本位移。測試可行值：2，4

	// 獲取螢幕寬度和高度
	int screen_width;// 320;
	int screen_height;// 480;

	// 磚的屬性
	int brick_width;// 每塊磚寬
	int brick_height;// 每塊磚高
	boolean brick_exist[];// 磚是否存在
	int k;// 列//到for迴圈裡才初始化
	int j;// 行
	int brick_left = brick_width * (k - 1);// 到判斷語句才初始化
	int brick_right = brick_width * k;
	int brick_top = brick_height * j;
	int brick_bottom = brick_height * (j + 1);

	// 擋板的屬性
	int board_length;// 擋板長度:80比較合適，可以隨意修改,但別超過screen_width
	final int boardYadd = 16;// 按上下鍵時擋板y方向位移量。經驗證4、8、16可以，12不行
	final int board_x_move = 30;// 擋板x方向位移量:可以隨意自訂
	int board_left;// 擋板左側（可變）
	int board_right;// 擋板右側（可變）
	int board_thickness;// 擋板厚度
	int board_default_top;// 即435,擋板的top面初始位置
	int board_alterable_top;// 擋板上側（可變）
	int board_alterable_bottom;// 擋板下側（可變）

	int ball_default_x;// 球的初始x座標
	int ball_default_y;// 球的初始y座標
	// 球的即時座標（可變）：
	int ball_x;// 球心橫坐標
	int ball_y;// 球心縱坐標
	// 球的前一步的y座標
	int ball_previous_y;
	int ball_x_speed;// 球的橫向偏移量//可變
	int ball_y_speed;// 球的縱向偏移量//可變
	boolean ball_isRun;// 球是否在動
	// 控制迴圈
	boolean mbLoop;
	// 定義SurfaceHolder物件
	SurfaceHolder mSurfaceHolder = null;
	// 獲得分數
	int score;
	/* 喚醒漸變渲染 */
	Shader mRadialGradient = null;

	Bitmap takebp;
	Rect takerect;
	int x, y;
	int touchX = 0, touchY = 0;
	int tempwidth = 0;
	int tempheight = 0;
	boolean chk = false;
	ImageView componentBarsMoveBtn;
	int displayWidth, displayHeight;
	int componentBarsPopupWindowWidth, componentBarsPopupWindowHeight;
	int componentBarsMoveBtnMinX;

	// ------------------------------------------------------------------------------------------------------//
	public BallView(Context context) {
		super(context);
		// 產生實體SurfaceHolder
		mSurfaceHolder = this.getHolder();
		// 添加回檔
		mSurfaceHolder.addCallback(this);
		this.setFocusable(true);

		// 獲取螢幕寬度和高度
		screen_width = MainActivity.screenWidth;// 320
		screen_height = MainActivity.screenHeight;// 480

		// 磚的屬性
		brick_width = screen_width / 5;// 每塊磚寬64
		brick_height = screen_height / 15;// 每塊磚高32

		// 擋板的屬性
		board_length = screen_width / 4;// 擋板長度:80比較合適，可以隨意修改,但別超過screen_width
		board_left = (screen_width - board_length) / 2;// 擋板左側（可變）
		board_right = (screen_width + board_length) / 2;// 擋板右側（可變）
		board_thickness = 30;// 擋板厚度
		board_default_top = 13 * screen_height / 15;// 即435,擋板的top面初始位置
		board_alterable_top = board_default_top;// 擋板上側（可變）
		board_alterable_bottom = board_alterable_top + board_thickness;// 擋板下側（可變）

		ball_default_x = screen_width / 2;// 球的初始x座標
		ball_default_y = board_default_top - ball_r;// 球的初始y座標
		// 球的即時座標（可變）：
		ball_x = ball_default_x;
		ball_y = ball_default_y;
		// 球的前一步的y座標
		ball_previous_y = 0;
		ball_x_speed = ballXorYadd;// 球的橫向偏移量
		ball_y_speed = ballXorYadd;// 球的縱向偏移量

		mbLoop = true;
		ball_isRun = false;
		score = 0;

		brick_exist = new boolean[25];
		for (int i = 0; i < 25; i++) {
			brick_exist[i] = true;
		}
		/* 構建RadialGradient物件，設置半徑的屬性 */
		mRadialGradient = new RadialGradient(ball_x, ball_y, ball_r,// 球中心座標x,y,半徑r
				new int[] { Color.WHITE, Color.BLUE, Color.GREEN, Color.RED,
						Color.YELLOW },// 顏色陣列
				null,// 顏色陣列中每一種顏色對應的相對位置，為空的話就是平均分佈，由中心向外排布
				Shader.TileMode.REPEAT);// 渲染模式：重複

		takebp = BitmapFactory.decodeResource(getResources(), R.drawable.bar);

		// componentBarsMoveBtn = (ImageView) findViewById(R.id.imageView2);
		componentBarsMoveBtn = new ImageView(context);
		componentBarsMoveBtn.setImageResource(R.drawable.bar);
		takerect = new Rect(board_left, board_default_top, board_right,
				board_default_top + board_thickness);
		x = board_left;
		y = board_default_top;

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		displayWidth = metrics.widthPixels;
		displayHeight = metrics.heightPixels;

		componentBarsPopupWindowWidth = displayWidth / 3;
		componentBarsPopupWindowHeight = displayHeight - takebp.getHeight()
				- 10;

		componentBarsMoveBtnMinX = 0;
	}

	public float convertDpToPixel(float dp) {
		Resources resources = getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	public void resetGame() {
		ball_isRun = false;
		score = 0;// 分數
		ball_x_speed = ballXorYadd;// 球的橫向偏移量
		ball_y_speed = ballXorYadd;// 球的縱向偏移量
		ball_x = screen_width / 2;// 球心起始橫坐標
		ball_y = board_default_top - ball_r;// 球心起始縱坐標
		board_left = (screen_width - board_length) / 2;// 擋板左側
		board_right = (screen_width + board_length) / 2;// 擋板右側
		board_alterable_top = board_default_top;// 擋板上側
		board_alterable_bottom = board_alterable_top + board_thickness;// 擋板下側
		for (int i = 0; i < 25; i++) {
			brick_exist[i] = true;
		}
	}

	// ---------------------------------繪圖迴圈開始----------------------------------
	public void run() {

		while (mbLoop && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(ball_sleep);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			// 球的前一步y座標
			ball_previous_y = ball_y;

			if (ball_isRun) {
				ballRunning();// 讓小球移動
				boardPositionCheck();// 檢測擋板是否處於“中線”位置，是就隨小球上升一步，直至到“上線”
				hitWallCheck();// 牆壁碰撞檢測
				hitBoardCheck();// 擋板碰撞檢測
				hitBrickCheck();// 磚塊碰撞檢測
			}
			synchronized (mSurfaceHolder) {
				Draw();
			}
		}
	}

	// ---------------------------------繪圖迴圈結束----------------------------------
	// ------------------------------------------------------------------------------

	// 讓小球移動
	public void ballRunning() {
		ball_x += ball_x_speed;
		ball_y -= ball_y_speed;
	}

	// 朝左或朝右碰撞後小球水準方向逆向
	public void ballLeftOrRightHit() {
		ball_x_speed *= -1;
	}

	// 朝上或朝下碰撞後小球豎直方向逆向
	public void ballUpOrDownHit() {
		ball_y_speed *= -1;
	}

	public void ballcornerHit() {
		ball_x_speed *= -1;
		ball_y_speed *= -1;
	}

	public void ballStraightUp() {// 功能沒實現，這是多餘代碼
		ball_x_speed = 0;// 注意在其他地方恢復
		ball_y_speed *= -1;
	}

	// -------------------牆壁碰撞檢測開始-------------------------------------
	public void hitWallCheck() {
		// 左碰牆
		if (ball_x <= ball_r && ball_y >= ball_r && ball_y <= screen_height) {
			ballLeftOrRightHit();
			// 右碰牆
		}
		if (ball_x >= screen_width - ball_r && // 不能寫為 else if,因為可能恰好碰到角落。
				ball_y >= ball_r && ball_y <= screen_height) {
			ballLeftOrRightHit();
		}
		// 上碰牆
		if (ball_x >= ball_r && ball_x <= screen_width - ball_r
				&& ball_y <= ball_r + brick_height) // 、、、、、、、、、、、、、、、25號修改
		{
			ballUpOrDownHit();
		}
		// 下碰牆
		if (ball_x >= ball_r && ball_x <= screen_width - ball_r
				&& ball_y >= screen_height - ball_r) {
			ballUpOrDownHit();
			if (score <= 10) {
				score = 0;
			} else
				score -= 10;
		}
	}

	// -------------------牆壁碰撞檢測結束-------------------------------------

	// -----------------------------擋板碰撞檢測開始---------------------------
	public void hitBoardCheck() {
		// 下碰擋板正面
		if (ball_x >= ball_r && ball_x <= screen_width - ball_r && // 在螢幕內，起碼條件
				ball_x >= board_left && ball_x <= board_right && // 在擋板X域上方
				ball_y == board_alterable_top - ball_r && // 球面與擋板相切
				ball_previous_y <= board_alterable_top - ball_r // 確定球是從上方下落
		) {
			if (board_alterable_top == board_default_top - boardYadd) {// 如果彈簧伸張，擋板位於上線
				ballHitBoardlower();// 作用：ball_y_move減小；擋板被打下;小球Y向運動反向
			} else if (board_alterable_top == board_default_top) {// 如果彈簧壓縮，擋板位於下線
				boardHitBallHigher();// 作用：ball_y_move增加；擋板彈上;小球Y向運動反向
			}
		}
		// 斜碰擋板右上角//經驗證有效
		else if (Math.pow(board_right - ball_x, 2)
				+ Math.pow(board_alterable_top - ball_y, 2) <= Math.pow(ball_r,
				2) && ball_x > board_right && ball_y < board_alterable_top) {
			ballcornerHit();
		}
		// 斜碰擋板的左上角//經驗證有效
		else if (Math.pow(board_left - ball_x, 2)
				+ Math.pow(board_alterable_top - ball_y, 2) <= Math.pow(ball_r,
				2) && ball_x < board_left && ball_y < board_alterable_top) {
			ballcornerHit();
		}
	}

	// -----------------------------擋板碰撞檢測結束--------------------------

	private void boardHitBallHigher() {// 增強
		ballUpOrDownHit();// 小球Y方向反向,ball_y_speed變為正數
		if (ball_y_speed == ballXorYadd) {
			ball_y_speed += ballXorYadd;// 離開擋板後小球Y方向速度增強
		}
		if (boardYadd > ball_y_speed) {// 在執行緒這一輪，小球上升多少，擋板就上升多少。
			board_alterable_top = board_default_top - ball_y_speed;
			board_alterable_bottom = board_alterable_top + board_thickness;// 擋板下層面
		}
	}

	// 檢測擋板是否處於“中線”位置，是就隨小球上升一步，直至到“上線”
	private void boardPositionCheck() {// 還可直接利用球的位置刷新，board_top與球心相差ball_r
		if (board_alterable_top < board_default_top
				&& board_alterable_top > board_default_top - boardYadd) {
			// 擋板隨球上升
			if (board_alterable_top - ball_y_speed >= board_default_top
					- boardYadd) {
				board_alterable_top -= ball_y_speed;// 擋板上層面
				board_alterable_bottom = board_alterable_top + board_thickness;// 擋板下層面
			} else {
				board_alterable_top = board_default_top - boardYadd;// 擋板上層面
				board_alterable_bottom = board_alterable_top + board_thickness;// 擋板下層面
			}
		}
	}

	private void ballHitBoardlower() {// 減弱
		board_alterable_top = board_default_top;
		board_alterable_bottom = board_default_top + board_thickness;// 擋板被打退
		ballUpOrDownHit();// 小球Y方向反向
		if (ball_y_speed == 2 * ballXorYadd) {
			ball_y_speed -= ballXorYadd;// 小球Y方向速度減弱
		}
	}

	// 磚塊碰撞檢測開始-----------------------------------------------------------
	public void hitBrickCheck() {
		for (int i = 0; i < 25; i++) {
			if (brick_exist[i]) {
				k = i % 5 + 1;// 1,2,3,4,5迴圈
				j = i / 5 + 1;// 1,1,1,1,1;2,2,2,2,2,;...;5,5,5,5,5
				brick_left = brick_width * (k - 1);
				brick_right = brick_width * k;
				brick_top = brick_height * j;
				brick_bottom = brick_height * (j + 1);
				// 朝下碰磚的top面 AAAAAAAAAAAAAAAAAAAAA
				if (ball_x >= brick_left && ball_x <= brick_right
						&& ball_y >= brick_top - ball_r && ball_y < brick_top) {
					ballUpOrDownHit();
					brick_exist[i] = false;
					score += 4;
					// 朝下正碰2磚中間，i磚右上角檢測
					if (k != 5 && ball_x == brick_right)// 如果不是第5列磚的右側邊
					{
						// 如果磚[i+1]存在
						if (brick_exist[i + 1]) {
							brick_exist[i + 1] = false;
							score += 4;
						}
					}
					// 朝下正碰2磚中間，i磚左上角檢測
					else if (k != 1 && ball_x == brick_left)// 如果不是第1列磚的左側邊
					{
						// 如果磚[i-1]存在
						if (brick_exist[i - 1]) {
							brick_exist[i - 1] = false;
							score += 4;
						}
					}
				}
				// 朝上碰磚的bottom面 BBBBBBBBBBBBBBBBB
				else if (ball_x >= brick_left && ball_x <= brick_right
						&& ball_y > brick_bottom
						&& ball_y <= brick_bottom + ball_r) {
					ballUpOrDownHit();
					brick_exist[i] = false;
					score += 4;
					// 朝上正碰2塊磚中間--i磚的右下角檢測
					if (k != 5 && ball_x == brick_right) // 如果不是第5列磚的右側邊
					{
						if (brick_exist[i + 1]) {// 如果磚[i+1]存在
							brick_exist[i + 1] = false;
							score += 4;
						}
					}
					// 朝上正碰2塊磚中間--i磚的左下角檢測
					else if (k != 1 && ball_x == brick_left) // 如果不是第1列磚的左側邊
					{
						if (brick_exist[i - 1]) {// 如果磚[i-1]存在
							brick_exist[i - 1] = false;
							score += 4;
						}
					}
				}
				// 朝右碰磚的left面CCCCCCCCCCCCCCCCC
				else if (ball_x >= brick_left - ball_r && ball_x < brick_left
						&& ball_y >= brick_top && ball_y <= brick_bottom) {
					ballLeftOrRightHit();
					brick_exist[i] = false;
					score += 4;
					// 朝右正碰2塊磚中間，左下角檢測
					if (j != 5 && ball_y == brick_bottom)// 如果不是第5行磚的下側邊
					{
						if (brick_exist[i + 5]) {// 如果磚[i+5]存在
							brick_exist[i + 5] = false;
							score += 4;
						}
					}
					// 朝右正碰2塊磚中間，左上角檢測
					else if (j != 1 && ball_y == brick_top)// 如果不是第1行磚的上側邊
					{
						if (brick_exist[i - 5]) {// 如果磚[i-5]存在
							brick_exist[i - 5] = false;
							score += 4;
						}
					}
				}
				// 朝左碰磚的right面DDDDDDDDDDDDDDDDDD
				else if (ball_x >= brick_right
						&& ball_x <= brick_right + ball_r
						&& ball_y >= brick_top && ball_y <= brick_bottom) {
					ballLeftOrRightHit();
					brick_exist[i] = false;
					score += 4;
					// 朝左正碰2塊磚中間，右下角檢測
					if (j != 5 && ball_y == brick_bottom)// 如果不是第5行磚的下側邊
					{
						if (brick_exist[i + 5]) {// 如果磚[i+5]存在
							brick_exist[i + 5] = false;
							score += 4;
						}
					}
					// 朝左正碰2塊磚中間，右上角檢測
					else if (j != 1 && ball_y == brick_top)// 如果不是第1行磚上側邊
					{
						if (brick_exist[i - 5]) {// 如果磚[i-5]存在
							brick_exist[i - 5] = false;
							score += 4;
						}
					}
				}
				// ///////////////////////////////////////
				// 斜碰i磚的左下角EEEEEEEEEEEEEEEEEEEEEEEE
				else if ((i - 1 < 0 || (i - 1 >= 0 && !brick_exist[i - 1]))
						&& (i + 5 >= 25 || (i + 5 < 25 && !brick_exist[i + 5]))
						&& Math.pow(brick_left - ball_x, 2)
								+ Math.pow(brick_bottom - ball_y, 2) <= Math
									.pow(ball_r, 2)
						&& ball_x > brick_left - ball_r && ball_x < brick_left
						&& ball_y > brick_bottom
						&& ball_y < brick_bottom + ball_r) {
					ballcornerHit();
					brick_exist[i] = false;
					score += 4;
				}
				// 斜碰i磚的右下角FFFFFFFFFFFFFFFFFFFFFFFFFF
				else if ((i + 1 >= 25 || (i + 1 < 25 && !brick_exist[i + 1]))
						&& (i + 5 >= 25 || (i + 5 < 25 && !brick_exist[i + 5]))
						&& Math.pow(brick_right - ball_x, 2)
								+ Math.pow(brick_bottom - ball_y, 2) <= Math
									.pow(ball_r, 2) && ball_x > brick_right
						&& ball_x < brick_right + ball_r
						&& ball_y > brick_bottom
						&& ball_y < brick_bottom + ball_r) {
					Log.v("----------", "right bottom hit" + i + ":"
							+ brick_exist[i]);
					ballcornerHit();
					brick_exist[i] = false;
					score += 4;
				}
				// 斜碰i磚的右上角GGGGGGGGGGGGGGGGGGGGGGGG
				else if ((i + 1 >= 25 || (i + 1 < 25 && !brick_exist[i + 1]))
						&& (i - 5 < 0 || (i - 5 > 0 && !brick_exist[i - 5]))
						&& Math.pow(brick_right - ball_x, 2)
								+ Math.pow(brick_top - ball_y, 2) <= Math.pow(
								ball_r, 2) && ball_x > brick_right
						&& ball_x < brick_right + ball_r
						&& ball_y > brick_top - ball_r && ball_y < brick_top) {
					ballcornerHit();
					brick_exist[i] = false;
					score += 4;
				}
				// 斜碰i磚的左上角HHHHHHHHHHHHHHHHHHHHHHHHHH
				else if ((i - 1 < 0 || (i - 1 >= 0 && !brick_exist[i - 1]))
						&& (i - 5 < 0 || (i - 5 >= 0 && !brick_exist[i - 5]))
						&& Math.pow(brick_left - ball_x, 2)
								+ Math.pow(brick_top - ball_y, 2) <= Math.pow(
								ball_r, 2) && ball_x > brick_left - ball_x
						&& ball_x < brick_left && ball_y > brick_top - ball_r
						&& ball_y < brick_top) {
					ballcornerHit();
					brick_exist[i] = false;
					score += 4;
				}
			}// end if
		}// end for
	}// end hitBrickCheck()
		// 磚塊碰撞檢測結束-----------------------------------------------------------

	// ------------------------------------------
	public boolean gameOver() {
		int count = 0;
		for (boolean s : brick_exist) {
			if (!s) {
				count++;
			}
		}
		if (count == 25) {
			return true;
		} else {
			return false;
		}

	}

	// ------------------------------------------

	// ---------------繪圖方法開始------------------------
	public void Draw() {
		// 鎖定畫布，得到canvas
		Canvas canvas = mSurfaceHolder.lockCanvas();
		if (mSurfaceHolder == null || canvas == null) {
			return;
		}
		// 繪圖
		Paint mPaint = new Paint();
		// 設置取消鋸齒效果
		mPaint.setAntiAlias(true);

		mPaint.setColor(Color.BLACK);
		// 繪製矩形--背景
		canvas.drawRect(0, 0, screen_width, brick_height - 2, mPaint);

		mPaint.setColor(Color.GREEN);
		// 繪製矩形--背景
		canvas.drawRect(0, brick_height - 2, screen_width, screen_height,
				mPaint);

		mPaint.setColor(Color.RED);// 設置字體顏色
		mPaint.setTextSize(brick_height - 7);// 設置字體大小
		canvas.drawText("得分：" + score, 0, brick_height - 7, mPaint);

		canvas.drawBitmap(takebp, null, takerect, null);

		// 繪製頂層擋板````````````````````````````````````````````````````````25號修改
		mPaint.setColor(Color.BLACK);// 設置顏色
		mPaint.setStrokeWidth(4);// 設置粗細
		canvas.drawLine(0, brick_height - 2, screen_width, brick_height - 2,
				mPaint);

		for (int i = 0; i < 25; i++) {
			if (brick_exist[i]) {
				k = i % 5 + 1;// 1,2,3,4,5迴圈
				j = i / 5 + 1;// 1,1,1,1,1;2,2,2,2,2,;...;5,5,5,5,5
				brick_left = brick_width * (k - 1);
				brick_right = brick_width * k;
				brick_top = brick_height * j;
				brick_bottom = brick_height * (j + 1);

				mPaint.setStyle(Paint.Style.FILL);// 設置實心畫筆
				mPaint.setColor(Color.YELLOW);
				canvas.drawRect(brick_left + 1, brick_top + 1, brick_right - 1,
						brick_bottom - 1, mPaint);

				mPaint.setStyle(Paint.Style.STROKE);// 設置空心畫筆
				mPaint.setStrokeWidth(2);// 設置粗細
				mPaint.setColor(Color.BLUE);
				canvas.drawRect(brick_left + 3, brick_top + 3, brick_right - 3,
						brick_bottom - 3, mPaint);
			}
		}
		// 設置實心畫筆
		mPaint.setStyle(Paint.Style.FILL);
		{
			mPaint.setShader(mRadialGradient);
			canvas.drawCircle(ball_x, ball_y, ball_r, mPaint);
		}

		Paint mPaint2 = new Paint();
		// 設置取消鋸齒效果
		mPaint2.setAntiAlias(true);
		// 設置實心畫筆
		mPaint2.setStyle(Paint.Style.FILL);
		{
			mPaint2.setColor(Color.BLACK);
			/* 繪製矩形擋板 */
			canvas.drawRect(board_left, board_alterable_top, board_right,
					board_alterable_bottom, mPaint2);
			float board2_bottom = screen_height - 2 * ball2_r;// 164
			float board2_top = board2_bottom - 4;// 160
			// 線段端點座標陣列
			float x0 = board_left + (board_right - board_left) / 4;
			float y0 = board_alterable_bottom;// 440或者444
			float springAdd = (board2_top - board_alterable_bottom) / 8;// 即2.5或者2：彈簧小線段的y向長度
			float springWidth = 5.0f;// 彈簧小線段的x向長度
			float x1 = x0 - springWidth;
			float y1 = y0 + springAdd;
			float x2 = x0 + springWidth;
			float y2 = y0 + 3 * springAdd;
			float x3 = x1;
			float y3 = y0 + 5 * springAdd;
			float x4 = x2;
			float y4 = y0 + 7 * springAdd;
			float x5 = x0;
			float y5 = board2_top;// 即460
			float between_spring = (board_right - board_left) / 2;
			float pts[] = { x0, y0, x1, y1, x1, y1, x2, y2, x2, y2, x3, y3, x3,
					y3, x4, y4, x4, y4, x5, y5 };
			float pts2[] = { x0 + between_spring, y0, x1 + between_spring, y1,
					x1 + between_spring, y1, x2 + between_spring, y2,
					x2 + between_spring, y2, x3 + between_spring, y3,
					x3 + between_spring, y3, x4 + between_spring, y4,
					x4 + between_spring, y4, x5 + between_spring, y5 };
			mPaint2.setStrokeWidth(2);// 設置彈簧粗細
			// 繪製2個彈簧
			canvas.drawLines(pts, mPaint2);
			canvas.drawLines(pts2, mPaint2);
			// 繪製下層擋板
			canvas.drawRect(board_left, board2_top, board_right, board2_bottom,
					mPaint2);
			mPaint2.setColor(Color.BLACK);//
			// 繪製最下麵的兩個“輪子”(圓心x,圓心y,半徑r,p)
			canvas.drawCircle(board_left + ball2_r, screen_height - ball2_r,
					ball2_r, mPaint2);// 圓
			canvas.drawCircle(board_right - ball2_r, screen_height - ball2_r,
					ball2_r, mPaint2);
			mPaint2.setColor(Color.WHITE);//
			canvas.drawPoint(board_left + ball2_r, screen_height - ball2_r,
					mPaint2);// 繪製左輪輪心
			canvas.drawPoint(board_right - ball2_r, screen_height - ball2_r,
					mPaint2);// 繪製右輪輪心

		}// 實心畫筆mPaint2結束

		if (gameOver()) {
			// mbLoop = false;//注釋掉的話GAME OVER後也可重啟
			ball_isRun = false;
			mPaint2.setColor(Color.BLACK);// 設置字體顏色
			mPaint2.setTextSize(40.0f);// 設置字體大小
			canvas.drawText("GAME OVER", screen_width / 32 + 40,
					screen_height / 16 * 9 - 70, mPaint2);
			mPaint2.setTextSize(20.0f);// 設置字體大小
			canvas.drawText("Press \"MENU\" button to restart,",
					screen_width / 32, // 320/32=10
					screen_height / 16 * 9, // 480/16=30,30*9=270
					mPaint2);
			canvas.drawText("Press \"BACK\" button to exit.",
					screen_width / 32, // 320/32=10
					screen_height / 16 * 10, // 480/16=30,30*10=300
					mPaint2);
		}
		// 繪製後解鎖，繪製後必須解鎖才能顯示
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}// end of Draw()
	// ---------------------繪圖方法結束-----------------------------------------------

	// 在surface的大小發生改變時激發
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	// 在surface創建時激發
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this).start();// 開啟繪圖執行緒
	}

	// 在surface銷毀時激發
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 停止迴圈
		mbLoop = false;
	}

	ShowThread st = null;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN

		) {

			if (takerect.contains((int) event.getX(), (int) event.getY())
					|| event.getY() >= board_alterable_top) {
				st = new ShowThread();
				st.start();
				touchX = (int) event.getX();
				// touchY = (int) event.getY();
				tempwidth = touchX - x;
				// tempheight = touchY - y;
				chk = true;
			}
			// else if (event.getY() <= takebp.getHeight()) {
			// st = new ShowThread();
			// st.start();
			// touchX = (int) event.getX();
			// tempwidth = takebp.getWidth() / 2;
			//
			// if ((int) event.getX() <= componentBarsMoveBtnMinX) {
			// x = componentBarsMoveBtnMinX;
			// } else if ((int) event.getX() >= displayWidth
			// - componentBarsMoveBtnMinX - takebp.getWidth()) {
			// x = displayWidth - componentBarsMoveBtnMinX
			// - takebp.getWidth();
			// } else {
			// x = touchX - tempwidth;
			//
			// }
			// handler.sendEmptyMessage(0);
			// chk = true;
			//
			// }
			else {
				this.ball_isRun = !this.ball_isRun;// 開始//暫停
			}

		} else if ((event.getAction() == MotionEvent.ACTION_MOVE) && chk) {
			touchX = (int) event.getX();
			// touchY = (int) event.getY();
			// 嚙踝蕭嚙瘢嚙踝蕭嚙踝蕭嚙緙嚙請佗蕭嚙踝蕭嚙誕歹蕭嚙踝蕭嚙瘢
			// takebp.getWidth()/2;
			// takebp.getHeight()/2;
			// x = touchX - tempwidth;
			// y = touchY - tempheight;
			if (ball_isRun) {
				int lastX = x;

				if ((int) event.getX() <= componentBarsMoveBtnMinX + tempwidth) {
					board_left = x = componentBarsMoveBtnMinX;
					board_right = componentBarsMoveBtnMinX + board_length;
				} else if ((int) event.getX() >= displayWidth
						- componentBarsMoveBtnMinX - board_length + tempwidth) {
					board_left = x = displayWidth - componentBarsMoveBtnMinX
							- board_length;
					board_right = displayWidth - componentBarsMoveBtnMinX
							- board_length + board_length;
				} else {
					if (lastX > x) {
						if (ball_isRun) {
							// tempwidth = takebp.getWidth()/2;
							board_left = x = touchX - tempwidth;
							// x = touchX;
							board_right = touchX - tempwidth + board_length;
						}
					} else {
						if (ball_isRun) {
							board_left = x = touchX - tempwidth;
							// x = touchX;
							board_right = touchX - tempwidth + board_length;
						}
					}

					// if(board_left<=board_x_move)
					// {
					// board_left=0;
					// board_right=board_length;
					// }else{
					// board_left-=board_x_move;
					// board_right-=board_x_move;
					// }
					// }
				}

				takerect.offsetTo(x, y);
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			chk = false;
			if (st != null)
				st.interrupt();
		}
		return true;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// componentBarsMoveBtn.setX(x);
			// componentBarsMoveBtn.setY(y);
			// componentBarsMoveBtn.invalidate();
		}
	};

	public class ShowThread extends Thread {

		Canvas canvas;
		boolean flag = false;
		int span = 20;

		// constructor
		public ShowThread() {
			flag = true;
		}

		public void run() {
			while (flag) {
				try {
					Thread.sleep(span);
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
