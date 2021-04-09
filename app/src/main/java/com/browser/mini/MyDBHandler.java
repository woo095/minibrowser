package com.browser.mini;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDBHandler extends SQLiteOpenHelper {

    //생성자 - SQLiteOpenHelper가 Default Constructor가 없어서 반드시 생성
    public MyDBHandler(@Nullable Context context) {
        super(context, "item.db", null,1);
    }

    //데이터베이스가 없으면 호출되는 문구
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table bookmark(_id integer primary key autoincrement," +
                "_name text, _link text)");

        db.execSQL("insert into bookmark(_name, _link) " +
                "values('Google','http://google.com')");
    }

    //데이터베이스 버전이 변경도니 경우 호출되는 메소드
    //기존 테이블을 제거 후 새 테이블을 만든다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //테이블 제거
        db.execSQL("drop table bookmark");
        onCreate(db);
    }
    
    //데이터를 삽입하는 메소드
    public void addItem(BookMarkDB bookmark){
        //ContentValues를 이용한 삽입
        
        //삽입할 객체를 생성
        ContentValues row = new ContentValues();
        row.put("_name", bookmark.get_name());
        row.put("_link", bookmark.get_link());
        
        //데이터베이스에 접속해서 row 삽입
        SQLiteDatabase db = getWritableDatabase();
        db.insert("bookmark", null,row);
        db.close();
    }


    //itemname을 받아서 삭제하는 메소드
    public void deleteItem(int id){
        SQLiteDatabase db = getWritableDatabase();
        //?를 이용해서 파라미터를 바인딩 한후 SQL을 실행
       db.execSQL("delete from bookmark where _id = "+id);
    }

    public List SelectAll(){
        String query = "select * from bookmark";
        List<Map<String, Object>> simpleData = new ArrayList<>();

        Cursor cur = getReadableDatabase().rawQuery(query,null);
        if(cur!=null && cur.moveToFirst()){
            do {
                HashMap<String, Object> tmp = new HashMap<>();
                tmp.put("_id",cur.getInt(cur.getColumnIndex("_id")));
                tmp.put("_name",cur.getString(cur.getColumnIndex("_name")));
                tmp.put("_link",cur.getString(cur.getColumnIndex("_link")));
                simpleData.add(tmp);
            }while(cur.moveToNext());
        }
        return simpleData;
    }
}
