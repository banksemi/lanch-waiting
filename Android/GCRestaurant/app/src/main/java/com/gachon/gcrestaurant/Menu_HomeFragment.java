package com.gachon.gcrestaurant;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Menu_HomeFragment extends Fragment {
    private ListViewHomeRestAdapter adapter;
    private ListView listView;
    private Button inputButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_home, null) ;

        listView = view.findViewById(R.id.menu_rest_list);
        adapter = new ListViewHomeRestAdapter();
        listView.setAdapter(adapter);
        //setListViewHeightBasedOnChildren(listView);


        adapter.addItem(new ListViewHomeRestAdapter.Item(1,"태평돈가스","15분","15m"));
        adapter.addItem(new ListViewHomeRestAdapter.Item(2,"호식당","25분","220m"));
        adapter.addItem(new ListViewHomeRestAdapter.Item(3,"룰루랄라","15분","150m"));
        adapter.addItem(new ListViewHomeRestAdapter.Item(4,"...","...","..."));

        inputButton = view.findViewById(R.id.inputbutton);
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final List<String> ListItems = new ArrayList<>();
                ListItems.add("없음 [0팀]");
                ListItems.add("조금 [1~3팀]");
                ListItems.add("많음 [4팀 이상]");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("대기시간 정보 입력");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        String selectedText = items[pos].toString();
                        Toast.makeText(getContext(), selectedText, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                            }
                        });

                builder.show();
            }
        });
        return view;
    }

}
