package com.gachon.gcrestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    public static class ListViewItem {
        public String iconURL;
        public int no;
        public String item_name_str;
        public String item_price_str;
        public String desc;
        public ListViewItem(String icon, int no, String name, String price, String desc)
        {
            this.iconURL = icon;
            this.no = no;
            this.item_name_str = name;
            this.item_price_str = price;
            this.desc = desc;
        }
    }

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_menu_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.item_image) ;
        TextView itemNameTextView = (TextView) convertView.findViewById(R.id.item_name) ;
        TextView itemPriceTextView = (TextView) convertView.findViewById(R.id.item_price) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(listViewItem.iconURL).into(iconImageView);
        itemNameTextView.setText(listViewItem.item_name_str);
        itemPriceTextView.setText(listViewItem.item_price_str);


        // 버튼을 클릭헀을 때
       //convertView.setOnClickListener(new View.OnClickListener() {
       //     @Override
         //   public void onClick(View view) {
                //Intent 추가후 화면 전환하기
           //     Intent intent = new Intent(context, MessageViewActivity.class);
            //    intent.putExtra("no",((ListViewItem)getItem(pos)).getNo() );
            //    context.startActivity(intent);
          //  }
      //  });

      return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListViewItem item) {
        listViewItemList.add(item);
    }

}