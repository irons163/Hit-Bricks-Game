package com.example.try_hit_brick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class Logo extends Activity{
	ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.logo);
		
		imageView=(ImageView)findViewById(R.id.imageView1);

		BitmapUtil bitmapUtil = new BitmapUtil(this);
		
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(3000);//设置动画持续时间

		animation.setRepeatCount(0);//设置重复次数
		animation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
//		animation.setStartOffset(2000);//执行前的等待时间

		imageView.setAnimation(animation);

		animation.startNow();

		AlphaAnimation animationFadeOut = new AlphaAnimation(1, 0);
		animationFadeOut.setDuration(2000);//设置动画持续时间

		animationFadeOut.setRepeatCount(0);//设置重复次数
		animationFadeOut.setFillAfter(true);//动画执行完后是否停留在执行完的状态
		animationFadeOut.setStartOffset(3000);//执行前的等待时间
		imageView.setAnimation(animationFadeOut);
		animationFadeOut.startNow();
		
		animationFadeOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Logo.this, MainMenu.class);
				startActivity(intent);
				finish();
			}
		});

	}
}
