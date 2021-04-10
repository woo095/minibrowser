package com.browser.mini;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookMarkActivity extends AppCompatActivity {

    private boolean multiplechoicemode = false;

    private MyDBHandler handler;

    private Button btnadd, btndel;

    private ListView listVIew;

    private SimpleAdapter simpleAdapter, multipleAdapter;

    private List<Map<String, Object>> simpleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        listVIew = (ListView)findViewById(R.id.bookmarklist);
        btnadd = (Button)findViewById(R.id.btnbookadd);
        btndel = (Button)findViewById(R.id.btnbookdel);

        handler = new MyDBHandler(BookMarkActivity.this);


        simpleData = new ArrayList<>();

        simpleData = handler.SelectAll();

        simpleAdapter = new SimpleAdapter(BookMarkActivity.this,
                simpleData,
                android.R.layout.simple_list_item_1,
                new String[]{"_name","_link"},
                new int[]{android.R.id.text1, android.R.id.text2}
                );

        multipleAdapter = new SimpleAdapter(BookMarkActivity.this,
                simpleData,
                android.R.layout.simple_list_item_multiple_choice,
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
                if(multiplechoicemode==true){
                    return;
                }
                HashMap<String, Object> tmp = (HashMap<String, Object>)parent.getItemAtPosition(position);
                String link = tmp.get("_link").toString();
                //Log.e("확인",link);
                Intent intent = new Intent();
                intent.putExtra("getlink",link);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        listVIew.setOnItemLongClickListener((parent, view, position, id) -> {
            listVIew.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listVIew.setAdapter(multipleAdapter);
            multiplechoicemode = true;
            simpleAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),R.string.warndelbookmk,Toast.LENGTH_SHORT).show();
            return true;
        });


        btnadd.setOnClickListener(v -> {
            BookMarkDB bookmark = new BookMarkDB();
            bookmark.set_link(getIntent().getStringExtra("golink"));
            bookmark.set_name(getIntent().getStringExtra("goname"));
            handler.addItem(bookmark);
            simpleData = handler.SelectAll();
            simpleAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),R.string.warnsavbookmk,Toast.LENGTH_LONG).show();
            finish();
        });

        btndel.setOnClickListener(v -> {
            SparseBooleanArray checkedItems = listVIew.getCheckedItemPositions();
            int count = simpleAdapter.getCount();

            for(int i = count-1; i>=0;i--){
                if(checkedItems.get(i)){
                    //handler.deleteItem(checkedItems.get(i).);
                    HashMap<String, Object> tmp = (HashMap<String, Object>)listVIew.getItemAtPosition(i);
                    int id = Integer.parseInt(tmp.get("_id").toString());
                    handler.deleteItem(id);
                }
            }
            listVIew.setChoiceMode(ListView.CHOICE_MODE_NONE);
            listVIew.setAdapter(simpleAdapter);
            multiplechoicemode = false;
            simpleData = handler.SelectAll();
            simpleAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),R.string.warndelbookmksuccs,Toast.LENGTH_LONG).show();
        });
    }


}