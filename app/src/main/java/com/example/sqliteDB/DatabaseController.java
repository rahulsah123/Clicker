package com.example.sqliteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.model.ListOfPeopleData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseController extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="Clicker";
    public static final String CLICKS_COLUMN_NAME = "name";
    public static final String CLICKS_COLUMN_SCORE = "score";
    public static final String CLICKS_COLUMN_DATE = "score_at";

    public DatabaseController(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table clicks(id integer primary key,name text,score integer,score_at DATETIME);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clicks");
        onCreate(db);
    }


    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public boolean insertCliks (String name, Integer score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("score", score);
        contentValues.put("score_at", getDateTime());
        /*Log.v("Database1",""+contentValues.getAsString("name"));
        Log.v("Database2",contentValues.getAsString("score"));
        Log.v("Database3",""+contentValues.getAsString("score_at"));*/
        db.insert("clicks", null, contentValues);
        return true;
    }
public String getHighestScore() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res = db.rawQuery("select name,max(score) from clicks", null);
    res.moveToFirst();
    String results = ""+res.getString(res.getColumnIndex(CLICKS_COLUMN_NAME))+": "+res.getString(res.getColumnIndex("max(score)"));
    if (!results.equals("null: null"))
    return results;
    else
        return "Name: 0";
}



    public Cursor getData(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from clicks where name="+name+"", null );
        return res;
    }

    public Integer deleteClcks (String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("clicks",
                "name = ? ",
                new String[] { name });
    }

    public void deleteAllrec(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from clicks;");
    }

    public ArrayList<ListOfPeopleData> getAllClicks() {
        ArrayList<ListOfPeopleData> array_list = new ArrayList<ListOfPeopleData>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from clicks", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new ListOfPeopleData(res.getString(res.getColumnIndex(CLICKS_COLUMN_NAME)),res.getString(res.getColumnIndex(CLICKS_COLUMN_SCORE)),res.getString(res.getColumnIndex(CLICKS_COLUMN_DATE))));
            res.moveToNext();
        }
        return array_list;
    }

}
