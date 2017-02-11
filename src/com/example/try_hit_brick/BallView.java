package com.example.try_hit_brick;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BallView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
	boolean gameFlag = true;
	
	int RADIUS = 20;
	int N = 3;
	int THICK_OF_STICK = 70;
	private int stickWidth;
	int ballLevel=0;
	int ballTime=0;
	
	Bundle b;

	float widthScreen;
	float heightScreen;
	private float imageX = -50.0f, imageY = -50.0f;
	private float fAngle;
	private float speedY = -15;
	private float speedX = -15;

//	public Rect[][] rBrick = new Rect[N][N];
	public BrickUtil[][] rBrick = new BrickUtil[N][N];
	public boolean bRbOn[][] = new boolean[N][N];
	int iNumBricks;
	Rect rStick = new Rect();
	Paint mPaint = new Paint();

	int[][] colors = { { Color.RED, Color.MAGENTA, Color.YELLOW },
			{ Color.GREEN, Color.BLUE, Color.CYAN },
			{ Color.BLACK, Color.DKGRAY, Color.GRAY } };
	private float deltaX = 0f;
	private boolean bStickTouched = false;
	public Thread mainLoop;

	Bitmap takebp;
	// Rect takerect;
	int x, y;
	int touchX = 0, touchY = 0;
	int tempwidth = 0;
	int tempheight = 0;
	boolean chk = false;
