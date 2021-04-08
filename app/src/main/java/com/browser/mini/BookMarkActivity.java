package com.browser.mini;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMarkActivity extends AppCompatActivity {

    BookMarkDB db;
    MyDBHandler handler;

    BookMarklistAdapter adapter;

    private ListView listVIew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        listVIew = (ListView)findViewById(R.id.bookmarklist);

        MyDBHandler handler = new MyDBHandler(BookMarkActivity.this);

        List<Map<String, Object>> simpleData = new ArrayList<>();

        simpleData = handler.SelectAll();

        SimpleAdapter simpleAdapter = new SimpleAdapter(BookMarkActivity.this,
                simpleData,
                android.R.layout.simple_list_item_2,
                new String[]{"_name","_link"},
                new int[]{android.R.id.text1, android.R.id.text2}
                );


        listVIew.setAdapter(simpleAdapter);

        listVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView)parent;
                /*BookMarkDB bookmark = (BookMarkDB) listView.getItemAtPosition(position);
                String link=bookmark.get_link();*/
                HashMap<String, Object> tmp = (HashMap<String, Object>)parent.getItemAtPosition(position);
                String link = tmp.get("_link").toString();
                Log.e("확인",link);
                Intent intent = new Intent();
                intent.putExtra("getlink",link);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


}