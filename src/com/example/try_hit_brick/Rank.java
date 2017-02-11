package com.example.try_hit_brick;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Rank extends Activity{
	ListView listView; 
	SQLiteHelper sqLiteHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.rank_listview);
		
		listView=(ListView)findViewById(R.id.listView1);
		
		sqLiteHelper = new SQLiteHelper(this);
		Cursor cursor = sqLiteHelper.getListViewCursor();
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.rank_listitem, cursor, new String[]{"_rank","_score","_name"}, new int[]{R.id.textView1, R.id.textView2, R.id.textView3 });
		listView.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		sqLiteHelper.close();
		super.onBackPressed();
	}
}