//	ImageView componentBarsMoveBtn;
//	int componentBarsMoveBtnMinX;

	
	boolean stickMoveingLeft = false;
	boolean stickMoveingRight = false;
	float ball_previous_y;
	
	ArrayList<ToolUtil> toolUtils = new ArrayList<ToolUtil>();
	
	int playGameLevel;
	Context context;
	
	int hitBrickLevelDownCount;
	int clearBrickCount;
	int comboCount = -1; 
	
	int score=0;
	final int BRICK_LEVEL_DOWN_SCORE = 300;
	final int BRICK_CLEAR_SCORE = 1000;
	final int BRICK_COMBO_SCORE = 100;
	
	SurfaceHolder mSurfaceHolder = null;
	
	// 實作MySurfaceView處理
	public BallView(Context context, int playGameLevel) {
		super(context);
		
		this.context = context;
		this.playGameLevel = playGameLevel;
		
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		iNumBricks = N * N;

		DisplayMetrics c = getResources().getDisplayMetrics();
		widthScreen = c.widthPixels;
		heightScreen = c.heightPixels - 50;

		this.initAngle();
		this.genSpeed();
		// 磚塊初始化
		for (int i = 0; i < rBrick.length; i++) {
			for (int j = 0; j < rBrick[i].length; j++) {
				bRbOn[i][j] = true;
//				rBrick[i][j] = new Rect();
				rBrick[i][j] = new BrickUtil(context, this);
				rBrick[i][j].set(playGameLevel, (int) widthScreen * j / N, (int) heightScreen
						* i / N / 3, (int) widthScreen * (j + 1) / N,
						(int) heightScreen * (i + 1) / N / 3);

			}
		}
		Log.d("ImageCollisionV2", (int) (widthScreen / 3) + ", "
				+ (int) (heightScreen - THICK_OF_STICK) + ", "
				+ (int) (widthScreen * 2 / 3) + ", " + (int) (heightScreen - 1));

//		componentBarsMoveBtn = new ImageView(context);
//		componentBarsMoveBtn.setImageResource(R.drawable.bar);


	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		widthScreen = w;
		heightScreen = h;

		//隨機產生球的位置
//		imageX = Math.round((w - 2 * RADIUS) * Math.random());
//		imageY = Math.round((h - 2 * RADIUS - N * rBrick[0][0].height())
//				* Math.random());
		
		//固定產生球的位置於擊板的上方
		imageX = w/2;
		imageY = h - THICK_OF_STICK - RADIUS;

//		imageX += RADIUS;
//		imageY += RADIUS + N * rBrick[0][0].height();

		// rStick.set(w/3, h-THICK_OF_STICK, w*2/3, h-1);

//		takebp = BitmapFactory.decodeResource(getResources(), R.drawable.bar);

		takebp = BitmapUtil.bar;
		
		// componentBarsMoveBtn = (ImageView) findViewById(R.id.imageView2);

		stickWidth = w / 3;
		rStick = new Rect(w / 3, h - THICK_OF_STICK, w * 2 / 3, h - 1);
		x = w / 3;
		y = h - THICK_OF_STICK;

		super.onSizeChanged(w, h, oldw, oldh);
	}

	// 不斷執行中，呼叫doDraw()
	void doDraw() {
		Canvas canvas = getHolder().lockCanvas();
		if (canvas != null) {
			canvas.drawColor(Color.WHITE);
			mPaint.setAntiAlias(true);
			// 繪出磚塊
			for (int i = 0; i < rBrick.length; i++) {
				for (int j = 0; j < rBrick[i].length; j++) {
					if (bRbOn[i][j]) {
//						mPaint.setColor(colors[i][j]);
//						canvas.drawRect(rBrick[i][j], mPaint);
						
						//有實心和空心的磚塊
//						mPaint.setStyle(Paint.Style.FILL);// 設置實心畫筆
//						mPaint.setColor(colors[i][j]);
//						canvas.drawRect(rBrick[i][j].left + 1, rBrick[i][j].top + 1, rBrick[i][j].right - 1,
//								rBrick[i][j].bottom - 1, mPaint);
//						mPaint.setStyle(Paint.Style.STROKE);// 設置空心畫筆
//						mPaint.setStrokeWidth(2);// 設置粗細
//						mPaint.setColor(Color.BLUE);
//						canvas.drawRect(rBrick[i][j].left + 3, rBrick[i][j].top + 3, rBrick[i][j].right - 3,
//								rBrick[i][j].bottom - 3, mPaint);
						
						//有空心的外框與實心圖片的磚塊
//						mPaint.setStyle(Paint.Style.FILL);// 設置實心畫筆
//						mPaint.setColor(colors[i][j]);
						canvas.drawRect(rBrick[i][j].left + 1, rBrick[i][j].top + 1, rBrick[i][j].right - 1,
								rBrick[i][j].bottom - 1, mPaint);
						canvas.drawBitmap(rBrick[i][j].getBrickBitmap(), null, rBrick[i][j].getRect(), null);
						mPaint.setStyle(Paint.Style.STROKE);// 設置空心畫筆
						mPaint.setStrokeWidth(2);// 設置粗細
						mPaint.setColor(Color.BLUE);
						canvas.drawRect(rBrick[i][j].left + 3, rBrick[i][j].top + 3, rBrick[i][j].right - 3,
								rBrick[i][j].bottom - 3, mPaint);
					} else {
//						mPaint.setColor(Color.WHITE);
//						canvas.drawRect(rBrick[i][j], mPaint);
					}
				}
			}
			
			for (ToolUtil toolUtil : toolUtils) {
				canvas.drawBitmap(toolUtil.toolBitmap, null, toolUtil.getToolRect(), null);
				toolUtil.moveDownToolObj();
				if(toolUtil.getToolRect().top>heightScreen){
					toolUtils.remove(toolUtil);
				}
			}
			
			// 繪出木棍
			// mPaint.setColor(Color.rgb(128, 64, 0));
			// canvas.drawRect(rStick, mPaint);
			canvas.drawBitmap(takebp, null, rStick, null);

			// 繪出圓球
			if(ballLevel!=-1){
				mPaint.setColor(Color.argb(128, 0, 0, 255));
				canvas.drawCircle(imageX, imageY, RADIUS, mPaint);
			}
			
			mPaint.setColor(Color.BLUE);
			mPaint.setTextSize(60);
			canvas.drawText(score+"", 100, 100, mPaint);

			getHolder().unlockCanvasAndPost(canvas);
		}
	}

	// 手勢觸控的監聽功能
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (x >= rStick.left && x <= rStick.right && y >= rStick.top
					&& y <= rStick.bottom) {
				deltaX = x - rStick.left;
				bStickTouched = true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			System.out.println("bStickTouched= " + bStickTouched);
			// if(bStickTouched && x - deltaX >= 0 && x - deltaX + widthScreen/3
			// <= widthScreen) {
			// rStick.left = (int) (x - deltaX);
			// rStick.right = (int) (rStick.left + widthScreen/3);
			// }
			if (bStickTouched) {
				if (bStickTouched && x - deltaX <= 0) {
					rStick.left = 0;
					rStick.right = (int) (rStick.left + stickWidth);
				} else if (bStickTouched
						&& x - deltaX + stickWidth >= widthScreen) {
					rStick.left = (int) (widthScreen - stickWidth);
					rStick.right = (int) widthScreen;
				} else {
					if(x - deltaX < rStick.left){
						stickMoveingLeft = true;
						stickMoveingRight = false;
					}else if(x - deltaX > rStick.left){
						stickMoveingLeft = false;
						stickMoveingRight = true;
					}else {
						stickMoveingLeft = false;
						stickMoveingRight = false;
					}
					 rStick.left = (int) (x - deltaX);
					 rStick.right = (int) (rStick.left + stickWidth);
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (bStickTouched){
				stickMoveingLeft = false;
				stickMoveingRight = false;
				bStickTouched = false;
			}
				
		}
		return true;
	}

	public void run() {
		while (gameFlag) {
			if(ballLevel!=-1){
				moving(); // 處理座標
			}	
			doDraw(); // 處理繪圖
//			if(!gameFlag){
//				endGame();
//			}
		}	
		mainLoop.interrupt();			
	}

	private synchronized void moving() {
		boolean aChanged = false;
		boolean bCollision = false;
		boolean bStickTouched = false;

		exit: for (int i = 0; i < rBrick.length; i++)
			for (int j = 0; j < rBrick[i].length; j++)
				if (bRbOn[i][j]) {
					CollisionLoc cl = isCollisionDetected(rBrick[i][j].getRect(), imageX,
							imageY, RADIUS);
					switch (cl) {
					case Left:
					case Right:
						this.speedX = (-1) * this.speedX;
//						bRbOn[i][j] = false;
						bCollision = true;
						
						rBrick[i][j].doHitEffect();
						if(rBrick[i][j].getEffect().isHasTool()){
							toolUtils.add(rBrick[i][j].getEffect().getToolObj());
						}
						if(!rBrick[i][j].isBrickExist()){
							clearBrickCount++;
							iNumBricks--;
						}
						if(!rBrick[i][j].isHitIronBrick())
							comboCount++;
						
						bRbOn[i][j] = rBrick[i][j].isBrickExist();
						break exit;
					case Top:
					case Bottom:
						this.speedY = (-1) * this.speedY;
//						bRbOn[i][j] = false;
						bCollision = true;
						comboCount++;
						
						rBrick[i][j].doHitEffect();
						if(rBrick[i][j].getEffect().isHasTool()){
							toolUtils.add(rBrick[i][j].getEffect().getToolObj());
						}
						if(!rBrick[i][j].isBrickExist()){
							clearBrickCount++;
							iNumBricks--;
						}
						bRbOn[i][j] = rBrick[i][j].isBrickExist();
						break exit;
					case CornerLT:
					case CornerRT:
					case CornerLB:
					case CornerRB:
						this.speedX = (-1) * this.speedX;
						this.speedY = (-1) * this.speedY;
						bCollision = true;
						comboCount++;
						
						rBrick[i][j].doHitEffect();
						if(rBrick[i][j].getEffect().isHasTool()){
							toolUtils.add(rBrick[i][j].getEffect().getToolObj());
						}
						if(!rBrick[i][j].isBrickExist()){
							clearBrickCount++;
							iNumBricks--;
						}
						bRbOn[i][j] = rBrick[i][j].isBrickExist();
						break exit;
					}
				}

		
		if(iNumBricks==0 || imageY-RADIUS>heightScreen){
			gameFlag = false;
			endGame();
			return;
		}
		
		if (bCollision){
			this.genAngle();
			imageX += this.speedX;
			imageY += this.speedY;
		}
			
		else {
			CollisionLoc cl = isCollisionDetected(rStick, imageX, imageY,
					RADIUS);
			switch (cl) {
			case Left:
			case Right:
				if(stickMoveingLeft && this.fAngle<270){
					this.speedX += (-5);
				}else if(stickMoveingLeft && this.fAngle>=270){
					this.speedX = (-1) * this.speedX;
					imageX += (-5);
				}else if(stickMoveingRight && this.fAngle<270){
					this.speedX = (-1) * this.speedX;
					imageX += 5;
				}else if(stickMoveingRight && this.fAngle>=270){
					this.speedX += 5;
				}else{
					this.speedX = (-1) * this.speedX;
				}
				bStickTouched = true;
				imageX += this.speedX;

				
				break;
			case Top:
				if(ball_previous_y - imageY <=0){
					this.speedY = (-1) * this.speedY;
				}
			case Bottom:
				bStickTouched = true;
				ball_previous_y = this.imageY;
				imageX += this.speedX;
				imageY += this.speedY;
				break;
			case CornerLT:
				getNewSpeedAfterHitCoener(getNewAngleAfterHitCoener(getHitCornerAngle(rStick.left, rStick.top, imageX, imageY)));
				break;
			case CornerRT:
				getNewSpeedAfterHitCoener(getNewAngleAfterHitCoener(getHitCornerAngle(rStick.right, rStick.top, imageX, imageY)));
			case CornerLB:
				getNewSpeedAfterHitCoener(getNewAngleAfterHitCoener(getHitCornerAngle(rStick.left, rStick.bottom, imageX, imageY)));
			case CornerRB:
//				this.speedX = (-1) * this.speedX;
//				this.speedY = (-1) * this.speedY;
				getNewSpeedAfterHitCoener(getNewAngleAfterHitCoener(getHitCornerAngle(rStick.right, rStick.top, imageX, imageY)));
				break;
			}
			
			if(cl != CollisionLoc.None){
				countScore();
				hitBrickLevelDownCount=0;
				clearBrickCount=0;
				comboCount=-1;
			}
			
			
			//
			if (bStickTouched)
				this.genAngle();
			else {
				
				for (ToolUtil toolUtil : toolUtils) {
					CollisionLoc collisionLoc = isCollisionDetected(toolUtil.getToolRect(), imageX, imageY, RADIUS);
					if(collisionLoc != CollisionLoc.None){
						toolUtil.doTool();
						toolUtils.remove(toolUtil);
					}
				}
				
				if (imageY - RADIUS < 0 /*
										 * || imageY + RADIUS > heightScreen
										 */) {
					this.speedY = (-1) * this.speedY;
					aChanged = true;
				}
				
				ball_previous_y = this.imageY;
				
				imageY += this.speedY;

				if (imageX - RADIUS < 0 || imageX + RADIUS > this.widthScreen) {
					this.speedX = (-1) * this.speedX;
					aChanged = true;
				}
				imageX += this.speedX;

				if (aChanged) {
					this.genAngle();
				}
			}
		}
	}

	private CollisionLoc isCollisionDetected(Rect rect, float fX, float fY,
			float fR) {
		// 站在圓心的立場，計算與矩形邊之間的最短 x 距離(不含四個角落點)，且跼限於 rect 的 top 與 bottom 之間
		float dx1 = Math.abs(fX - rect.left);
		float dx2 = Math.abs(fX - rect.right);
		float dx = dx1 < dx2 ? dx1 : dx2;

		if (fY >= rect.top && fY <= rect.bottom && dx <= fR) {
			if (dx1 < dx2)
				return CollisionLoc.Left;
			else
				return CollisionLoc.Right;
		}
		// 站在圓心的立場，計算與矩形邊之間的最短 y 距離(不含四個角落點)，且跼限於 rect 的 left 與 right 之間
		float dy1 = Math.abs(fY - rect.top);
		float dy2 = Math.abs(fY - rect.bottom);
		float dy = dy1 < dy2 ? dy1 : dy2;
		if (fX >= rect.left && fX <= rect.right && dy <= fR) {
			if (dy1 < dy2)
				return CollisionLoc.Top;
			else
				return CollisionLoc.Bottom;
		}
		// 計算四個角落點是否落在圓內
		Point[] pts = { new Point(rect.left, rect.top),
				new Point(rect.right, rect.top),
				new Point(rect.left, rect.bottom),
				new Point(rect.right, rect.bottom) };
		for (int i = 0; i < pts.length; i++)
			if ((pts[i].x - fX) * (pts[i].x - fX) + (pts[i].y - fY)
					* (pts[i].y - fY) <= fR * fR) {
				if (i == 0)
					return CollisionLoc.CornerLT;
				else if (i == 1)
					return CollisionLoc.CornerRT;
				else if (i == 2)
					return CollisionLoc.CornerLB;
				else
					return CollisionLoc.CornerRB;
			}
		return CollisionLoc.None;
	}

	public void initAngle() {
		fAngle = Math.round(360 * Math.random());
		// setTitle(fAngle+"度");
	}

	public void genSpeed() {
		this.speedX = (float) Math.cos(Math.toRadians(this.fAngle)) * (5);
		this.speedY = (float) Math.sin(Math.toRadians(this.fAngle)) * (5)
				* (-1);
	}
	
	public void getNewSpeedAfterHitCoener(float newAngleAfterHitCoener) {
		this.speedX = (float) Math.cos(Math.toRadians(newAngleAfterHitCoener)) * (5);
		this.speedY = (float) Math.sin(Math.toRadians(newAngleAfterHitCoener)) * (5)
				* (-1);
	}

	public void genAngle() {
		this.fAngle = (float) ((Math.atan2(this.speedY, (-1) * this.speedX) + Math.PI)
				/ Math.PI * 180);
		// setTitle(fAngle+"度");
	}

	public float getHitCornerAngle(int cornerX, int cornerY, float ballCenterX, float ballCenterY) {
//		this.fAngle = (float) ((Math.atan2(this.speedY, (-1) * this.speedX) + Math.PI)
//				/ Math.PI * 180);
//		float hitCornerAngle = ((float)(Math.atan((-1)*(cornerY-ballCenterY) / (cornerX-ballCenterX))/Math.PI *180));
		float hitCornerAngle = ((float)((Math.atan2( (-1) *(cornerY-ballCenterY), (cornerX-ballCenterX)))/Math.PI *180));
		if(hitCornerAngle<0){
			hitCornerAngle = 360 + hitCornerAngle;
		}	
		
//		float x = (float)(Math.atan(((-1)*(cornerY-ballCenterY)/(cornerX-ballCenterX))));
//		float hitCornerAngle = (float) (((float)((Math.atan2((-1)*(cornerY-ballCenterY), (cornerX-ballCenterX)))))*(180/Math.PI));
		return hitCornerAngle; 
	}
	
	public float getNewAngleAfterHitCoener(float hitCornerAngle){
		float newAngleAfterHitCoener = 0;
		if(this.fAngle - hitCornerAngle <=45){
			newAngleAfterHitCoener = this.fAngle - 180 - (this.fAngle - hitCornerAngle);
		}else{
			newAngleAfterHitCoener = this.fAngle + (this.fAngle - hitCornerAngle);
		}
		return newAngleAfterHitCoener;	
	}
	
	public 
	
	enum CollisionLoc { // 碰撞情形以 enum 宣告
		None, Left, Top, Right, Bottom, CornerLT, CornerRT, CornerLB, CornerRB
	}

	private CollisionLoc hitBoardCheck(Rect rect, float fX, float fY, float fR) {
		// 站在圓心的立場，計算與矩形邊之間的最短 x 距離(不含四個角落點)，且跼限於 rect 的 top 與 bottom 之間
		float dx1 = Math.abs(fX - rect.left);
		float dx2 = Math.abs(fX - rect.right);
		float dx = dx1 < dx2 ? dx1 : dx2;

		if (fY >= rect.top && fY <= rect.bottom && dx <= fR) {
			if (dx1 < dx2)
				return CollisionLoc.Left;
			else
				return CollisionLoc.Right;
		}
		// 站在圓心的立場，計算與矩形邊之間的最短 y 距離(不含四個角落點)，且跼限於 rect 的 left 與 right 之間
		float dy1 = Math.abs(fY - rect.top);
		float dy2 = Math.abs(fY - rect.bottom);
		float dy = dy1 < dy2 ? dy1 : dy2;
		if (fX >= rect.left && fX <= rect.right && dy <= fR) {
			if (dy1 < dy2)
				return CollisionLoc.Top;
			else
				return CollisionLoc.Bottom;
		}
		// 計算四個角落點是否落在圓內
		Point[] pts = { new Point(rect.left, rect.top),
				new Point(rect.right, rect.top),
				new Point(rect.left, rect.bottom),
				new Point(rect.right, rect.bottom) };
		for (int i = 0; i < pts.length; i++)
			if ((pts[i].x - fX) * (pts[i].x - fX) + (pts[i].y - fY)
					* (pts[i].y - fY) <= fR * fR) {
				if (i == 0)
					return CollisionLoc.CornerLT;
				else if (i == 1)
					return CollisionLoc.CornerRT;
				else if (i == 2)
					return CollisionLoc.CornerLB;
				else
					return CollisionLoc.CornerRB;
			}
		return CollisionLoc.None;
	}
	
	private void submitScore() {

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		final View submitTextView = layoutInflater.inflate(
				R.layout.rank_dialog, null);
		final EditText mNameEditText = (EditText) submitTextView
				.findViewById(R.id.editText1);
		final TextView scoreTextView = (TextView) submitTextView
				.findViewById(R.id.textView3);
		scoreTextView.setText("100");

		Button button = (Button) submitTextView.findViewById(R.id.button1);
		Button button2 = (Button) submitTextView.findViewById(R.id.button2);

		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setTitle(getResources().getString(R.string.success));
		// builder.setView(submitTextView);

		final Dialog dialog = new Dialog(context);
		dialog.setContentView(submitTextView);
		dialog.setCanceledOnTouchOutside(false);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				SQLiteHelper helper = new SQLiteHelper(context);
				final String name = mNameEditText.getText().toString();
				if (name.equals("") || name.trim().equals("")) {
					Toast.makeText(context,
							getResources().getString(R.string.cantnull),
							Toast.LENGTH_LONG).show();
					dialog.cancel();
					submitScore();
				} else {

					final String rank = helper.queryrank(String
							.valueOf(100));
					helper.insertData(name, 100,
							Integer.parseInt(rank) + 1);// 插入排行

					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle(getResources()
							.getString(R.string.recordOK));
					builder.setMessage(getResources().getString(
							R.string.countinue));
					builder.setPositiveButton(
							getResources().getString(R.string.retry),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									BallView ballView = new BallView(context, playGameLevel);
									MainActivity activity = (MainActivity) context;
									activity.setContentView(ballView);

								}
							});
					builder.show();
					dialog.cancel();
				}
			}
		});
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				MainActivity gameLevel = (MainActivity) context;
//				gameLevel.handler.sendEmptyMessage(0);
//				gameFlag = false;
//				dialog.cancel();
				((Activity)context).finish();
			}
		});
		dialog.show();
	}
	
	private void countScore(){
		score += hitBrickLevelDownCount*BRICK_LEVEL_DOWN_SCORE;
		score += clearBrickCount*BRICK_CLEAR_SCORE;
		score += comboCount*BRICK_COMBO_SCORE>0 ? comboCount*BRICK_COMBO_SCORE:0;
	}
	
	private void endGame(){
		handler.sendEmptyMessage(0);
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(iNumBricks==0){
				showGameSuccess();
			}else{
				showGameOver();
			}
		}
	};
	
	private void showGameSuccess(){

			SharedPreferences preferences = context
					.getSharedPreferences("user", Context.MODE_PRIVATE);
			int maxLevel = preferences.getInt("level", 0);
			if (maxLevel < 9 && maxLevel==playGameLevel) {
				Editor editor = preferences.edit();
				int lv = maxLevel + 1;
				editor.putInt("level", lv);
				editor.commit();
			}
			
			if ( playGameLevel == 3) {
				submitScore();
			} else {

				final Dialog dialog = new Dialog(context,
						R.style.Translucent_NoTitle);
				dialog.setContentView(R.layout.success_dialog);
				dialog.setCanceledOnTouchOutside(false);
				
				Button button = (Button) dialog
						.findViewById(R.id.button1);
				Button button2 = (Button) dialog
						.findViewById(R.id.button2);

				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MainActivity mainActivity = (MainActivity) context;
						mainActivity.handler.sendEmptyMessage(0);
						gameFlag = false;
						dialog.dismiss();
					}
				});

				button2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						BallView rv = new BallView(context,
								playGameLevel + 1);
						MainActivity activity = (MainActivity) context;
						activity.setContentView(rv);
						dialog.dismiss();
						System.gc();
					}
				});

				dialog.show();
			}

	}
	
	private void showGameOver(){
		final Dialog dialog = new Dialog(context,
				R.style.Translucent_NoTitle);
		dialog.setContentView(R.layout.gameover_dialog);
		dialog.setCanceledOnTouchOutside(false);
		
		Button button = (Button) dialog.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity gameLevel = (MainActivity) context;
				gameLevel.handler.sendEmptyMessage(0);
				gameFlag = false;
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void setStickLong(int stickWidth){
		this.stickWidth = stickWidth;
	}
	
	public int getStickLong(){
		return stickWidth;
	}
	
	public void setStickThick(int THICK_OF_STICK){
		this.THICK_OF_STICK = THICK_OF_STICK;
	}
	
	public int getStickThick(){
		return THICK_OF_STICK;
	}
	
	public void setBallRadius(int radius){
		this.RADIUS = radius;
	}
	
	public int getBallRadius(){
		return RADIUS;
	}
	
	public void setBallLevel(int ballLevel){
		this.ballLevel = ballLevel;
	}
	
	public int getBallLevel(){
		return ballLevel;
	}
	
	public void setBallTime(int ballTime){
		this.ballTime = ballTime;
	}
	
	public void setBallSpeed(float speedX, float speedY){
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	public float getBallSpeedX(){
		return speedX;
	}
	
	public float getBallSpeedY(){
		return speedY;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 以執行緒處理座標與繪圖
		mainLoop = new Thread(this);
		mainLoop.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
