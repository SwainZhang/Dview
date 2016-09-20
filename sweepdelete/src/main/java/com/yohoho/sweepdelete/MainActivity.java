package com.yohoho.sweepdelete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeLayout mSwipeLayout;
    private ListView mList;
    private MyAdapter mAdapter;
    private List<SwipeLayout> mSwipeLayouts=new ArrayList<>();
    private  List<String > mStrings=new ArrayList<>();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mList = (ListView) findViewById(R.id.list);
        mList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mAdapter = new MyAdapter();
        mList.setAdapter(mAdapter);
       for(String str:Cheeses.NAMES){
           mStrings.add(str);
       }

    }
             class  MyAdapter extends BaseAdapter{

                 @Override
                 public int getCount() {
                     return mStrings.size();
                 }

                 @Override
                 public Object getItem(int position) {
                     return null;
                 }

                 @Override
                 public long getItemId(int position) {
                     return 0;
                 }

                 @Override
                 public View getView(final int position, View convertView, final ViewGroup parent) {
                     ViewHolder holder=null;
                     if(convertView==null){
                         holder=new ViewHolder();
                         convertView= View.inflate(getApplicationContext(), R.layout.item_list,
                                 null);

                         holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                         holder.tv_call=(TextView) convertView.findViewById(R.id.tv_call);
                         holder.tv_del=(TextView) convertView.findViewById(R.id.tv_del);
                         convertView.setTag(holder);
                     }else{
                         holder = (ViewHolder) convertView.getTag();
                     }

                     holder.tv_name.setText(mStrings.get(position));
                     ((SwipeLayout)convertView).setOnStateChangedListener(new SwipeLayout.onStateChangedListener() {
                         @Override
                         public void onClosed(SwipeLayout mSwipeLayout) {

                         }

                         @Override
                         public void onOpened(SwipeLayout mSwipeLayout) {
                             mSwipeLayouts.add(mSwipeLayout);
                         }

                         @Override
                         public void onDraging(SwipeLayout mSwipeLayout) {
                            // Log.d("sliding_menu","onDraging");
                         }

                         @Override
                         public void onStartOpen(SwipeLayout mSwipeLayout) {
                             //Log.d("sliding_menu","onStartOpen");
                             if(mSwipeLayouts!=null){
                                  for(SwipeLayout layout:mSwipeLayouts){
                                          layout.close(true);
                                  }

                                 mSwipeLayouts.clear();
                             }
                         }

                         @Override
                         public void onStartClose(SwipeLayout mSwipeLayout) {
                             Log.d("sliding_menu","onStartClose");
                         }
                     });
                     holder.tv_call.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Toast.makeText(getApplicationContext(), "点击call", Toast
                                     .LENGTH_SHORT).show();
                         }
                     });
                     holder.tv_del.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Toast.makeText(getApplicationContext(), "点击delete", Toast
                                     .LENGTH_SHORT).show();
                             mStrings.remove(position);
                             notifyDataSetChanged();
                         }
                     });
                     return convertView;

                 }
             }

    class ViewHolder{
        TextView tv_name;
        TextView tv_call;
        TextView tv_del;
    }
}
