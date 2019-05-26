package com.example.gcrestaurant;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

public class Menu_RestaurantDetail extends NetworkFragment{
    private ListViewAdapter adapter;

    public static Menu_RestaurantDetail newInstance(int no)
    {
        Menu_RestaurantDetail instance = new Menu_RestaurantDetail();

        Bundle bundle = new Bundle();
        bundle.putInt("no", no);
        instance.setArguments(bundle);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_detail, null) ;
        ListView listview = view.findViewById(R.id.menu_list);

        adapter = new ListViewAdapter();
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 1, "피자헛","삼마넌");
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 2, "피자헛","삼마넌");
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 3, "피자헛","삼마넌");
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 4, "피자헛","삼마넌");
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 5, "피자헛","삼마넌");
        adapter.addItem(getResources().getDrawable(R.drawable.doughnut), 6, "피자헛","삼마넌");
        listview.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listview);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String titleStr =  item.getItemName();
                Drawable iconDrawable = item.getIcon();
            }
        });

        int no = getArguments().getInt("no");
        NetworkService.SendMessage(PacketType.RestaurantInfo,"no", String.valueOf(no));
        return view;
    }
    @Override
    public void ReceivePacket(JSONObject json)
    {
        try
        {
            switch (json.getInt("type"))
            {
                case PacketType.RestaurantInfo:
                    SetText(R.id.rest_detail_title, json.getString("title"));
                    SetImage(R.id.rest_detail_image, json.getString("image"));
                    break;
            }
        }
        catch (Exception e)
        {

        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}