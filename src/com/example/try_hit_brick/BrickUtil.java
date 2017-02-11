package com.example.try_hit_brick;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.effect.Effect;

public class BrickUtil {
	Rect rect = new Rect();
	Bitmap bitmap;
	public int left, top, right, bottom;
	Context context;
	BallView ballView;
	EffectUtil effectUtil;
	int whichBrickType;
	
	public BrickUtil(Context context, BallView ballView){
		this.context = context;
		this.ballView = ballView;
	}
	
	public void set(int playGameLevel, int left, int top, int right, int bottom){
		rect.set(left, top, right,bottom);
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		Random random = new Random();
		if(playGameLevel == 0 || playGameLevel ==1)
			whichBrickType = random.nextInt(1);
		else if(playGameLevel == 2 || playGameLevel ==3){
			int temp = random.nextInt(3);
			if(temp ==0){
				whichBrickType = 1;
			}else if(temp ==1){
				whichBrickType = 5;
			}else {
				whichBrickType = 6;
			}
		}else if(playGameLevel == 4 || playGameLevel ==5){
			int temp = random.nextInt(4);
			if(temp ==0){
				whichBrickType = 2;
			}else if(temp ==1){
				whichBrickType = 3;
			}else if(temp ==2){
				whichBrickType = 5;
			}else {
				whichBrickType = 6;
			}
		}else if(playGameLevel == 6 || playGameLevel ==7){
			int temp = random.nextInt(6);
			if(temp ==0){
				whichBrickType = 0;
			}else if(temp ==1){
				whichBrickType = 1;
			}else if(temp ==2){
				whichBrickType = 2;
			}else if(temp ==3){
				whichBrickType = 4;
			}else if(temp ==4){
				whichBrickType = 5;
			}else {
				whichBrickType = 6;
			}
		}else if(playGameLevel == 8 || playGameLevel ==9){
			whichBrickType = random.nextInt(7);
		}
		
		
		setBitmapByBrickType(whichBrickType);
		setEffect(whichBrickType);
	}
	
	private void setBitmapByBrickType(int whichBrickType){
		switch (whichBrickType) {
		case 0:
			bitmap = BitmapUtil.brick_once_bmp;
			break;
		case 1:
			bitmap = BitmapUtil.brick_twice_bmp;
			break;
		case 2:
			bitmap = BitmapUtil.brick_three_bmp;
			break;
		case 3:
			bitmap = BitmapUtil.brick_iron_bmp;
			break;
		case 4:
			bitmap = BitmapUtil.brick_time_bmp;
			break;
		case 5:
			bitmap = BitmapUtil.brick_tool_bmp;
			break;
		case 6:
			bitmap = BitmapUtil.brick_ball_level_up_bmp;
			break;
		}
	}
	
	private void setEffect(int whichBrickType){
		effectUtil = new EffectUtil(ballView, this);
		effectUtil.setEffect(whichBrickType);
	}
	
	public EffectUtil getEffect(){
		return effectUtil;
	}
	
	public void doHitEffect(){
		effectUtil.doEffect();
	}
	
	public boolean isBrickExist(){
		return effectUtil.getNeedHitCount()!=0;
	}
	
	public Bitmap getBrickBitmap(){
		return bitmap;
	}
	
	public Rect getRect(){
		return rect;
	}
	
	public boolean isHitIronBrick(){
		if(whichBrickType == 3 && effectUtil.ironsCombo){
			return true;
		}
		return false;
	}
}
