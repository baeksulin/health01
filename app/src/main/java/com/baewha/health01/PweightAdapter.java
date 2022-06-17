package com.baewha.health01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PweightAdapter extends BaseAdapter {

    ArrayList<PweightList> pweightlist = new ArrayList<PweightList>();

    public PweightAdapter() {

    }

    @Override
    public int getCount() {
        return pweightlist.size();
    }

    @Override
    public Object getItem(int position) {
        return pweightlist.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final  Context context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_pweight, parent, false);
        }

        TextView tv_pweight = (TextView)convertView.findViewById(R.id.tv_pweight);

        PweightList plist = pweightlist.get(position);
        tv_pweight.setText("\t"+plist.getwDate()+"\t\t\t"+plist.getwPweight()+"kg");

        return convertView;
    }

    public void addItem(String wDate, String wPweight){
        PweightList plist = new PweightList();

        plist.setwDate(wDate);
        plist.setwPweight(wPweight);

        pweightlist.add(plist);

    }
}
