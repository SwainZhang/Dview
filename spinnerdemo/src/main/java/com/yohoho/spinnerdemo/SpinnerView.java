package com.yohoho.spinnerdemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @time 2016/9/14 15:11
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class SpinnerView extends RelativeLayout implements View.OnClickListener {
    private EditText mEditText;
    private ImageView mImageView;
    private ListView mDataList;
    private List<String> content = new ArrayList<>();
    private GoogleApiClient mClient;
    private PopupWindow mPopupWindow;
    private MyAdapter mAdapter;

    public SpinnerView(Context context) {
        this(context,null);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context,R.layout.spinner_menu,this);
        mEditText = (EditText) findViewById(R.id.ed_input);

        mImageView = (ImageView) findViewById(R.id.iv_arrow);

        mImageView.setOnClickListener(this);
        for (int i = 0; i < 200; i++) {
            content.add("内容---" + i);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_arrow :
                clickArrow();//点击箭头应该弹出popupwindow
                break;
            default:
                break;
        }
    }

    private void clickArrow() {
        if(mPopupWindow!=null&&mPopupWindow.isShowing()){
            mPopupWindow.dismiss();

            mPopupWindow=null;
            return;
        }

        final View contentView = View.inflate(getContext(), R.layout.item_popupwindow, null);
        mDataList = (ListView) contentView.findViewById(R.id.popupwindow_listview);
        mDataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditText.setText(content.get(position));
            }
        });
        mAdapter = new MyAdapter();
        mDataList.setAdapter(mAdapter);
        mPopupWindow = new PopupWindow(contentView, mEditText.getWidth(), 280);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(mEditText);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (content != null) {
                return content.size();
            }
            return 0;
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getContext(), R.layout.item_popup_listview, null);
                holder.mDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holder.mUser = (ImageView) convertView.findViewById(R.id.iv_user);
                holder.mTextView= (TextView) convertView.findViewById(R.id.content);
                holder.mDelete.setOnClickListener(SpinnerView.this);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    content.remove(position);
                    mAdapter.notifyDataSetChanged();
                    if(!mEditText.getText().equals("")){
                        mEditText.setText("");
                    }
                }
            });
            holder.mTextView.setText(content.get(position));
            return convertView;
        }
    }

    class ViewHolder {
        ImageView mUser;
        TextView mTextView;
        ImageView mDelete;
    }

}
