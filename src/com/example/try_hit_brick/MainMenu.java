package com.example.try_hit_brick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mainmenu);
		
		Button startGameBtn = (Button)findViewById(R.id.button1);
		Button aboutGameBtn = (Button)findViewById(R.id.button2);
		Button rankGameBtn = (Button)findViewById(R.id.button3);
		Button exitGameBtn = (Button)findViewById(R.id.button4);
		
		startGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, GameMap.class);
				startActivity(intent);
//				finish();
			}
		});
		
		aboutGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, About.class);
				startActivity(intent);
			}
		});
		
		rankGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, Rank.class);
				startActivity(intent);
			}
		});
		
		exitGameBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
