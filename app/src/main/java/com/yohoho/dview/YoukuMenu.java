package com.yohoho.dview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Administrator
 * @time 2016/9/14 11:52
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class YoukuMenu extends RelativeLayout {

    public YoukuMenu(Context context) {
        this(context,null);
    }

    public YoukuMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        //将xml和我们的view绑定
        View.inflate(context,R.layout.layout,this);
    }
}
