package com.example.try_hit_brick;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{
	
	// ===========================================================
	// Constants
	// ===========================================================	
	private static final String TAG = "SQLiteHelper";
	
	private static final String DATABASE_NAME = "hit_brick.db";
	private static final String TABLE_NAME = "score";
    private static final int DATABASE_VERSION = 1;
	
	// ===========================================================
	// Fields
	// ===========================================================   
	private SQLiteDatabase mDB;
	
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	public SQLiteHelper(Context context) {			
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists " + TABLE_NAME + " ("
				 + "_id integer primary key autoincrement,"
				 + "_name varchar(50),"
				 + "_score integer,"
				 + "_rank integer)");
		
		Log.i(TAG, "onCreate"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
		Log.i(TAG, "onUpgrade");
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * add data
	 */
	public void insertData(String name,int score,int rank){
		mDB = getWritableDatabase();
		mDB.beginTransaction();
		ContentValues values = new ContentValues();
		values.put("_name", name);
		values.put("_score", score);
		values.put("_rank", rank);
		try{			
			mDB.insert(TABLE_NAME, "_id", values);	
			mDB.setTransactionSuccessful(); 
		}catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}finally{
			mDB.endTransaction();
			mDB.close();
		}
	}
	
	/**
	 * determine the name is exist or not.
	 * @param nameString
	 * @return
	 * @author Qingfeng
	 */
	public boolean isNameExist(String nameString){
		boolean flag = false;
		mDB = getReadableDatabase();	
		Cursor cursor = mDB.query(TABLE_NAME, null, "_name='"+nameString+"'", null, null, null, "_score desc","3");	
		int nameIndex = cursor.getColumnIndex("_name");
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			if(cursor.getString(nameIndex).equals("") ||cursor.getString(nameIndex) == null){
				flag = false;//不存在
			}else {
				flag = true;//已经存在
			}
		}
		cursor.close();	
		mDB.close();			
		return flag;
	}
	
	/**
	 * search
	 * @return
	 */
	public String queryData(){
		int i = 0;
		String result = "";
		mDB = getReadableDatabase();
		Cursor cursor = mDB.query(TABLE_NAME, null, null, null, null, null, "_score desc","3");
		int nameIndex = cursor.getColumnIndex("_name");	
		int scoreIndex = cursor.getColumnIndex("_score");
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			i += 1;
			result = result + i + "          ";
			result = result + cursor.getString(nameIndex) + "         ";
			result = result + cursor.getInt(scoreIndex) + "           \n";	
		}
		cursor.close();	
		mDB.close();
		return result;
	}
	
	/**
	 * 查詢插入資料的排名，並更新排在其之後的排名
	 * @param score
	 * @return
	 * @author Houzi
	 */
	public String queryrank(String score){
		String rank = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TABLE_NAME, null, " _score >= "+score , null, null, null, null, null);
		rank = String.valueOf(c.getCount());
		
		Cursor cc = db.query(TABLE_NAME, null, " _score < "+score , null, null, null, null, null);
		if(cc.getCount() > 0){
			int mName = cc.getColumnIndex("_name");
			int mScore = cc.getColumnIndex("_score");
			int mRank = cc.getColumnIndex("_rank");
			String aa ="";
			String bb ="";
			String ee ="";
			for(cc.moveToFirst();!(cc.isAfterLast());cc.moveToNext()){
				if(cc.getString(mName)!=null){
				aa=aa+","+ cc.getString(mName);
				bb =bb+ ","+String.valueOf(cc.getInt(mScore));
				ee =ee+","+String.valueOf(cc.getInt(mRank));
				}
			}
			String[] aaa=aa.split(",");
			String[] bbb=bb.split(",");
			String[] ddd=ee.split(",");
			for(int i=0;i<aaa.length;i++){
				if(aaa[i]!=null && aaa[i]!="" && aaa[i].length()>0){
					ContentValues values=new ContentValues();
					values.put("_name", aaa[i]);
					values.put("_score", Integer.parseInt(bbb[i]));
					values.put("_rank", Integer.parseInt(ddd[i])+1);
					db.update(TABLE_NAME, values, "_score = '"+bbb[i]+"'", null);
				}
			}			
		}
		c.close();	
		db.close();
		return rank;
	}

	/**
	 * 取得表中所有資料
	 * @return 返回的一個Cursor集，按照分數的降序排列
	 */
	public Cursor getListViewCursor() {
		Cursor cursor = null;
		try {
			mDB = getWritableDatabase();
			cursor = mDB.query(TABLE_NAME, null, null, null, null, null, "_score desc",null);

		} catch (Exception e) {
		}finally{
//			mDB.close();
		}		
		return cursor;
	}

	public void dbClose(){
		mDB.close();
	}
}
