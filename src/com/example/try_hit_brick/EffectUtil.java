package com.example.try_hit_brick;

import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.widget.GridView;

public class EffectUtil {
//	BallView ballView;
	BallView ballView;
	final int TYPE_NUM = 7;
	EffectType whichEffectType;
	int needHitCount;
	final int TIME_EFFECT_COUNT = 60;
	int timeCount = TIME_EFFECT_COUNT;
	ToolUtil toolUtil;
	BrickUtil brickUtil;
	boolean ironsCombo=false;
	
	enum EffectType { 
		Once, Twice, Three, Iron, Time, Tool, BallLevelUP 
	}
	
	public EffectUtil(BallView ballView, BrickUtil brickUtil){
		this.ballView = ballView;
		this.brickUtil = brickUtil;
//		setRandomEffectType();
	}
	
//	private void setRandomEffectType(){
//		Random random = new Random();
//		int whichType = random.nextInt(TYPE_NUM-1);
//		
//		EffectType[] effectTypes = EffectType.values();
//		whichEffectType = effectTypes[whichType];
//		setInit();
//	}
	
	public void setEffect(int whichType){
		EffectType[] effectTypes = EffectType.values();
		whichEffectType = effectTypes[whichType];
		switch (whichEffectType) {
		case Once:
			needHitCount = 1;
			break;
		case Twice:
			needHitCount = 2;
			break;
		case Three:
			needHitCount = 3;
			break;
		case Iron:
			needHitCount = -2;
			break;
		case Time:
			needHitCount = 1;
			break;
		case Tool:
			needHitCount = 1;
			break;
		case BallLevelUP:
			needHitCount = 1;
			break;
		}
	}
	
//	private void setInit(){
//		switch (whichEffectType) {
//		case Once:
//			needHitCount = 1;
//			break;
//		case Twice:
//			needHitCount = 2;
//			break;
//		case Three:
//			needHitCount = 3;
//			break;
//		case Iron:
//			needHitCount = -2;
//			break;
//		case Time:
//			needHitCount = 1;
//			break;
//		case Tool:
//			needHitCount = 1;
//			break;
//		case Weapen:
//			needHitCount = 1;
//			break;
//		}
//	}
	
	public void doEffect(){
		switch (whichEffectType) {
		case Once:
		case Twice:
		case Three:
			doHitDetermine();
			break;
		case Iron:
			if(ballView.getBallLevel()==2){
				needHitCount++;
				ironsCombo=true;
			}else{
				ironsCombo=false;
			}
			break;
		case Time:
			doHitDetermine();
			doTimeCountEffect();
			break;
		case Tool:
			doHitDetermine();
			setToolEffect();
			startDownTool();
			break;
		case BallLevelUP:
			doHitDetermine();
			doBallLevelUPEffect();
			break;
		}
	}
	
	private void doHitDetermine(){
		if(needHitCount>0){
			int hit = ballView.getBallLevel()>1 ? 2:ballView.getBallLevel()+1;
			if(needHitCount==2){
				ballView.hitBrickLevelDownCount += 1;
			}else if(needHitCount==3){
				ballView.hitBrickLevelDownCount += hit;
			}
			needHitCount = needHitCount-hit>0 ? needHitCount-hit:0;
		}
	}
	
	private void doTimeCountEffect(){
		
		handler.postDelayed(runnable, 1000);
	}
	
	Handler handler = new Handler();  
	Runnable runnable = new Runnable() {  
	    @Override  
	    public void run() {
	    	ballView.setBallTime(timeCount);
	    	if(timeCount>0){
	    		timeCount--;
	    		handler.postDelayed(this, 1000);
	    	}          
	    }  
	};
	
	private void setToolEffect(){
		toolUtil = new ToolUtil(ballView, brickUtil);
	}
	
//	private void downTool(){
//		toolUtil.getToolRect();
//	}
	
	private void doBallLevelUPEffect(){
		int ballLevel = ballView.getBallLevel()>2 ? -1:ballView.getBallLevel()+1;
		ballView.setBallLevel(ballLevel);
	}
	
	public int getNeedHitCount(){
		return needHitCount;
	}
	
	public boolean isHasTool(){
		return toolUtil!=null;
	}
	
	public ToolUtil getToolObj(){
		return toolUtil;
	}
	
	public void startDownTool(){
		toolUtil.isStartDownTool=true;
	}
	
//	class EffectOnce{
//		
//		private final static EffectOnce II = new EffectOnce();
//		
//		private EffectOnce(){}
//		
//		public static EffectOnce getInstance(){
//			return EFFECTONCE;
//		}
//		
//	}
}
