package com.example.try_hit_brick;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class ToolUtil {
	BallView ballView;
	final int TYPE_NUM = 14;
	ToolType whichToolType;
	int toolObjLeft, toolObjTop;
	Bitmap toolBitmap;
	Rect toolRect = new Rect();
	boolean isStartDownTool = false;
	public final static int TOOL_DOWN_SPEED = 1;

	enum ToolType {
		BallSpeedUp, BallSpeedDown, StickLongUp, StickLongDown, BallCountUpToThree, LifeUp, Weapen, BallReset, StickLongMax, BallRadiusUp, BallRadiusDown, BlackHole, BallLevelUpOnce, BallLevelUpTwice
	}

	public ToolUtil(BallView ballView, BrickUtil brickUtil) {
		this.ballView = ballView;
		setRandomEffectType();
		setToolObjectXY(brickUtil.left, brickUtil.top, brickUtil.right,
				brickUtil.bottom);
		setToolBitmap();
	}

	private void setRandomEffectType() {
		Random random = new Random();
		int whichType = random.nextInt(TYPE_NUM);

		ToolType[] toolTypes = ToolType.values();
		whichToolType = toolTypes[whichType];
	}

	private void setToolObjectXY(int BrickLeft, int BrickTop, int BrickRight,
			int BrickBottom) {
		int height = BrickBottom - BrickTop;
		int width = height;
		toolObjTop = BrickTop;
		toolObjLeft = (BrickLeft + BrickRight - width) / 2;
		toolRect.set(toolObjLeft, toolObjTop, toolObjLeft + width, toolObjTop
				+ height);
	}

	private void setToolBitmap() {
		switch (whichToolType) {
		case BallSpeedUp:
			toolBitmap = BitmapUtil.tool_BallSpeedUp_bmp;
			break;
		case BallSpeedDown:
			toolBitmap = BitmapUtil.tool_BallSpeedDown_bmp;
			break;
		case StickLongUp:
			toolBitmap = BitmapUtil.tool_StickLongUp_bmp;
			break;
		case StickLongDown:
			toolBitmap = BitmapUtil.tool_StickLongDown_bmp;
			break;
		case BallCountUpToThree:
			toolBitmap = BitmapUtil.tool_BallCountUpToThree_bmp;
			break;
		case LifeUp:
			toolBitmap = BitmapUtil.tool_LifeUp_bmp;
			break;
		case Weapen:
			toolBitmap = BitmapUtil.tool_Weapen_bmp;
			break;
		case BallReset:
			toolBitmap = BitmapUtil.tool_BallReset_bmp;
			break;
		case StickLongMax:
			toolBitmap = BitmapUtil.tool_StickLongMax_bmp;
			break;
		case BallRadiusUp:
			toolBitmap = BitmapUtil.tool_BallRadiusUp_bmp;
			break;
		case BallRadiusDown:
			toolBitmap = BitmapUtil.tool_BallRadiusDown_bmp;
			break;
		case BlackHole:
			toolBitmap = BitmapUtil.tool_BlackHole_bmp;
			break;
		case BallLevelUpOnce:
			toolBitmap = BitmapUtil.tool_BallLevelUpOnce_bmp;
			break;
		case BallLevelUpTwice:
			toolBitmap = BitmapUtil.tool_BallLevelUpTwice_bmp;
			break;
		}
	}

	public void doTool() {
		switch (whichToolType) {
		case BallSpeedUp:
			ballView.setBallSpeed(ballView.getBallSpeedX() * 2,
					ballView.getBallSpeedY() * 2);
			break;
		case BallSpeedDown:
			ballView.setBallSpeed(ballView.getBallSpeedX() / 2,
					ballView.getBallSpeedY() / 2);
			break;
		case StickLongUp:
			ballView.setStickLong((int) (ballView.getStickLong() * 1.5));
			break;
		case StickLongDown:
			ballView.setStickLong((int) (ballView.getStickLong() * 0.5));
			break;
		case BallRadiusUp:
			ballView.setBallRadius((int) (ballView.getBallRadius() * 1.5));
			break;
		case BallRadiusDown:
			ballView.setBallRadius((int) (ballView.getBallRadius() * 0.5));
			break;
		}
	}

	public Rect getToolRect() {
		return toolRect;
	}

	public boolean isStartDownTool() {
		return isStartDownTool;
	}

	public void moveDownToolObj() {
		toolRect.offset(0, TOOL_DOWN_SPEED);
	}
}
