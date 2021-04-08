package com.browser.mini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BookMarklistAdapter extends ArrayAdapter {

    Context context;

    ArrayList<BookMarkDB> list;

    LayoutInflater inflater;

    /*public BookMarklistAdapter(@NonNull Context context, ArrayList<BookMarkDB> list){
        this.context = context;
        this.list = list;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/
    public BookMarklistAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).get_name();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_book_mark, parent, false);
        }

        BookMarkDB list_item = (BookMarkDB)getItem(position);

        TextView name = (TextView)convertView.findViewById(R.id.bookname);
        //name.setText(list.get(position).get_name());
        name.setText(list_item.get_name());
        TextView link = (TextView)convertView.findViewById(R.id.booklink);
        link.setText(list_item.get_link());
        //link.setText(list.get(position).get_link());

        return convertView;
    }
}
