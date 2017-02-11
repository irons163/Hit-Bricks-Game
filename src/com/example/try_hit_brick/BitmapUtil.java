package com.example.try_hit_brick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {
	public static Bitmap bar;
	
	public static Bitmap brick_once_bmp;
	public static Bitmap brick_twice_bmp;
	public static Bitmap brick_three_bmp;
	public static Bitmap brick_iron_bmp;
	public static Bitmap brick_time_bmp;
	public static Bitmap brick_tool_bmp;
	public static Bitmap brick_ball_level_up_bmp;

	public static Bitmap tool_BallSpeedUp_bmp;
	public static Bitmap tool_BallSpeedDown_bmp;
	public static Bitmap tool_StickLongUp_bmp;
	public static Bitmap tool_StickLongDown_bmp;
	public static Bitmap tool_BallCountUpToThree_bmp;
	public static Bitmap tool_LifeUp_bmp;
	public static Bitmap tool_Weapen_bmp;
	public static Bitmap tool_BallReset_bmp;
	public static Bitmap tool_StickLongMax_bmp;
	public static Bitmap tool_BallRadiusUp_bmp;
	public static Bitmap tool_BallRadiusDown_bmp;
	public static Bitmap tool_BlackHole_bmp;
	public static Bitmap tool_BallLevelUpOnce_bmp;
	public static Bitmap tool_BallLevelUpTwice_bmp;
	
	public BitmapUtil(Context context) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = false;

		bar = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.bar, options);
		
		brick_once_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick01, options);
		brick_twice_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick02, options);
		brick_three_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick03, options);
		brick_iron_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick04, options);
		brick_time_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick05, options);
		brick_tool_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick06, options);
		brick_ball_level_up_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.brick07, options);
	
		tool_BallSpeedUp_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool01, options);
		tool_BallSpeedDown_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool02, options);
		tool_StickLongUp_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool03, options);
		tool_StickLongDown_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool04, options);
		tool_BallCountUpToThree_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool05, options);
		tool_LifeUp_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool06, options);
		tool_Weapen_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool07, options);
		tool_BallReset_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool08, options);
		tool_StickLongMax_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool09, options);
		tool_BallRadiusUp_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool10, options);
		tool_BallRadiusDown_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool11, options);
		tool_BlackHole_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool12, options);
		tool_BallLevelUpOnce_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool13, options);
		tool_BallLevelUpTwice_bmp = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.tool14, options);
	}

}
